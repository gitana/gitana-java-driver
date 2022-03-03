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
package org.gitana.platform.client.plan;

import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.registrar.AbstractRegistrarDocumentImpl;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.services.payment.BillingSchedule;
import org.gitana.platform.services.plan.DataUnit;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

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
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_PLAN;
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


    private boolean _has(String objectFieldId, String fieldId)
    {
        boolean has = false;

        ObjectNode object = getObject(objectFieldId);
        if (object != null && object.has(fieldId))
        {
            has = true;
        }

        return has;
    }

    private String _getString(String objectFieldId, String fieldId)
    {
        String value = null;

        ObjectNode object = getObject(objectFieldId);
        if (object != null && object.has(fieldId))
        {
            value = object.get(fieldId).textValue();
        }

        return value;
    }

    private BigDecimal _getBigDecimal(String objectFieldId, String fieldId)
    {
        BigDecimal big = null;

        ObjectNode object = getObject(objectFieldId);
        if (object != null && object.has(fieldId))
        {
            Object value = object.get(fieldId);
            if (value != null)
            {
                if (value instanceof NumericNode)
                {
                    big = ((NumericNode)value).decimalValue();
                }
            }
        }

        return big;
    }

    private int _getInt(String objectFieldId, String fieldId)
    {
        int value = -1;

        ObjectNode object = getObject(objectFieldId);
        if (object != null && object.has(fieldId))
        {
            value = object.get(fieldId).intValue();
        }

        return value;
    }

    private boolean _getBoolean(String objectFieldId, String fieldId)
    {
        boolean value = false;

        ObjectNode object = getObject(objectFieldId);
        if (object != null && object.has(fieldId))
        {
            value = object.get(fieldId).booleanValue();
        }

        return value;
    }

    private long _getLong(String objectFieldId, String fieldId)
    {
        long value = -1;

        ObjectNode object = getObject(objectFieldId);
        if (object != null && object.has(fieldId))
        {
            value = object.get(fieldId).longValue();
        }

        return value;
    }

    private void _set(String objectFieldId, String fieldId, Object value)
    {
        ObjectNode object = getObject(objectFieldId);
        if (object == null)
        {
            object = JsonUtil.createObject();
            set(objectFieldId, object);
        }

        JsonUtil.objectPut(object, fieldId, value);
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
        BillingSchedule schedule = null;

        if (_has(FIELD_BASE, FIELD_BASE_SCHEDULE))
        {
            schedule = BillingSchedule.valueOf(_getString(FIELD_BASE, FIELD_BASE_SCHEDULE));
        }

        return schedule;
    }

    @Override
    public void setBillingSchedule(BillingSchedule schedule)
    {
        _set(FIELD_BASE, FIELD_BASE_SCHEDULE, schedule.toString());
    }

    @Override
    public BigDecimal getBillingPrice()
    {
        return _getBigDecimal(FIELD_BASE, FIELD_BASE_PRICE);
    }

    @Override
    public void setBillingPrice(BigDecimal paymentPrice)
    {
        _set(FIELD_BASE, FIELD_BASE_PRICE, paymentPrice);
    }

    @Override
    public long getStorageAllowance()
    {
        return _getLong(FIELD_STORAGE, FIELD_STORAGE_ALLOWANCE);
    }

    @Override
    public void setStorageAllowance(long storageAllowance)
    {
        _set(FIELD_STORAGE, FIELD_STORAGE_ALLOWANCE, storageAllowance);
    }

    @Override
    public DataUnit getStorageUnit()
    {
        DataUnit dataUnit = null;

        if (_has(FIELD_STORAGE, FIELD_STORAGE_UNIT))
        {
            dataUnit = DataUnit.valueOf(_getString(FIELD_STORAGE, FIELD_STORAGE_UNIT));
        }

        return dataUnit;
    }

    @Override
    public void setStorageUnit(DataUnit storageUnit)
    {
        _set(FIELD_STORAGE, FIELD_STORAGE_UNIT, storageUnit.toString());
    }

    @Override
    public BigDecimal getStoragePrice()
    {
        return _getBigDecimal(FIELD_STORAGE, FIELD_STORAGE_PRICE);
    }

    @Override
    public void setStoragePrice(BigDecimal storagePrice)
    {
        _set(FIELD_STORAGE, FIELD_STORAGE_PRICE, storagePrice);
    }

    @Override
    public String getStorageBillingKey()
    {
        return _getString(FIELD_STORAGE, FIELD_STORAGE_BILLING_KEY);
    }

    @Override
    public void setStorageBillingKey(String storageBillingKey)
    {
        _set(FIELD_STORAGE, FIELD_STORAGE_BILLING_KEY, storageBillingKey);
    }

    @Override
    public boolean getStorageBillingRequired()
    {
        return _getBoolean(FIELD_STORAGE, FIELD_STORAGE_REQUIRES_BILLING);
    }

    @Override
    public void setStorageBillingRequired(boolean storageBillingRequired)
    {
        _set(FIELD_STORAGE, FIELD_STORAGE_REQUIRES_BILLING, storageBillingRequired);
    }

    @Override
    public long getStorageMax()
    {
        return _getLong(FIELD_STORAGE, FIELD_STORAGE_MAX);
    }

    @Override
    public void setStorageMax(long storageMax)
    {
        _set(FIELD_STORAGE, FIELD_STORAGE_MAX, storageMax);
    }

    @Override
    public long getTransferOutAllowance()
    {
        return _getLong(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_ALLOWANCE);
    }

    @Override
    public void setTransferOutAllowance(long transferOutAllowance)
    {
        _set(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_ALLOWANCE, transferOutAllowance);
    }

    @Override
    public DataUnit getTransferOutUnit()
    {
        DataUnit dataUnit = null;

        if (_has(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_UNIT))
        {
            dataUnit = DataUnit.valueOf(_getString(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_UNIT));
        }

        return dataUnit;
    }

    @Override
    public void setTransferOutUnit(DataUnit transferOutUnit)
    {
        _set(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_UNIT, transferOutUnit.toString());
    }

    @Override
    public BigDecimal getTransferOutPrice()
    {
        return _getBigDecimal(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_PRICE);
    }

    @Override
    public void setTransferOutPrice(BigDecimal transferOutPrice)
    {
        _set(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_PRICE, transferOutPrice);
    }

    @Override
    public String getTransferOutBillingKey()
    {
        return _getString(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_BILLING_KEY);
    }

    @Override
    public void setTransferOutBillingKey(String transferOutBillingKey)
    {
        _set(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_BILLING_KEY, transferOutBillingKey);
    }

    @Override
    public boolean getTransferOutBillingRequired()
    {
        return _getBoolean(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_REQUIRES_BILLING);
    }

    @Override
    public void setTransferOutBillingRequired(boolean transferOutBillingRequired)
    {
        _set(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_REQUIRES_BILLING, transferOutBillingRequired);
    }

    @Override
    public long getTransferOutMax()
    {
        return _getLong(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_MAX);
    }

    @Override
    public void setTransferOutMax(long transferOutMax)
    {
        _set(FIELD_TRANSFER_OUT, FIELD_TRANSFER_OUT_MAX, transferOutMax);
    }

    @Override
    public ObjectNode getCapabilities()
    {
        return getObject(FIELD_CAPABILITIES);
    }

    @Override
    public void setCapabilities(ObjectNode capabilities)
    {
        set(FIELD_CAPABILITIES, capabilities);
    }

    @Override
    public int getSubTenantAllowance()
    {
        return getInt(FIELD_SUBTENANT_ALLOWANCE);
    }

    @Override
    public void setSubTenantAllowance(int subtenantAllowance)
    {
        set(FIELD_SUBTENANT_ALLOWANCE, subtenantAllowance);
    }

}
