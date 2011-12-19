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
import org.gitana.platform.client.platform.AbstractPlatformDocumentImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

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

}
