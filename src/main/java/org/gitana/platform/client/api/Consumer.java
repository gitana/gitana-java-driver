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

import org.gitana.platform.client.platform.PlatformDocument;

import java.util.List;

/**
 * @author uzi
 */
public interface Consumer extends PlatformDocument
{
    public final static String FIELD_AUTH_TYPE = "authType";
    public final static String FIELD_KEY = "key";
    public final static String FIELD_SECRET = "secret";

    public final static String FIELD_DOMAIN_URLS = "domainUrls";
    public final static String FIELD_AUTHORITIES = "authorities";

    public final static String FIELD_ALLOW_TICKET_AUTHENTICATION = "allowTicketAuthentication";
    public final static String FIELD_ALLOW_OPENDRIVER_AUTHENTICATION = "allowOpenDriverAuthentication";

    public final static String FIELD_IS_TENANT_DEFAULT = "isTenantDefault";
    public final static String FIELD_DEFAULT_TENANT_ID = "defaultTenantId";

    public String getAuthType();
    public void setAuthType(String authType);

    public String getKey();
    public void setKey(String key);

    public String getSecret();
    public void setSecret(String secret);

    public List<String> getDomainUrls();
    public void setDomainUrls(List<String> domainUrls);

    public List<String> getAuthorities();
    public void setAuthorities(List<String> authorities);

    public boolean getAllowTicketAuthentication();
    public void setAllowTicketAuthentication(boolean allowTicketAuthentication);

    public boolean getAllowOpenDriverAuthentication();
    public void setAllowOpenDriverAuthentication(boolean allowOpenDriverAuthentication);

    public boolean getIsTenantDefault();
    public void setIsTenantDefault(boolean isTenantDefault);

    public String getDefaultTenantId();
    public void setDefaultTenantId(String tenantId);

}
