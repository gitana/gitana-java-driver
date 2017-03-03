/**
 * Copyright 2017 Gitana Software, Inc.
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

package org.gitana.platform.client.billing;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.GitanaObjectImpl;
import org.gitana.util.DateUtil;
import org.gitana.util.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author uzi
 */
public class BillingTransactionImpl extends GitanaObjectImpl implements BillingTransaction
{
    private Tenant tenant = null;

    public BillingTransactionImpl(Tenant tenant, ObjectNode obj)
    {
        super(obj);

        this.tenant = tenant;
    }

    protected String getResourceUri()
    {
        return "/registrars/" + tenant.getRegistrarId() + "/tenants/" + tenant.getId() + "/billing/transactions/" + getId();
    }

    protected Driver getDriver()
    {
        return DriverContext.getDriver();
    }

    protected Remote getRemote()
    {
        return getDriver().getRemote();
    }

    @Override
    public Tenant getTenant()
    {
        return tenant;
    }

    @Override
    public String getId()
    {
        return getString(FIELD_ID);
    }

    @Override
    public void setAmount(BigDecimal amount)
    {
        set(FIELD_AMOUNT, amount);
    }

    @Override
    public BigDecimal getAmount()
    {
        return getBigDecimal(FIELD_AMOUNT);
    }

    @Override
    public void setPaymentMethodType(String paymentMethodType)
    {
        set(FIELD_PAYMENT_METHOD_TYPE, paymentMethodType);
    }

    @Override
    public String getPaymentMethodType()
    {
        return getString(FIELD_PAYMENT_METHOD_TYPE);
    }

    @Override
    public void setPaymentMethodNumberMasked(String paymentMethodNumberMasked)
    {
        set(FIELD_PAYMENT_METHOD_NUMBER_MASKED, paymentMethodNumberMasked);
    }

    @Override
    public String getPaymentMethodNumberMasked()
    {
        return getString(FIELD_PAYMENT_METHOD_NUMBER_MASKED);
    }

    @Override
    public void setCreatedOn(Calendar createdOn)
    {
        set(FIELD_CREATED_ON, DateUtil.getTimestamp(createdOn));
    }

    @Override
    public Calendar getCreatedOn()
    {
        Calendar createdOn = null;

        if (has(FIELD_CREATED_ON))
        {
            createdOn = DateUtil.getTime(getObject(FIELD_CREATED_ON));
        }

        return createdOn;
    }

    @Override
    public void setCurrencyIsoCode(String currencyIsoCode)
    {
        set(FIELD_CURRENCY_ISO_CODE, currencyIsoCode);
    }

    @Override
    public String getCurrencyIsoCode()
    {
        return getString(FIELD_CURRENCY_ISO_CODE);
    }

    @Override
    public void setPlanKey(String planKey)
    {
        set(FIELD_PLAN_KEY, planKey);
    }

    @Override
    public String getPlanKey()
    {
        return getString(FIELD_PLAN_KEY);
    }

    @Override
    public void setTaxAmount(BigDecimal taxAmount)
    {
        set(FIELD_TAX_AMOUNT, taxAmount);
    }

    @Override
    public BigDecimal getTaxAmount()
    {
        return getBigDecimal(FIELD_TAX_AMOUNT);
    }

    @Override
    public byte[] generateReceipt(String template)
    {
        byte[] templateBytes = template.getBytes();
        ByteArrayInputStream templateIn = new ByteArrayInputStream(templateBytes);

        // build the uri
        String uri = getResourceUri() + "/receipt";

        HttpResponse response = null;
        byte[] bytes = null;
        try
        {
            response = getRemote().postData(uri, templateIn, templateBytes.length, MimeTypeMap.APPLICATION_FREEMARKER);
            if (response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 204)
            {
                InputStream in = response.getEntity().getContent();
                bytes = StreamUtil.getBytes(in);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
        finally
        {
            try { EntityUtils.consume(response.getEntity()); } catch (Exception ex) { }
        }

        return bytes;
    }
}
