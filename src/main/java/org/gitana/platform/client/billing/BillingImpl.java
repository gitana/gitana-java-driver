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

package org.gitana.platform.client.billing;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.Map;

/**
 * @author uzi
 */
public class BillingImpl implements Billing
{
    private Platform platform;

    public BillingImpl(Platform platform)
    {
        this.platform = platform;
    }

    protected Driver getDriver()
    {
        return DriverContext.getDriver();
    }

    protected Remote getRemote()
    {
        return getDriver().getRemote();
    }
    
    protected String getResourceUri()
    {
        return "";
    }

    protected ObjectFactory getFactory()
    {
        return getDriver().getFactory();
    }

    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // PAYMENT METHODS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<PaymentMethod> listPaymentMethods()
    {
        return listPaymentMethods(null);
    }

    @Override
    public ResultMap<PaymentMethod> listPaymentMethods(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/billing/paymentmethods", params);
        return getFactory().paymentMethods(this, response);
    }

    @Override
    public ResultMap<PaymentMethod> queryPaymentMethods(ObjectNode query)
    {
        return queryPaymentMethods(query, null);
    }

    @Override
    public ResultMap<PaymentMethod> queryPaymentMethods(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/billing/paymentmethods/query", params, query);
        return getFactory().paymentMethods(this, response);
    }

    @Override
    public PaymentMethod readPaymentMethod(String paymentMethodId)
    {
        PaymentMethod paymentMethod = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/billing/paymentmethods/" + paymentMethodId);
            paymentMethod = getFactory().paymentMethod(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return paymentMethod;
    }

    @Override
    public PaymentMethod createPaymentMethod(String holderName, String number, int expirationMonth, int expirationYear)
    {
        ObjectNode object = JsonUtil.createObject();

        object.put(PaymentMethod.FIELD_HOLDER_NAME, holderName);
        object.put(PaymentMethod.FIELD_CARDNUMBER, number);
        object.put(PaymentMethod.FIELD_EXPIRATION_MONTH, expirationMonth);
        object.put(PaymentMethod.FIELD_EXPIRATION_YEAR, expirationYear);

        return createPaymentMethod(object);
    }

    @Override
    public PaymentMethod createPaymentMethod(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/billing/paymentmethods", object);

        String paymentMethodId = response.getId();
        return readPaymentMethod(paymentMethodId);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BILLING TRANSACTIONS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<BillingTransaction> listTransactions()
    {
        return listTransactions(null);
    }

    @Override
    public ResultMap<BillingTransaction> listTransactions(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/billing/transactions", params);
        return getFactory().billingTransactions(this, response);
    }

    @Override
    public ResultMap<BillingTransaction> queryTransactions(ObjectNode query)
    {
        return queryTransactions(query, null);
    }

    @Override
    public ResultMap<BillingTransaction> queryTransactions(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/billing/transactions/query", params, query);
        return getFactory().billingTransactions(this, response);
    }

    @Override
    public BillingTransaction readTransaction(String transactionId)
    {
        BillingTransaction transaction = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/billing/transactions/" + transactionId);
            transaction = getFactory().billingTransaction(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return transaction;
    }
}
