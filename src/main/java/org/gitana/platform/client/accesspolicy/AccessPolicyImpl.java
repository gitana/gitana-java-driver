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
package org.gitana.platform.client.accesspolicy;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.http.HttpPayload;
import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.platform.AbstractPlatformDocumentImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class AccessPolicyImpl extends AbstractPlatformDocumentImpl implements AccessPolicy
{
    public AccessPolicyImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_ACCESS_POLICY;
    }

    @Override
    protected String getResourceUri()
    {
        return "/access/policies/" + getId();
    }

    @Override
    public String getScope()
    {
        return getString(FIELD_SCOPE);
    }

    @Override
    public void setScope(String scope)
    {
        set(FIELD_SCOPE, scope);
    }

    @Override
    public int getOrder()
    {
        return getInt(FIELD_ORDER);
    }

    @Override
    public void setOrder(int order)
    {
        set(FIELD_ORDER, order);
    }

    @Override
    public List<ObjectNode> getStatements()
    {
        List<ObjectNode> list = new ArrayList<ObjectNode>();

        ArrayNode array = getArray(FIELD_STATEMENTS);
        if (array != null)
        {
            for (int i = 0; i < array.size(); i++)
            {
                list.add((ObjectNode) array.get(i));
            }
        }

        return list;
    }

    @Override
    public void setStatements(List<ObjectNode> statements)
    {
        ArrayNode array = JsonUtil.createArray();

        for (int i = 0; i < statements.size(); i++)
        {
            ObjectNode object = statements.get(i);

            array.add(object);
        }

        set(FIELD_STATEMENTS, array);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELF
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void reload()
    {
        AccessPolicy accessPolicy = getPlatform().readAccessPolicy(this.getId());

        this.reload(accessPolicy.getObject());
    }

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



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get(getResourceUri() + "/acl/list");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getACL(String principalId)
    {
        Response response = getRemote().get(getResourceUri() + "/acl?id=" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/grant?id=" + principalId);
    }

    @Override
    public void grant(DomainPrincipal principal, String authorityId)
    {
        grant(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/revoke?id=" + principalId);
    }

    @Override
    public void revoke(DomainPrincipal principal, String authorityId)
    {
        revoke(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public void revokeAll(DomainPrincipal principal)
    {
        revokeAll(principal.getDomainQualifiedId());
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").booleanValue();
        }

        return has;
    }

    @Override
    public boolean hasAuthority(DomainPrincipal principal, String authorityId)
    {
        return hasAuthority(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public Map<String, Map<String, AuthorityGrant>> getAuthorityGrants(List<String> principalIds)
    {
        ObjectNode object = JsonUtil.createObject();
        JsonUtil.objectPut(object, "principals", principalIds);

        Response response = getRemote().post(getResourceUri() + "/authorities", object);
        return getFactory().principalAuthorityGrants(response);
    }

    @Override
    public boolean hasPermission(String principalId, String permissionId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/permissions/" + permissionId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").booleanValue();
        }

        return has;
    }

    @Override
    public boolean hasPermission(DomainPrincipal principal, String authorityId)
    {
        return hasPermission(principal.getDomainQualifiedId(), authorityId);
    }

}
