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
package org.gitana.platform.client.identity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.directory.AbstractDirectoryDocumentImpl;
import org.gitana.platform.client.directory.Directory;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

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
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_IDENTITY;
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
    public void changePassword(String password, String verifyPassword)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put("password", password);
        object.put("verifyPassword", verifyPassword);

        getRemote().post(getResourceUri() + "/changepassword", object);
    }
    
    private ResultMap<ObjectNode> toResultMap(Response response)
    {
        ResultMap<ObjectNode> results = new ResultMapImpl<ObjectNode>();
        for (ObjectNode objectNode: response.getObjectNodes())
        {
            String id = JsonUtil.objectGetString(objectNode, DomainUser.FIELD_ID);

            results.put(id, objectNode);
        }

        return results;        
    }

    @Override
    public ResultMap<ObjectNode> findPolicyUserObjects()
    {
        return findPolicyUserObjects(null);
    }

    @Override
    public ResultMap<ObjectNode> findPolicyUserObjects(String tenantId)
    {
        Map<String, String> params = DriverUtil.params();
        if (tenantId != null)
        {
            params.put("tenantId", tenantId);
        }

        Response response = getRemote().get(getResourceUri() + "/policy/users", params);
        
        return toResultMap(response);
    }

    @Override
    public ObjectNode findPolicyUserObjectForTenant(String tenantId)
    {
        ObjectNode userObject = null;

        try
        {
            Map<String, String> params = DriverUtil.params();
            params.put("tenantId", tenantId);
            
            Response response = getRemote().get(getResourceUri() + "/policy/user", params);

            userObject = response.getObjectNode();
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return userObject;
    }

    @Override
    public ResultMap<ObjectNode> findPolicyTenantObjects()
    {
        return findPolicyTenantObjects(null, null);
    }

    public ResultMap<ObjectNode> findPolicyTenantObjects(Registrar registrar)
    {
        return findPolicyTenantObjects(registrar, null);
    }

    @Override
    public ResultMap<ObjectNode> findPolicyTenantObjects(String authorityId)
    {
        return findPolicyTenantObjects(null, authorityId);
    }

    @Override
    public ResultMap<ObjectNode> findPolicyTenantObjects(Registrar registrar, String authorityId)
    {
        Map<String, String> params = DriverUtil.params();
        if (registrar != null)
        {
            params.put("registrarId", registrar.getId());
        }
        if (authorityId != null)
        {
            params.put("authorityId", authorityId);
        }

        Response response = getRemote().get(getResourceUri() + "/policy/tenants", params);

        return toResultMap(response);
    }

    @Override
    public ResultMap<ObjectNode> findRecipientUserObjects()
    {
        Map<String, String> params = DriverUtil.params();

        Response response = getRemote().get(getResourceUri() + "/users", params);

        return toResultMap(response);
    }

}
