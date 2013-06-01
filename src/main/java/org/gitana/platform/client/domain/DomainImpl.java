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

package org.gitana.platform.client.domain;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainGroup;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.principals.PrincipalType;
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
public class DomainImpl extends AbstractPlatformDataStoreImpl implements Domain
{
    public DomainImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_DOMAIN;
    }

    @Override
    public String getResourceUri()
    {
        return "/domains/" + getId();
    }

    @Override
    public void reload()
    {
        Domain domain = getPlatform().readDomain(getId());
        this.reload(domain.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DOMAIN GROUPS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<DomainPrincipal> listPrincipals()
    {
        return listPrincipals(null);
    }

    @Override
    public ResultMap<DomainPrincipal> listPrincipals(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/principals", params);
        return getFactory().domainPrincipals(getPlatform(), response);
    }

    @Override
    public DomainPrincipal readPrincipal(String principalId)
    {
        DomainPrincipal principal = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/principals/" + principalId);
            principal = getFactory().domainPrincipal(getPlatform(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return principal;
    }

    @Override
    public DomainPrincipal createPrincipal(PrincipalType type, ObjectNode object)
    {
        if (object == null)
        {
            throw new RuntimeException("Object required");
        }

        // ensure object has name
        if (!object.has(DomainPrincipal.FIELD_NAME))
        {
            throw new RuntimeException("Missing principal name");
        }

        object.put(DomainGroup.FIELD_TYPE, type.toString());

        Response response = getRemote().post(getResourceUri() + "/principals", object);

        String principalId = response.getId();
        return readPrincipal(principalId);
    }

    @Override
    public DomainUser createUser(String name, String password)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put(DomainUser.FIELD_NAME, name);
        object.put("password", password);

        return (DomainUser) createPrincipal(PrincipalType.USER, object);
    }

    @Override
    public DomainGroup createGroup(String name)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put(DomainUser.FIELD_NAME, name);

        return (DomainGroup) createPrincipal(PrincipalType.GROUP, object);
    }

    @Override
    public ResultMap<DomainPrincipal> queryPrincipals(ObjectNode query)
    {
        return queryPrincipals(query, null);
    }

    @Override
    public ResultMap<DomainPrincipal> queryPrincipals(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/principals/query", params, query);
        return getFactory().domainPrincipals(getPlatform(), response);
    }

    @Override
    public void updatePrincipal(DomainPrincipal principal)
    {
        getRemote().put(getResourceUri() + "/principals/" + principal.getId(), principal.getObject());
    }

    @Override
    public void deletePrincipal(DomainPrincipal principal)
    {
        deletePrincipal(principal.getId());
    }

    @Override
    public void deletePrincipal(String principalId)
    {
        getRemote().delete(getResourceUri() + "/principals/" + principalId);
    }

    @Override
    public ResultMap<DomainGroup> listMemberships(DomainPrincipal principal)
    {
        return listMemberships(principal.getId());
    }

    @Override
    public ResultMap<DomainGroup> listMemberships(String principalId)
    {
        return listMemberships(principalId, false);
    }

    @Override
    public ResultMap<DomainGroup> listMemberships(DomainPrincipal principal, boolean includeIndirectMemberships)
    {
        return listMemberships(principal.getId(), includeIndirectMemberships);
    }

    @Override
    public ResultMap<DomainGroup> listMemberships(String principalId, boolean includeIndirectMemberships)
    {
        String url = getResourceUri() + "/principals/" + principalId + "/memberships";
        if (includeIndirectMemberships)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        ResultMap<DomainPrincipal> principals = getFactory().domainPrincipals(getPlatform(), response);
        ResultMap<DomainGroup> groups = new ResultMapImpl<DomainGroup>();
        for (DomainPrincipal principal: principals.values())
        {
            DomainGroup group = (DomainGroup) principal;
            groups.put(group.getId(), group);
        }

        return groups;
    }

    @Override
    public PermissionCheckResults checkPrincipalPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/principals/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }

    @Override
    public ResultMap<DomainUser> listUsers()
    {
        return listUsers(null);
    }

    @Override
    public ResultMap<DomainUser> listUsers(Pagination pagination)
    {
        return queryUsers(JsonUtil.createObject(), pagination);
    }

    @Override
    public ResultMap<DomainGroup> listGroups()
    {
        return listGroups(null);
    }

    @Override
    public ResultMap<DomainGroup> listGroups(Pagination pagination)
    {
        return queryGroups(JsonUtil.createObject(), pagination);
    }

    @Override
    public ResultMap<DomainUser> queryUsers(ObjectNode query)
    {
        return queryUsers(query, null);
    }

    @Override
    public ResultMap<DomainUser> queryUsers(ObjectNode query, Pagination pagination)
    {
        query.put(DomainPrincipal.FIELD_TYPE, PrincipalType.USER.toString());

        ResultMap<DomainPrincipal> principals = queryPrincipals(query, pagination);
        ResultMap<DomainUser> users = new ResultMapImpl<DomainUser>(principals.offset(), principals.totalRows());
        for (DomainPrincipal principal: principals.values())
        {
            DomainUser user = (DomainUser) principal;
            users.put(user.getId(), user);
        }

        return users;
    }

    @Override
    public ResultMap<DomainGroup> queryGroups(ObjectNode query)
    {
        return queryGroups(query, null);
    }

    @Override
    public ResultMap<DomainGroup> queryGroups(ObjectNode query, Pagination pagination)
    {
        query.put(DomainPrincipal.FIELD_TYPE, PrincipalType.GROUP.toString());

        ResultMap<DomainPrincipal> principals = queryPrincipals(query, pagination);
        ResultMap<DomainGroup> groups = new ResultMapImpl<DomainGroup>(principals.offset(), principals.totalRows());
        for (DomainPrincipal principal: principals.values())
        {
            DomainGroup group = (DomainGroup) principal;
            groups.put(group.getId(), group);
        }

        return groups;
    }

    @Override
    public DomainUser inviteUser(DomainUser invitee)
    {
        Response response = getRemote().post(getResourceUri() + "/principals/invite?id=" + invitee.getDomainQualifiedId());

        String principalId = response.getId();
        return (DomainUser) readPrincipal(principalId);
    }
}
