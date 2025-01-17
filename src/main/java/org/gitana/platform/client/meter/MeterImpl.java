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
 *   info@gitana.io
 */
package org.gitana.platform.client.meter;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.registrar.AbstractRegistrarDocumentImpl;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.services.meter.MeterType;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.DateUtil;
import org.gitana.util.JsonUtil;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author uzi
 */
public class MeterImpl extends AbstractRegistrarDocumentImpl implements Meter
{
    public MeterImpl(Registrar registrar, ObjectNode obj, boolean isSaved)
    {
        super(registrar, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_METER;
    }

    @Override
    protected String getResourceUri()
    {
        return "/registrars/" + getRegistrarId() + "/meters/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Meter)
        {
            Meter other = (Meter) object;

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
        Meter meter = getRegistrar().readMeter(getId());

        this.reload(meter.getObject());
    }
    

    ///////////////////////////////////////////


    @Override
    public String getTenantId()
    {
        return getString(FIELD_TENANT_ID);
    }

    @Override
    public void setTenantId(String tenantId)
    {
        set(FIELD_TENANT_ID, tenantId);
    }

    @Override
    public MeterType getMeterType()
    {
        MeterType meterType = null;

        if (has(FIELD_METER_TYPE))
        {
            meterType = MeterType.valueOf(getString(FIELD_METER_TYPE));
        }

        return meterType;
    }

    @Override
    public void setMeterType(MeterType meterType)
    {
        set(FIELD_METER_TYPE, meterType.toString());
    }

    @Override
    public Calendar getMeterStart()
    {
        Calendar calendar = null;

        if (has(FIELD_METER_START))
        {
            ObjectNode timestamp = getObject(FIELD_METER_START);

            long ms = JsonUtil.objectGetLong(timestamp, "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public void setMeterStart(Calendar meterStart)
    {
        set(FIELD_METER_START, DateUtil.getTimestamp(meterStart));
    }

    @Override
    public Calendar getMeterEnd()
    {
        Calendar calendar = null;

        if (has(FIELD_METER_END))
        {
            ObjectNode timestamp = getObject(FIELD_METER_END);

            long ms = JsonUtil.objectGetLong(timestamp, "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public void setMeterEnd(Calendar meterEnd)
    {
        set(FIELD_METER_END, DateUtil.getTimestamp(meterEnd));
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public long getMaxByteCount()
    {
        return getLong(FIELD_MAX_BYTE_COUNT);
    }

    @Override
    public void setMaxByteCount(long maxByteCount)
    {
        set(FIELD_MAX_BYTE_COUNT, maxByteCount);
    }

    @Override
    public long getRawByteCount()
    {
        return getLong(FIELD_RAW_BYTE_COUNT);
    }

    @Override
    public void setRawByteCount(long rawByteCount)
    {
        set(FIELD_RAW_BYTE_COUNT, rawByteCount);
    }

    @Override
    public BigDecimal getRawByteCountPercentage()
    {
        return getBigDecimal(FIELD_RAW_BYTE_COUNT_PERCENTAGE);
    }

    @Override
    public void setRawByteCountPercentage(BigDecimal rawByteCountPercentage)
    {
        set(FIELD_RAW_BYTE_COUNT_PERCENTAGE, rawByteCountPercentage);
    }

    @Override
    public long getUnprocessedByteCount()
    {
        return getLong(FIELD_UNPROCESSED_BYTE_COUNT);
    }

    @Override
    public void setUnprocessedByteCount(long unprocessedByteCount)
    {
        set(FIELD_UNPROCESSED_BYTE_COUNT, unprocessedByteCount);
    }

    @Override
    public long getBillableByteCount()
    {
        return getLong(FIELD_BILLABLE_BYTE_COUNT);
    }

    @Override
    public void setBillableByteCount(long billableByteCount)
    {
        set(FIELD_BILLABLE_BYTE_COUNT, billableByteCount);
    }

    @Override
    public BigDecimal getBillableByteCountPercentage()
    {
        return getBigDecimal(FIELD_BILLABLE_BYTE_COUNT_PERCENTAGE);
    }

    @Override
    public void setBillableByteCountPercentage(BigDecimal billableByteCountPercentage)
    {
        set(FIELD_BILLABLE_BYTE_COUNT_PERCENTAGE, billableByteCountPercentage);
    }

    @Override
    public long getMaxObjectCount()
    {
        return getLong(FIELD_MAX_OBJECT_COUNT);
    }

    @Override
    public void setMaxObjectCount(long maxObjectCount)
    {
        set(FIELD_MAX_OBJECT_COUNT, maxObjectCount);
    }

    @Override
    public long getRawObjectCount()
    {
        return getLong(FIELD_RAW_OBJECT_COUNT);
    }

    @Override
    public void setRawObjectCount(long rawObjectCount)
    {
        set(FIELD_RAW_OBJECT_COUNT, rawObjectCount);
    }

    @Override
    public BigDecimal getRawObjectCountPercentage()
    {
        return getBigDecimal(FIELD_RAW_OBJECT_COUNT_PERCENTAGE);
    }

    @Override
    public void setRawObjectCountPercentage(BigDecimal rawObjectCountPercentage)
    {
        set(FIELD_RAW_OBJECT_COUNT_PERCENTAGE, rawObjectCountPercentage);
    }

    @Override
    public long getUnprocessedObjectCount()
    {
        return getLong(FIELD_UNPROCESSED_OBJECT_COUNT);
    }

    @Override
    public void setUnprocessedObjectCount(long unprocessedObjectCount)
    {
        set(FIELD_UNPROCESSED_OBJECT_COUNT, unprocessedObjectCount);
    }

    @Override
    public long getBillableObjectCount()
    {
        return getLong(FIELD_BILLABLE_OBJECT_COUNT);
    }

    @Override
    public void setBillableObjectCount(long billableObjectCount)
    {
        set(FIELD_BILLABLE_OBJECT_COUNT, billableObjectCount);
    }

    @Override
    public BigDecimal getBillableObjectCountPercentage()
    {
        return getBigDecimal(FIELD_BILLABLE_OBJECT_COUNT_PERCENTAGE);
    }

    @Override
    public void setBillableObjectCountPercentage(BigDecimal billableObjectCountPercentage)
    {
        set(FIELD_BILLABLE_OBJECT_COUNT_PERCENTAGE, billableObjectCountPercentage);
    }
}
