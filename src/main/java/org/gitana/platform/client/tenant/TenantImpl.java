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

package org.gitana.platform.client.tenant;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.api.Consumer;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.registrar.AbstractRegistrarDocumentImpl;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
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
    public ResultMap<ObjectNode> listAllocatedObjects()
    {
        return listAllocatedObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> listAllocatedObjects(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);
        Response response = getRemote().get(getResourceUri() + "/objects", params);

        ResultMap<ObjectNode> results = new ResultMapImpl<ObjectNode>();

        List<ObjectNode> objects = response.getObjectNodes();
        for (ObjectNode object: objects)
        {
            String id = object.get("_doc").getTextValue();
            results.put(id, object);
        }

        return results;
    }

    @Override
    public ResultMap<Repository> listRepositories()
    {
        return listRepositories(null);
    }

    @Override
    public ResultMap<Repository> listRepositories(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/repositories", params);
        return getFactory().repositories(getRegistrar().getPlatform(), response);
    }

    @Override
    public ResultMap<Domain> listDomains()
    {
        return listDomains(null);
    }

    @Override
    public ResultMap<Domain> listDomains(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/domains", params);
        return getFactory().domains(getRegistrar().getPlatform(), response);
    }

    @Override
    public ResultMap<Vault> listVaults()
    {
        return listVaults(null);
    }

    @Override
    public ResultMap<Vault> listVaults(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/vaults", params);
        return getFactory().vaults(getRegistrar().getPlatform(), response);
    }

    @Override
    public ResultMap<Consumer> listConsumers()
    {
        return listConsumers(null);
    }

    @Override
    public ResultMap<Consumer> listConsumers(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/consumers", params);
        return getFactory().consumers(getRegistrar().getPlatform(), response);
    }

    @Override
    public ResultMap<Registrar> listRegistrars()
    {
        return listRegistrars(null);
    }

    @Override
    public ResultMap<Registrar> listRegistrars(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/registrars", params);
        return getFactory().registrars(getRegistrar().getPlatform(), response);
    }

    @Override
    public Consumer readDefaultConsumer()
    {
        Consumer consumer = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/defaultconsumer");
            consumer = getFactory().consumer(this.getRegistrar().getPlatform(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return consumer;
    }

}
