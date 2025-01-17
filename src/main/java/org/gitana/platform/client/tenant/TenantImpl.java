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
package org.gitana.platform.client.tenant;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.meter.Meter;
import org.gitana.platform.client.registrar.AbstractRegistrarDocumentImpl;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.meter.MeterType;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class TenantImpl extends AbstractRegistrarDocumentImpl implements Tenant
{
    public TenantImpl(Registrar registrar, ObjectNode obj, boolean isSaved)
    {
        super(registrar, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_TENANT;
    }

    @Override
    protected String getResourceUri()
    {
        return "/registrars/" + getRegistrarId() + "/tenants/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Tenant)
        {
            Tenant other = (Tenant) object;

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
    public void update(ObjectNode paymentMethodObject)
    {
        ObjectNode o = JsonUtil.copyObject(getObject());
        if (paymentMethodObject != null)
        {
            o.put("paymentMethod", paymentMethodObject);
        }

        getRemote().put(getResourceUri(), o);
    }

    @Override
    public void delete()
    {
        getRemote().delete(getResourceUri());
    }

    @Override
    public void reload()
    {
        Tenant tenant = getRegistrar().readTenant(getId());

        this.reload(tenant.getObject());
    }

    @Override
    public void closeOut()
    {
        getRemote().post(getResourceUri() + "/closeout");
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
    public void setPrincipalId(String adminPrincipalId)
    {
        set(FIELD_PRINCIPAL_ID, adminPrincipalId);
    }

    @Override
    public String getPrincipalId()
    {
        return getString(FIELD_PRINCIPAL_ID);
    }

    @Override
    public void setDomainId(String domainId)
    {
        set(FIELD_DOMAIN_ID, domainId);
    }

    @Override
    public String getDomainId()
    {
        return getString(FIELD_DOMAIN_ID);
    }

    @Override
    public void setPlatformId(String platformId)
    {
        set(FIELD_PLATFORM_ID, platformId);
    }

    @Override
    public String getPlatformId()
    {
        return getString(FIELD_PLATFORM_ID);
    }

    @Override
    public void setBillingSubscriptionId(String billingSubscriptionId)
    {
        set(FIELD_BILLING_SUBSCRIPTION_ID, billingSubscriptionId);
    }

    @Override
    public String getBillingSubscriptionId()
    {
        return getString(FIELD_BILLING_SUBSCRIPTION_ID);
    }

    @Override
    public void setBillingMethodPaymentId(String billingPaymentMethodId)
    {
        set(FIELD_BILLING_PAYMENT_METHOD_ID, billingPaymentMethodId);
    }

    @Override
    public String getBillingMethodPaymentId()
    {
        return getString(FIELD_BILLING_PAYMENT_METHOD_ID);
    }

    @Override
    public void setDnsSlug(String tenantDnsKey)
    {
        set(FIELD_DNS_SLUG, tenantDnsKey);
    }

    @Override
    public String getDnsSlug()
    {
        return getString(FIELD_DNS_SLUG);
    }

    @Override
    public void setSubTenantCount(int subtenantCount)
    {
        set(FIELD_SUBTENANT_COUNT, subtenantCount);
    }

    @Override
    public int getSubTenantCount()
    {
        return getInt(FIELD_SUBTENANT_COUNT);
    }

    @Override
    public void setDeployedApplicationCount(int deployedApplicationCount)
    {
        set(FIELD_DEPLOYED_APPLICATION_COUNT, deployedApplicationCount);
    }

    @Override
    public int getDeployedApplicationCount()
    {
        return getInt(FIELD_DEPLOYED_APPLICATION_COUNT);
    }

    @Override
    public boolean getEnabled()
    {
        // in order to support legacy tenants (where the "enabled" field did not exist), we assume
        // enabled to be true.  If the field does not exist, then we return true.  If it does exist,
        // then we hand back the value of the field

        boolean enabled = true;
        if (has(FIELD_ENABLED))
        {
            enabled = getBoolean(FIELD_ENABLED);
        }

        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        set(FIELD_ENABLED, enabled);
    }

    private ResultMap<ObjectNode> toObjects(Response response)
    {
        ResultMap<ObjectNode> results = new ResultMapImpl<ObjectNode>();

        List<ObjectNode> objects = response.getObjectNodes();
        for (ObjectNode object: objects)
        {
            String id = object.get("_doc").textValue();
            results.put(id, object);
        }

        return results;        
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedObjects()
    {
        return listAllocatedObjects(null, null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedObjects(Pagination pagination) 
    {
        return listAllocatedObjects(null, pagination);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedObjects(String objectType) 
    {
        return listAllocatedObjects(objectType, null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedObjects(String objectType, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);
        if (objectType != null)
        {
            params.put("type", objectType);
        }
        
        Response response = getRemote().get(getResourceUri() + "/objects", params);

        return toObjects(response);
    }
    
    @Override
    public ResultMap<ObjectNode> listAllocatedRepositoryObjects()
    {
        return listAllocatedRepositoryObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedRepositoryObjects(Pagination pagination)
    {
        return listAllocatedObjects("repository", pagination);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedDomainObjects()
    {
        return listAllocatedDomainObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedDomainObjects(Pagination pagination)
    {
        return listAllocatedObjects("domain", pagination);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedVaultObjects()
    {
        return listAllocatedVaultObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedVaultObjects(Pagination pagination)
    {
        return listAllocatedObjects("vault", pagination);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedClientObjects()
    {
        return listAllocatedClientObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedClientObjects(Pagination pagination)
    {
        return listAllocatedObjects("client", pagination);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedRegistrarObjects()
    {
        return listAllocatedRegistrarObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedRegistrarObjects(Pagination pagination)
    {
        return listAllocatedObjects("registrar", pagination);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedStackObjects()
    {
        return listAllocatedStackObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedStackObjects(Pagination pagination)
    {
        return listAllocatedObjects("stack", pagination);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedDirectoryObjects()
    {
        return listAllocatedDirectoryObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedDirectoryObjects(Pagination pagination)
    {
        return listAllocatedObjects("directory", pagination);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedApplicationObjects()
    {
        return listAllocatedApplicationObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedApplicationObjects(Pagination pagination)
    {
        return listAllocatedObjects("application", pagination);
    }

    @Override
    public ObjectNode readDefaultAllocatedClientObject() 
    {
        ObjectNode object = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/defaultclient");

            object = response.getObjectNode();
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return object;
    }

    @Override
    public ResultMap<Meter> listMeters()
    {
        return listMeters(null);
    }

    @Override
    public ResultMap<Meter> listMeters(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/meters", params);
        return getFactory().meters(this.getRegistrar(), response);
    }

    @Override
    public ResultMap<Meter> queryMeters(ObjectNode query)
    {
        return queryMeters(query, null);
    }

    @Override
    public ResultMap<Meter> queryMeters(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/meters/query", params, query);
        return getFactory().meters(this.getRegistrar(), response);
    }

    @Override
    public Meter readCurrentMeter(MeterType meterType)
    {
        Meter meter = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/meters/current/" + meterType.toString());
            meter = getFactory().meter(this.getRegistrar(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return meter;
    }
}
