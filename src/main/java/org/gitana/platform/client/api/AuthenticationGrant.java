/**
 * Copyright 2025 Gitana Software, Inc.
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


import org.gitana.platform.client.platform.PlatformDocument;

/**
 * @author uzi
 */
public interface AuthenticationGrant extends PlatformDocument
{
    // fields
    public final static String FIELD_KEY = "key";
    public final static String FIELD_SECRET = "secret";

    // principal
    public final static String FIELD_PRINCIPAL_DOMAIN_ID = "principalDomainId";
    public final static String FIELD_PRINCIPAL_ID = "principalId";

    // consumer
    public final static String FIELD_CLIENT_ID = "clientId";

    // is this grant enabled?
    public final static String FIELD_ENABLED = "enabled";

    public String getKey();
    public void setKey(String key);

    public String getSecret();
    public void setSecret(String secret);

    public String getPrincipalId();
    public void setPrincipalId(String principalId);

    public String getPrincipalDomainId();
    public void setPrincipalDomainId(String principalDomainId);

    public String getClientId();
    public void setClientId(String clientId);

    public boolean getEnabled();
    public void setEnable(boolean enabled);

}
