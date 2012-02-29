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

    // billing
    public final static String FIELD_REQUIRES_BILLING = "requiresBilling";
    public final static String FIELD_BILLING_SCHEDULE = "billingSchedule";
    public final static String FIELD_BILLING_PRICE = "billingPrice";

    // storage
    public final static String FIELD_STORAGE_AMOUNT = "storageAmount";
    public final static String FIELD_STORAGE_UNIT = "storageUnit";
    public final static String FIELD_STORAGE_OVERAGE_PRICE = "storageOveragePrice";
    public final static String FIELD_STORAGE_OVERAGE_UNIT = "storageOverageUnit";

    // transfer
    public final static String FIELD_TRANSFER_AMOUNT = "transferAmount";
    public final static String FIELD_TRANSFER_UNIT = "transferUnit";
    public final static String FIELD_TRANSFER_OVERAGE_PRICE = "transferOveragePrice";
    public final static String FIELD_TRANSFER_OVERAGE_UNIT = "transferOverageUnit";

    public final static String FIELD_DATASTORE_AMOUNT = "datastoreAmount";
    public final static String FIELD_OBJECT_AMOUNT = "objectAmount";
    public final static String FIELD_COLLABORATOR_AMOUNT = "collaboratorAmount";

    public void setPlanKey(String planKey);
    public String getPlanKey();

    public boolean getRequiresBilling();
    public void setRequiresBilling(boolean requiresBilling);

    public BillingSchedule getBillingSchedule();
    public void setBillingSchedule(BillingSchedule billingSchedule);

    public BigDecimal getBillingPrice();
    public void setBillingPrice(BigDecimal billingPrice);

    public long getStorageAmount();
    public void setStorageAmount(long storageAmount);

    public DataUnit getStorageUnit();
    public void setStorageUnit(DataUnit storageUnit);

    public BigDecimal getStorageOveragePrice();
    public void setStorageOveragePrice(BigDecimal storageOveragePrice);

    public DataUnit getStorageOverageUnit();
    public void setStorageOverageUnit(DataUnit storageOverageUnit);

    public long getTransferAmount();
    public void setTransferAmount(long transferAmount);

    public DataUnit getTransferUnit();
    public void setTransferUnit(DataUnit transferUnit);

    public BigDecimal getTransferOveragePrice();
    public void setTransferOveragePrice(BigDecimal transferOveragePrice);

    public DataUnit getTransferOverageUnit();
    public void setTransferOverageUnit(DataUnit transferOverageUnit);

    public long getDatastoreAmount();
    public void setDatastoreAmount(long datastoreAmount);

    public long getObjectAmount();
    public void setObjectAmount(long objectAmount);

    public long getCollaboratorAmount();
    public void setCollaboratorAmount(long collaboratorAmount);


}
