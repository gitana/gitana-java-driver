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

package org.gitana.platform.client.plan;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.registrar.AbstractRegistrarDocumentImpl;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.services.payment.BillingSchedule;
import org.gitana.platform.services.plan.DataUnit;

import java.math.BigDecimal;

/**
 * @author uzi
 */
public class PlanImpl extends AbstractRegistrarDocumentImpl implements Plan
{
    public PlanImpl(Registrar registrar, ObjectNode obj, boolean isSaved)
    {
        super(registrar, obj, isSaved);
    }

    @Override
    protected String getResourceUri()
    {
        return "/registrars/" + getRegistrarId() + "/plans/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Plan)
        {
            Plan other = (Plan) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELFABLE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put(getResourceUri(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete(getResourceUri());
    }

    @Override
    public void reload()
    {
        Plan plan = getRegistrar().readPlan(getId());

        this.reload(plan.getObject());
    }
    

    ///////////////////////////////////////////


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
    public boolean getRequiresBilling()
    {
        return getBoolean(FIELD_REQUIRES_BILLING);
    }

    @Override
    public void setRequiresBilling(boolean requiresBilling)
    {
        set(FIELD_REQUIRES_BILLING, requiresBilling);
    }

    @Override
    public BillingSchedule getBillingSchedule()
    {
        BillingSchedule paymentSchedule = null;

        if (has(FIELD_BILLING_SCHEDULE))
        {
            paymentSchedule = BillingSchedule.valueOf(getString(FIELD_BILLING_SCHEDULE));
        }

        return paymentSchedule;
    }

    @Override
    public void setBillingSchedule(BillingSchedule paymentSchedule)
    {
        set(FIELD_BILLING_SCHEDULE, paymentSchedule.toString());
    }

    @Override
    public BigDecimal getBillingPrice()
    {
        return getBigDecimal(FIELD_BILLING_PRICE);
    }

    @Override
    public void setBillingPrice(BigDecimal paymentPrice)
    {
        set(FIELD_BILLING_PRICE, paymentPrice);
    }

    @Override
    public long getStorageAmount()
    {
        return getLong(FIELD_STORAGE_AMOUNT);
    }

    @Override
    public void setStorageAmount(long storageAmount)
    {
        set(FIELD_STORAGE_AMOUNT, storageAmount);
    }

    @Override
    public DataUnit getStorageUnit()
    {
        DataUnit dataUnit = null;

        if (has(FIELD_STORAGE_UNIT))
        {
            dataUnit = DataUnit.valueOf(getString(FIELD_STORAGE_UNIT));
        }

        return dataUnit;
    }

    @Override
    public void setStorageUnit(DataUnit storageUnit)
    {
        set(FIELD_STORAGE_UNIT, storageUnit.toString());
    }

    @Override
    public BigDecimal getStorageOveragePrice()
    {
        return getBigDecimal(FIELD_STORAGE_OVERAGE_PRICE);
    }

    @Override
    public void setStorageOveragePrice(BigDecimal storageOveragePrice)
    {
        set(FIELD_STORAGE_OVERAGE_PRICE, storageOveragePrice);
    }

    @Override
    public DataUnit getStorageOverageUnit()
    {
        DataUnit dataUnit = null;

        if (has(FIELD_STORAGE_OVERAGE_UNIT))
        {
            dataUnit = DataUnit.valueOf(getString(FIELD_STORAGE_OVERAGE_UNIT));
        }

        return dataUnit;
    }

    @Override
    public void setStorageOverageUnit(DataUnit storageOverageUnit)
    {
        set(FIELD_STORAGE_OVERAGE_UNIT, storageOverageUnit.toString());
    }

    @Override
    public long getTransferAmount()
    {
        return getLong(FIELD_TRANSFER_AMOUNT);
    }

    @Override
    public void setTransferAmount(long transferAmount)
    {
        set(FIELD_TRANSFER_AMOUNT, transferAmount);
    }

    @Override
    public DataUnit getTransferUnit()
    {
        DataUnit dataUnit = null;

        if (has(FIELD_TRANSFER_UNIT))
        {
            dataUnit = DataUnit.valueOf(getString(FIELD_TRANSFER_UNIT));
        }

        return dataUnit;
    }

    @Override
    public void setTransferUnit(DataUnit transferUnit)
    {
        set(FIELD_TRANSFER_UNIT, transferUnit.toString());
    }

    @Override
    public BigDecimal getTransferOveragePrice()
    {
        return getBigDecimal(FIELD_TRANSFER_OVERAGE_PRICE);
    }

    @Override
    public void setTransferOveragePrice(BigDecimal transferOveragePrice)
    {
        set(FIELD_TRANSFER_OVERAGE_PRICE, transferOveragePrice);
    }

    @Override
    public DataUnit getTransferOverageUnit()
    {
        DataUnit dataUnit = null;

        if (has(FIELD_TRANSFER_OVERAGE_UNIT))
        {
            dataUnit = DataUnit.valueOf(getString(FIELD_TRANSFER_OVERAGE_UNIT));
        }

        return dataUnit;
    }

    @Override
    public void setTransferOverageUnit(DataUnit transferOverageUnit)
    {
        set(FIELD_TRANSFER_OVERAGE_UNIT, transferOverageUnit.toString());
    }

    @Override
    public long getDatastoreAmount()
    {
        return getLong(FIELD_DATASTORE_AMOUNT);
    }

    @Override
    public void setDatastoreAmount(long datastoreAmount)
    {
        set(FIELD_DATASTORE_AMOUNT, datastoreAmount);
    }

    @Override
    public long getObjectAmount()
    {
        return getLong(FIELD_OBJECT_AMOUNT);
    }

    @Override
    public void setObjectAmount(long objectAmount)
    {
        set(FIELD_OBJECT_AMOUNT, objectAmount);
    }

    @Override
    public long getCollaboratorAmount()
    {
        return getLong(FIELD_COLLABORATOR_AMOUNT);
    }

    @Override
    public void setCollaboratorAmount(long collaboratorAmount)
    {
        set(FIELD_COLLABORATOR_AMOUNT, collaboratorAmount);
    }


}
