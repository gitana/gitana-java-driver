/**
 * Copyright 2026 Gitana Software, Inc.
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
package org.gitana.platform.client.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.platform.AbstractPlatformDocumentImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class ClientImpl extends AbstractPlatformDocumentImpl implements Client
{
    public ClientImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_CLIENT;
    }

    @Override
    protected String getResourceUri()
    {
        return "/clients/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Client)
        {
            Client other = (Client) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    @Override
    public String getKey()
    {
        return getString(FIELD_KEY);
    }

    @Override
    public void setKey(String key)
    {
        set(FIELD_KEY, key);
    }

    @Override
    public String getSecret()
    {
        return getString(FIELD_SECRET);
    }

    @Override
    public void setSecret(String secret)
    {
        set(FIELD_SECRET, secret);
    }

    @Override
    public List<String> getDomainUrls()
    {
        List<String> domainUrls = new ArrayList<String>();

        if (has(FIELD_DOMAIN_URLS))
        {
            domainUrls.addAll((List) JsonUtil.toValue(this.getArray(FIELD_DOMAIN_URLS)));
        }

        return domainUrls;
    }

    @Override
    public void setDomainUrls(List<String> domainUrls)
    {
        set(FIELD_DOMAIN_URLS, domainUrls);
    }

    @Override
    public boolean getIsTenantDefault()
    {
        return getBoolean(FIELD_IS_TENANT_DEFAULT);
    }

    @Override
    public void setIsTenantDefault(boolean isTenantDefault)
    {
        set(FIELD_IS_TENANT_DEFAULT, isTenantDefault);
    }

    @Override
    public String getDefaultTenantId()
    {
        return getString(FIELD_DEFAULT_TENANT_ID);
    }

    @Override
    public void setDefaultTenantId(String tenantId)
    {
        set(FIELD_DEFAULT_TENANT_ID, tenantId);
    }

    @Override
    public Collection<String> getAuthorizedGrantTypes()
    {
        List<String> authorizedGrantTypes = new ArrayList<String>();

        ArrayNode array = getArray(FIELD_AUTHORIZED_GRANT_TYPES);
        if (array != null)
        {
            for (int i = 0; i < array.size(); i++)
            {
                String authorizedGrantType = array.get(i).textValue();

                authorizedGrantTypes.add(authorizedGrantType);
            }
        }

        return authorizedGrantTypes;
    }

    @Override
    public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes)
    {
        List<String> list = new ArrayList<String>();
        list.addAll(authorizedGrantTypes);

        set(FIELD_AUTHORIZED_GRANT_TYPES, list);
    }

    @Override
    public Collection<String> getScope()
    {
        List<String> scopeList = new ArrayList<String>();

        ArrayNode array = getArray(FIELD_SCOPE);
        if (array != null)
        {
            for (int i = 0; i < array.size(); i++)
            {
                String scope = array.get(i).textValue();

                scopeList.add(scope);
            }
        }

        return scopeList;
    }

    @Override
    public void setScope(Collection<String> scope)
    {
        List<String> list = new ArrayList<String>();
        list.addAll(scope);

        set(FIELD_SCOPE, list);
    }

    @Override
    public String getRegisteredRedirectUri()
    {
        return getString(FIELD_REGISTERED_REDIRECT_URI);
    }

    @Override
    public void setRegisteredRedirectUri(String registeredRedirectUri)
    {
        set(FIELD_REGISTERED_REDIRECT_URI, registeredRedirectUri);
    }

    @Override
    public boolean getAllowOpenDriverAuthentication()
    {
        return getBoolean(FIELD_ALLOW_OPENDRIVER_AUTHENTICATION);
    }

    @Override
    public void setAllowOpenDriverAuthentication(boolean allowOpenDriverAuthentication)
    {
        set(FIELD_ALLOW_OPENDRIVER_AUTHENTICATION, allowOpenDriverAuthentication);
    }

    @Override
    public boolean getEnabled()
    {
        return getBoolean(FIELD_ENABLED);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        set(FIELD_ENABLED, enabled);
    }

    @Override
    public boolean getAllowAutoApprovalForImplicitFlow()
    {
        return getBoolean(FIELD_ALLOW_AUTO_APPROVAL_FOR_IMPLICIT_FLOW);
    }

    @Override
    public void setAllowAutoApprovalForImplicitFlow(boolean allowAutoApprovalForImplicitFlow)
    {
        set(FIELD_ALLOW_AUTO_APPROVAL_FOR_IMPLICIT_FLOW, allowAutoApprovalForImplicitFlow);
    }

    @Override
    public int getAccessTokenValiditySeconds()
    {
        return getInt(FIELD_ACCESS_TOKEN_VALIDITY_SECONDS);
    }

    @Override
    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds)
    {
        set(FIELD_ACCESS_TOKEN_VALIDITY_SECONDS, accessTokenValiditySeconds);
    }

    @Override
    public int getRefreshTokenValiditySeconds()
    {
        return getInt(FIELD_REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    @Override
    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds)
    {
        set(FIELD_REFRESH_TOKEN_VALIDITY_SECONDS, refreshTokenValiditySeconds);
    }

    @Override
    public boolean getAllowGuestLogin()
    {
        return getBoolean(FIELD_ALLOW_GUEST_LOGIN);
    }

    @Override
    public void setAllowGuestLogin(boolean allowGuestLogin)
    {
        set(FIELD_ALLOW_GUEST_LOGIN, allowGuestLogin);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELF
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void reload()
    {
        Stack project = getPlatform().readStack(this.getId());

        this.reload(project.getObject());
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
