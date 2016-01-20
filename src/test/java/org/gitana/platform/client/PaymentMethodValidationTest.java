/**
 * Copyright 2016 Gitana Software, Inc.
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
 *   info@gitanasoftware.com
 */

package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.JSONBuilder;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.billing.BillingProviderConfiguration;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.services.billing.PaymentMethodValidation;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class PaymentMethodValidationTest extends AbstractTestCase
{
    public final static String NUMBER_VISA_GOOD = "4012000033330026";
    public final static String NUMBER_VISA_BAD = "41111111111111"; // two numbers short
    
    @Test
    public void test1()
        throws Exception
    {
        // authenticate as "admin"
        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // create a domain
        Domain domain = platform.createDomain();

        // create a principal
        DomainUser user = domain.createUser("test-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);

        // create a tenant for this principal (unlimited plan);
        Tenant tenant = registrar.createTenant(user, "unlimited");

        // get the client info
        ObjectNode defaultClientObject = tenant.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject);
        String clientKey = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_KEY);
        String clientSecret = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_SECRET);

        // authenticate as the tenant user
        gitana = new Gitana(clientKey, clientSecret);
        platform = gitana.authenticateOnTenant(user, TestConstants.TEST_PASSWORD, tenant);

        // create a billing provider configuration
        ObjectNode configObject = JsonUtil.createObject();
        configObject.put("environment", "SANDBOX");
        configObject.put("merchantId", "");
        configObject.put("publicKey", "");
        configObject.put("privateKey", "");
        BillingProviderConfiguration billingProviderConfiguration = platform.createBillingProviderConfiguration("braintree", configObject);
        assertNotNull(billingProviderConfiguration);
        
        // create a registrar with this billing provider configuration
        registrar = platform.createRegistrar(JSONBuilder.start(Registrar.FIELD_BILLING_PROVIDER_CONFIGURATION_ID).is(billingProviderConfiguration.getId()).get());
        
        // validate (GOOD)
        PaymentMethodValidation validation1 = registrar.validateCreditCard("Chuck Berry", NUMBER_VISA_GOOD, 1, 2017);
        assertTrue(validation1.isValid());
        assertTrue(validation1.listErrors().size() == 0);

        // validate (BAD NUMBER)
        PaymentMethodValidation validation2 = registrar.validateCreditCard("Chuck Berry", NUMBER_VISA_BAD, 1, 2017);
        assertFalse(validation2.isValid());
        assertTrue(validation2.listErrors().size() > 0);

        // validate (BAD MONTH)
        PaymentMethodValidation validation3 = registrar.validateCreditCard("Chuck Berry", NUMBER_VISA_GOOD, 13, 2017);
        assertFalse(validation3.isValid());
        assertTrue(validation3.listErrors().size() > 0);

        // validate (BAD YEAR - EXPIRED)
        PaymentMethodValidation validation4 = registrar.validateCreditCard("Chuck Berry", NUMBER_VISA_GOOD, 1, 1999);
        assertFalse(validation4.isValid());
        assertTrue(validation4.listErrors().size() > 0);
    }

}
