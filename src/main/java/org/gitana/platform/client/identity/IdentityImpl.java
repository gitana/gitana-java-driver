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

package org.gitana.platform.client.identity;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.directory.AbstractDirectoryDocumentImpl;
import org.gitana.platform.client.directory.Directory;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
import org.gitana.util.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class IdentityImpl extends AbstractDirectoryDocumentImpl implements Identity
{
    public IdentityImpl(Directory directory, ObjectNode obj, boolean isSaved)
    {
        super(directory, obj, isSaved);
    }

    @Override
    protected String getResourceUri()
    {
        return "/directories/" + getDirectoryId() + "/identities/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Identity)
        {
            Identity other = (Identity) object;

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
        Identity identity = getDirectory().readIdentity(getId());

        this.reload(identity.getObject());
    }

    @Override
    public void changePassword(String newPassword)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put("password", newPassword);

        getRemote().post(getResourceUri() + "/changepassword", object);
    }

    @Override
    public ResultMap<DomainUser> findUsers() 
    {
        Map<String, String> params = DriverUtil.params();

        Response response = getRemote().get(getResourceUri() + "/users", params);
        ResultMap<DomainPrincipal> principals = getFactory().domainPrincipals(this.getPlatform(), response);
        
        ResultMap<DomainUser> users = new ResultMapImpl<DomainUser>(principals.offset(), principals.totalRows());
        for (DomainPrincipal principal: principals.values())
        {
            DomainUser user = (DomainUser) principal;
            
            users.put(user.getId(), user);
        }

        return users;
    }

    @Override
    public DomainUser findUserForTenant(String tenantId) 
    {
        DomainUser domainUser = null;

        try
        {
            Map<String, String> params = DriverUtil.params();
            params.put("tenantId", tenantId);
            
            Response response = getRemote().get(getResourceUri() + "/user", params);
            domainUser = (DomainUser) getFactory().domainPrincipal(this.getPlatform(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return domainUser;
    }

    @Override
    public ResultMap<Tenant> findTenants() 
    {
        return findTenants(null);
    }

    public ResultMap<Tenant> findTenants(Registrar registrar)
    {
        Map<String, String> params = DriverUtil.params();
        if (registrar != null)
        {
            params.put("registrarId", registrar.getId());
        }

        Map<String, Registrar> registrars = new HashMap<String, Registrar>();
        if (registrar != null)
        {
            registrars.put(registrar.getId(), registrar);
        }
        
        Response response = getRemote().get(getResourceUri() + "/tenants", params);

        ResultMap<Tenant> tenants = new ResultMapImpl<Tenant>();
        List<ObjectNode> objectNodes = response.getObjectNodes();
        for (ObjectNode objectNode: objectNodes)
        {
            String registrarId = JsonUtil.objectGetString(objectNode, "registrarId");
            
            registrar = registrars.get(registrarId);
            if (registrar == null)
            {
                registrar = getPlatform().readRegistrar(registrarId);
                
                registrars.put(registrarId, registrar);
            }
            
            Tenant tenant = getFactory().tenant(registrar, objectNode);
            tenants.put(tenant.getId(), tenant);
        }

        return tenants;
    }
}
