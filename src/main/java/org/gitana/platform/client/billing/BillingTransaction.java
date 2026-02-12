/**
 * Copyright 2026 Gitana Software, Inc.
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
 *   info@gitana.io
 */
package org.gitana.platform.client.billing;

import org.gitana.platform.GitanaObject;
import org.gitana.platform.client.tenant.Tenant;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author uzi
 */
public interface BillingTransaction extends GitanaObject
{
    // fields                      
    public final static String FIELD_ID = "id";

    public final static String FIELD_AMOUNT = "amount";
    public final static String FIELD_PAYMENT_METHOD_TYPE = "paymentMethodType"; // VISA, etc
    public final static String FIELD_PAYMENT_METHOD_NUMBER_MASKED = "paymentMethodNumberMasked";
    public final static String FIELD_CREATED_ON = "createdOn";
    public final static String FIELD_CURRENCY_ISO_CODE = "currencyIsoCode";

    public final static String FIELD_PLAN_KEY = "planKey";

    public final static String FIELD_TAX_AMOUNT = "taxAmount";

    public Tenant getTenant();
    public String getId();

    public void setAmount(BigDecimal amount);
    public BigDecimal getAmount();

    public void setPaymentMethodType(String paymentMethodType);
    public String getPaymentMethodType();

    public void setPaymentMethodNumberMasked(String paymentMethodNumberMasked);
    public String getPaymentMethodNumberMasked();

    public void setCreatedOn(Calendar createdOn);
    public Calendar getCreatedOn();

    public void setCurrencyIsoCode(String currencyIsoCode);
    public String getCurrencyIsoCode();

    public void setPlanKey(String planKey);
    public String getPlanKey();

    public void setTaxAmount(BigDecimal taxAmount);
    public BigDecimal getTaxAmount();

    /**
     * Downloads the receipt in PDF.
     *
     * @return attachment
     */
    public byte[] generateReceipt(String template);

}
