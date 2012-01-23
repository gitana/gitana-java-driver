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
    public void setMaxTotalStorageMB(long maxTotalStorageMB)
    {
        set(FIELD_MAX_TOTAL_STORAGE_MB, maxTotalStorageMB);
    }

    @Override
    public long getMaxTotalStorageMB()
    {
        return getLong(FIELD_MAX_TOTAL_STORAGE_MB);
    }

    @Override
    public void setMaxTotalStorageObjectCount(long maxTotalStorageObjectCount)
    {
        set(FIELD_MAX_TOTAL_STORAGE_OBJECT_COUNT, maxTotalStorageObjectCount);
    }

    @Override
    public long getMaxTotalStorageObjectCount()
    {
        return getLong(FIELD_MAX_TOTAL_STORAGE_OBJECT_COUNT);
    }

    @Override
    public void setMaxDataStoreCount(long maxDataStoreCount)
    {
        set(FIELD_MAX_DATASTORE_COUNT, maxDataStoreCount);
    }

    @Override
    public long getMaxDataStoreCount()
    {
        return getLong(FIELD_MAX_DATASTORE_COUNT);
    }

    @Override
    public void setMaxCollaboratorCount(long maxCollaboratorCount)
    {
        set(FIELD_MAX_COLLABORATOR_COUNT, maxCollaboratorCount);
    }

    @Override
    public long getMaxCollaboratorCount()
    {
        return getLong(FIELD_MAX_COLLABORATOR_COUNT);
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


}
