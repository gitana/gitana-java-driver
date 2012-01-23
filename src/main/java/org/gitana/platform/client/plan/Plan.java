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

import java.math.BigDecimal;

/**
 * @author uzi
 */
public interface Plan extends RegistrarDocument, Selfable
{
    // fields
    public final static String FIELD_PLAN_KEY = "planKey";

    public final static String FIELD_MAX_TOTAL_STORAGE_MB = "maxTotalStorageMB";
    public final static String FIELD_MAX_TOTAL_STORAGE_OBJECT_COUNT = "maxTotalStorageObjectCount";
    public final static String FIELD_MAX_DATASTORE_COUNT = "maxDataStoreCount";
    public final static String FIELD_MAX_COLLABORATOR_COUNT = "maxCollaboratorCount";
    
    // billing
    public final static String FIELD_REQUIRES_BILLING = "requiresBilling";
    public final static String FIELD_BILLING_SCHEDULE = "billingSchedule";
    public final static String FIELD_BILLING_PRICE = "billingPrice";

    public void setPlanKey(String planKey);
    public String getPlanKey();

    public void setMaxTotalStorageMB(long maxTotalStorageMB);
    public long getMaxTotalStorageMB();

    public void setMaxTotalStorageObjectCount(long maxTotalStorageObjectCount);
    public long getMaxTotalStorageObjectCount();

    public void setMaxDataStoreCount(long maxDataStoreCount);
    public long getMaxDataStoreCount();

    public void setMaxCollaboratorCount(long maxCollaboratorCount);
    public long getMaxCollaboratorCount();

    public boolean getRequiresBilling();
    public void setRequiresBilling(boolean requiresBilling);

    public BillingSchedule getBillingSchedule();
    public void setBillingSchedule(BillingSchedule billingSchedule);

    public BigDecimal getBillingPrice();
    public void setBillingPrice(BigDecimal price);

}
