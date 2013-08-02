/**
 * Copyright 2013 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.platform.AbstractPlatformDocumentImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.support.TypedIDConstants;

/**
 * @author uzi
 */
public class AuthenticationGrantImpl extends AbstractPlatformDocumentImpl implements AuthenticationGrant
{
    public AuthenticationGrantImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_AUTHENTICATION_GRANT;
    }

    @Override
    protected String getResourceUri()
    {
        return "/authgrants/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof AuthenticationGrant)
        {
            AuthenticationGrant other = (AuthenticationGrant) object;

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
    public String getPrincipalId()
    {
        return getString(FIELD_PRINCIPAL_ID);
    }

    @Override
    public void setPrincipalId(String principalId)
    {
        set(FIELD_PRINCIPAL_ID, principalId);
    }

    @Override
    public String getPrincipalDomainId()
    {
        return getString(FIELD_PRINCIPAL_DOMAIN_ID);
    }

    @Override
    public void setPrincipalDomainId(String principalDomainId)
    {
        set(FIELD_PRINCIPAL_DOMAIN_ID, principalDomainId);
    }

    @Override
    public String getClientId()
    {
        return getString(FIELD_CLIENT_ID);
    }

    @Override
    public void setClientId(String clientId)
    {
        set(FIELD_CLIENT_ID, clientId);
    }

    @Override
    public boolean getEnabled()
    {
        return getBoolean(FIELD_ENABLED);
    }

    @Override
    public void setEnable(boolean enabled)
    {
        set(FIELD_ENABLED, enabled);
    }
}
