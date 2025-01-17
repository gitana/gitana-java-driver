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
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.GitanaObjectImpl;

/**
 * @author uzi
 */
public class AuthInfo extends GitanaObjectImpl
{
    public AuthInfo(ObjectNode object)
    {
        super(object);
    }

    public String getPrincipalId()
    {
        return getString("principalId");
    }

    public String getPrincipalDomainId()
    {
        return getString("principalDomainId");
    }

    public String getPrincipalName()
    {
        return getString("principalName");
    }

    public String getTenantId()
    {
        return getString("tenantId");
    }

    public String getTenantTitle()
    {
        return getString("tenantTitle");
    }

    public String getTenantDescription()
    {
        return getString("tenantDescription");
    }

    public String getTenantRegistrarId()
    {
        return getString("tenantRegistrarId");
    }

    public String getClientId()
    {
        return getString("clientId");
    }
    
    public String getDirectoryId()
    {
        return getString("directoryId");
    }
    
    public String getIdentityId()
    {
        return getString("identityId");
    }
}
