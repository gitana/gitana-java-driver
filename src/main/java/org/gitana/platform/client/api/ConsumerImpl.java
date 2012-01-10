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

package org.gitana.platform.client.api;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.platform.AbstractPlatformDocumentImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class ConsumerImpl extends AbstractPlatformDocumentImpl implements Consumer
{
    public ConsumerImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    protected String getResourceUri()
    {
        return "/consumers/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Consumer)
        {
            Consumer other = (Consumer) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    @Override
    public String getAuthType()
    {
        return getString(FIELD_AUTH_TYPE);
    }

    @Override
    public void setAuthType(String authType)
    {
        set(FIELD_AUTH_TYPE, authType);
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
            domainUrls.addAll((List) JsonUtil.toValue(getArray(FIELD_DOMAIN_URLS)));
        }

        return domainUrls;
    }

    @Override
    public void setDomainUrls(List<String> domainUrls)
    {
        set(FIELD_DOMAIN_URLS, domainUrls);
    }

    @Override
    public List<String> getAuthorities()
    {
        List<String> authorities = new ArrayList<String>();

        if (has(FIELD_DOMAIN_URLS))
        {
            authorities.addAll((List) JsonUtil.toValue(getArray(FIELD_AUTHORITIES)));
        }

        return authorities;
    }

    @Override
    public void setAuthorities(List<String> authorities)
    {
        set(FIELD_AUTHORITIES, authorities);
    }

    @Override
    public boolean getAllowTicketAuthentication()
    {
        return getBoolean(FIELD_ALLOW_TICKET_AUTHENTICATION);
    }

    @Override
    public void setAllowTicketAuthentication(boolean allowTicketAuthentication)
    {
        set(FIELD_ALLOW_TICKET_AUTHENTICATION, allowTicketAuthentication);
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
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/revoke?id=" + principalId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
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
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
    }
}
