/**
 * Copyright 2010 Gitana Software, Inc.
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

package org.gitana.platform.client.tenant;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.billing.BillingTransaction;
import org.gitana.platform.client.billing.PaymentMethod;
import org.gitana.platform.client.meter.Meter;
import org.gitana.platform.client.registrar.RegistrarDocument;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.services.meter.MeterType;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Tenant extends RegistrarDocument, Selfable
{
    // fields
    public final static String FIELD_PLAN_KEY = "planKey";
    public final static String FIELD_PRINCIPAL_ID = "principalId";
    public final static String FIELD_DOMAIN_ID = "domainId";

    public final static String FIELD_PLATFORM_ID = "platformId";

    // subscription (for recurring billing)
    public final static String FIELD_BILLING_SUBSCRIPTION_ID = "billingSubscriptionId";
    public final static String FIELD_BILLING_PAYMENT_METHOD_ID = "billingPaymentMethodId";

    // tenant dns key
    public final static String FIELD_DNS_SLUG = "dnsSlug";


    public void setPlanKey(String planKey);
    public String getPlanKey();

    public void setPrincipalId(String principalId);
    public String getPrincipalId();

    public void setDomainId(String domainId);
    public String getDomainId();

    public void setPlatformId(String platformId);
    public String getPlatformId();

    // tenant dns key
    public void setDnsSlug(String dnsSlug);
    public String getDnsSlug();

    public void closeOut();


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Allocations
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<ObjectNode> listAllocatedObjects();
    public ResultMap<ObjectNode> listAllocatedObjects(Pagination pagination);
    public ResultMap<ObjectNode> listAllocatedObjects(String objectType);
    public ResultMap<ObjectNode> listAllocatedObjects(String objectType, Pagination pagination);

    public ResultMap<ObjectNode> listAllocatedRepositoryObjects();
    public ResultMap<ObjectNode> listAllocatedRepositoryObjects(Pagination pagination);

    public ResultMap<ObjectNode> listAllocatedDomainObjects();
    public ResultMap<ObjectNode> listAllocatedDomainObjects(Pagination pagination);

    public ResultMap<ObjectNode> listAllocatedVaultObjects();
    public ResultMap<ObjectNode> listAllocatedVaultObjects(Pagination pagination);

    public ResultMap<ObjectNode> listAllocatedClientObjects();
    public ResultMap<ObjectNode> listAllocatedClientObjects(Pagination pagination);

    public ResultMap<ObjectNode> listAllocatedRegistrarObjects();
    public ResultMap<ObjectNode> listAllocatedRegistrarObjects(Pagination pagination);

    public ResultMap<ObjectNode> listAllocatedStackObjects();
    public ResultMap<ObjectNode> listAllocatedStackObjects(Pagination pagination);

    public ResultMap<ObjectNode> listAllocatedDirectoryObjects();
    public ResultMap<ObjectNode> listAllocatedDirectoryObjects(Pagination pagination);

    public ResultMap<ObjectNode> listAllocatedApplicationObjects();
    public ResultMap<ObjectNode> listAllocatedApplicationObjects(Pagination pagination);

    /**
     * Administrative method that will only work for the "owner" of the tenant.  Otherwise will return null.
     *
     * @return default client configuration
     */
    public ObjectNode readDefaultAllocatedClientObject();


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BILLING
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setBillingSubscriptionId(String billingSubscriptionId);
    public String getBillingSubscriptionId();

    public void setBillingMethodPaymentId(String billingPaymentMethodId);
    public String getBillingMethodPaymentId();


    // PAYMENT METHODS
    public ResultMap<PaymentMethod> listPaymentMethods();
    public ResultMap<PaymentMethod> listPaymentMethods(Pagination pagination);
    public ResultMap<PaymentMethod> queryPaymentMethods(ObjectNode query);
    public ResultMap<PaymentMethod> queryPaymentMethods(ObjectNode query, Pagination pagination);
    public PaymentMethod readPaymentMethod(String paymentMethodId);
    public PaymentMethod createPaymentMethod(String holderName, String number, int expirationMonth, int expirationYear);
    public PaymentMethod createPaymentMethod(ObjectNode object);

    // BILLING TRANSACTIONS
    public ResultMap<BillingTransaction> listBillingTransactions();
    public ResultMap<BillingTransaction> listBillingTransactions(Pagination pagination);
    public ResultMap<BillingTransaction> queryBillingTransactions(ObjectNode query);
    public ResultMap<BillingTransaction> queryBillingTransactions(ObjectNode query, Pagination pagination);
    public BillingTransaction readBillingTransaction(String transactionId);


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // METERS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Meter> listMeters();
    public ResultMap<Meter> listMeters(Pagination pagination);
    public ResultMap<Meter> queryMeters(ObjectNode query);
    public ResultMap<Meter> queryMeters(ObjectNode query, Pagination pagination);
    public Meter readCurrentMeter(MeterType meterType);


}
