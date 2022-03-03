/**
 * Copyright 2022 Gitana Software, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information, please contact Gitana Software, Inc. at this
 * address:
 *
 *   info@cloudcms.com
 */
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.billing.BillingProviderConfiguration;
import org.gitana.platform.client.billing.BillingTransaction;
import org.gitana.platform.client.billing.PaymentMethod;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.plan.Plan;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.services.payment.BillingSchedule;
import org.gitana.platform.services.plan.DataUnit;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author uzi
 */
public class BillingTest extends AbstractTestCase
{
    public final static String NUMBER_VISA_GOOD = "4012000033330026";
    public final static String NUMBER_VISA_BAD = "4111111111111115";

    @Test
    public void testSuccessCase1()
        throws Exception
    {
        // authenticate as "admin"
        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate("admin", "admin");

        // create using a valid CC
        Tenant tenant1 = createTenant(platform, NUMBER_VISA_GOOD);
        assertNotNull(tenant1);

        // check transactions (should be 1)
        ResultMap<BillingTransaction> transactions = tenant1.listBillingTransactions();
        assertEquals(1, transactions.size());
        
        // grab the transaction and generate a receipt
        BillingTransaction billingTransaction = transactions.values().iterator().next();
        String template = ClasspathUtil.loadFromClasspath("org/gitana/platform/client/billingTransactionReceipt.ftl");
        byte[] data = billingTransaction.generateReceipt(template);
        assertNotNull(data);
        assertTrue(data.length > 0);
    }

    @Test
    public void testFailureCase1()
        throws Exception
    {
        // authenticate as "admin"
        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate("admin", "admin");

        // create using an invalid CC
        Exception ex1 = null;
        Tenant tenant1 = null;
        try
        {
            tenant1 = createTenant(platform, NUMBER_VISA_BAD);
        }
        catch (Exception ex)
        {
            ex1 = ex;
        }
        assertNotNull(ex1);
        assertNull(tenant1);
    }
    
    
    private Tenant createTenant(Platform platform, String creditCardNumber)
    {
        // get the "bt" billing provider configuration
        BillingProviderConfiguration bt = platform.readBillingProviderConfiguration("bt");
        if (bt == null)
        {
            ObjectNode object = JsonUtil.createObject();
            object.put(BillingProviderConfiguration.FIELD_KEY, "bt");
            object.put("environment", "SANDBOX");
            object.put("merchantId", "37ptjdc5y796vh8s");
            object.put("publicKey", "hch8x42twgtcxnjz");
            object.put("privateKey", "z8bwfqknqh2cs848");

            bt = platform.createBillingProviderConfiguration("braintree", object);
        }
        
        // create registrar + domain
        ObjectNode registrarObject = JsonUtil.createObject();
        registrarObject.put(Registrar.FIELD_BILLING_PROVIDER_CONFIGURATION_ID, bt.getKey());
        Registrar registrar = platform.createRegistrar(registrarObject);
        Domain domain = platform.createDomain();
        
        // create a plan
        Plan plan = registrar.createPlan("test");
        plan.setBillingPrice(new BigDecimal("999"));
        plan.setBillingSchedule(BillingSchedule.MONTHLY);
        plan.setRequiresBilling(true);
        plan.setStoragePrice(new BigDecimal(0.15));
        plan.setStorageUnit(DataUnit.GB);
        plan.setStorageMax(-1);
        plan.setStorageBillingRequired(true);
        plan.setStorageBillingKey("storage");
        plan.setTransferOutPrice(new BigDecimal(0.15));
        plan.setTransferOutUnit(DataUnit.GB);
        plan.setTransferOutMax(-1);
        plan.setTransferOutBillingRequired(true);
        plan.setTransferOutBillingKey("transferout");
        plan.update();

        // create a new principal
        String userName = "user-" + System.currentTimeMillis();
        DomainUser user = domain.createUser(userName, TestConstants.TEST_PASSWORD);

        // define a payment method
        ObjectNode paymentMethodObject = JsonUtil.createObject();
        paymentMethodObject.put(PaymentMethod.FIELD_NUMBER, creditCardNumber);
        paymentMethodObject.put(PaymentMethod.FIELD_HOLDER_NAME, "Bruce Springsteen");
        paymentMethodObject.put(PaymentMethod.FIELD_EXPIRATION_MONTH, 12);
        paymentMethodObject.put(PaymentMethod.FIELD_EXPIRATION_YEAR, 2020);

        // create a new tenant
        // this should succeed if the credit card successfully billed
        // if there was a validation error with the credit card or the credit card could not process
        // for any reason, we should get an error back
        ObjectNode tenantObject = JsonUtil.createObject();
        Tenant tenant = registrar.createTenant(user, "test", tenantObject, paymentMethodObject);

        /*
        // authenticate on the new tenant as the new principal
        ObjectNode defaultClientObject = tenant.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject);
        String clientKey = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_KEY);
        String clientSecret = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_SECRET);
        gitana = new Gitana(clientKey, clientSecret);
        platform = gitana.authenticateOnTenant(user, TestConstants.TEST_PASSWORD, tenant);
        */
        
        return tenant;
    }

}
