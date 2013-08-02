/**
 * Copyright 2013 Gitana Software, Inc.
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

package org.gitana.platform.client.meter;

import org.gitana.platform.client.registrar.RegistrarDocument;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.services.meter.MeterType;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * @author uzi
 */
public interface Meter extends RegistrarDocument, Selfable
{
    // default collection location
    public final static String DEFAULT_COLLECTION_ID = "meters";

    // tenant information
    public final static String FIELD_TENANT_ID = "tenantId";

    // meter data
    public final static String FIELD_METER_TYPE = "meterType";
    public final static String FIELD_METER_START = "meterStart"; // timestamp
    public final static String FIELD_METER_END = "meterEnd"; // timestamp

    // bytes
    public final static String FIELD_MAX_BYTE_COUNT = "maxByteCount";
    public final static String FIELD_RAW_BYTE_COUNT = "rawByteCount"; // raw count (all bytes counter)
    public final static String FIELD_RAW_BYTE_COUNT_PERCENTAGE = "rawByteCountPercentage";
    public final static String FIELD_UNPROCESSED_BYTE_COUNT = "unprocessedByteCount"; // waiting to be processed
    public final static String FIELD_BILLABLE_BYTE_COUNT = "billableByteCount";
    public final static String FIELD_BILLABLE_BYTE_COUNT_PERCENTAGE = "billableByteCountPercentage";

    // objects
    public final static String FIELD_MAX_OBJECT_COUNT = "maxObjectCount";
    public final static String FIELD_RAW_OBJECT_COUNT = "rawObjectCount"; // raw count (all objects counter)
    public final static String FIELD_RAW_OBJECT_COUNT_PERCENTAGE = "rawObjectCountPercentage";
    public final static String FIELD_UNPROCESSED_OBJECT_COUNT = "unprocessedObjectCount";
    public final static String FIELD_BILLABLE_OBJECT_COUNT = "billableObjectCount";
    public final static String FIELD_BILLABLE_OBJECT_COUNT_PERCENTAGE = "billableObjectCountPercentage";

    public String getTenantId();
    public void setTenantId(String tenantId);

    public MeterType getMeterType();
    public void setMeterType(MeterType meterType);

    public Calendar getMeterStart();
    public void setMeterStart(Calendar meterStart);

    public Calendar getMeterEnd();
    public void setMeterEnd(Calendar meterEnd);


    // bytes

    public long getMaxByteCount();
    public void setMaxByteCount(long maxByteCount);

    public long getRawByteCount();
    public void setRawByteCount(long rawByteCount);

    public BigDecimal getRawByteCountPercentage();
    public void setRawByteCountPercentage(BigDecimal rawByteCountPercentage);

    public long getUnprocessedByteCount();
    public void setUnprocessedByteCount(long unprocessedByteCount);

    public long getBillableByteCount();
    public void setBillableByteCount(long billableByteCount);

    public BigDecimal getBillableByteCountPercentage();
    public void setBillableByteCountPercentage(BigDecimal byteCountPercentage);


    // objects

    public long getMaxObjectCount();
    public void setMaxObjectCount(long maxObjectCount);

    public long getRawObjectCount();
    public void setRawObjectCount(long rawObjectCount);

    public BigDecimal getRawObjectCountPercentage();
    public void setRawObjectCountPercentage(BigDecimal rawObjectCountPercentage);

    public long getUnprocessedObjectCount();
    public void setUnprocessedObjectCount(long unprocessedObjectCount);

    public long getBillableObjectCount();
    public void setBillableObjectCount(long billableObjectCount);

    public BigDecimal getBillableObjectCountPercentage();
    public void setBillableObjectCountPercentage(BigDecimal billableObjectCountPercentage);

}
