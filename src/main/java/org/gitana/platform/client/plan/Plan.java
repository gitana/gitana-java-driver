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

package org.gitana.platform.client.plan;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.registrar.RegistrarDocument;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.services.payment.BillingSchedule;
import org.gitana.platform.services.plan.DataUnit;

import java.math.BigDecimal;

/**
 * @author uzi
 */
public interface Plan extends RegistrarDocument, Selfable
{
    // fields
    public final static String FIELD_PLAN_KEY = "planKey";
    public final static String FIELD_REQUIRES_BILLING = "requiresBilling";

    // base
    public final static String FIELD_BASE = "base";
    public final static String FIELD_BASE_PRICE = "price";
    public final static String FIELD_BASE_SCHEDULE = "schedule";

    // storage
    public final static String FIELD_STORAGE = "storage";
    public final static String FIELD_STORAGE_UNIT = "unit";
    public final static String FIELD_STORAGE_ALLOWANCE = "allowance";
    public final static String FIELD_STORAGE_PRICE = "price";
    public final static String FIELD_STORAGE_MAX = "max";
    public final static String FIELD_STORAGE_BILLING_KEY = "billingKey";
    public final static String FIELD_STORAGE_REQUIRES_BILLING = "requiresBilling";

    // transfer out
    public final static String FIELD_TRANSFER_OUT = "transferOut";
    public final static String FIELD_TRANSFER_OUT_UNIT = "unit";
    public final static String FIELD_TRANSFER_OUT_ALLOWANCE = "allowance";
    public final static String FIELD_TRANSFER_OUT_PRICE = "price";
    public final static String FIELD_TRANSFER_OUT_MAX = "max";
    public final static String FIELD_TRANSFER_OUT_BILLING_KEY = "billingKey";
    public final static String FIELD_TRANSFER_OUT_REQUIRES_BILLING = "requiresBilling";

    // subtenant allowance
    public final static String FIELD_SUBTENANT_ALLOWANCE = "subtenantAllowance";

    // capabilities
    public final static String FIELD_CAPABILITIES = "capabilities";

    public void setPlanKey(String planKey);
    public String getPlanKey();

    public boolean getRequiresBilling();
    public void setRequiresBilling(boolean requiresBilling);

    // base

    public BigDecimal getBillingPrice();
    public void setBillingPrice(BigDecimal billingPrice);

    public BillingSchedule getBillingSchedule();
    public void setBillingSchedule(BillingSchedule billingSchedule);

    // storage

    public DataUnit getStorageUnit();
    public void setStorageUnit(DataUnit storageUnit);

    public long getStorageAllowance();
    public void setStorageAllowance(long storageAllowance);

    public BigDecimal getStoragePrice();
    public void setStoragePrice(BigDecimal storagePrice);

    public long getStorageMax();
    public void setStorageMax(long storageMax);

    public String getStorageBillingKey();
    public void setStorageBillingKey(String storageBillingKey);

    public boolean getStorageBillingRequired();
    public void setStorageBillingRequired(boolean storageBillingRequired);


    // transfer out

    public DataUnit getTransferOutUnit();
    public void setTransferOutUnit(DataUnit transferOutUnit);

    public long getTransferOutAllowance();
    public void setTransferOutAllowance(long transferOutAllowance);

    public BigDecimal getTransferOutPrice();
    public void setTransferOutPrice(BigDecimal transferOutPrice);

    public long getTransferOutMax();
    public void setTransferOutMax(long transferOutMax);

    public String getTransferOutBillingKey();
    public void setTransferOutBillingKey(String transferOutBillingKey);

    public boolean getTransferOutBillingRequired();
    public void setTransferOutBillingRequired(boolean transferOutBillingRequired);


    // capabilities

    public ObjectNode getCapabilities();
    public void setCapabilities(ObjectNode capabilities);

    public int getSubTenantAllowance();
    public void setSubTenantAllowance(int subtenantAllowance);
}
