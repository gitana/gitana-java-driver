/**
 * Copyright 2016 Gitana Software, Inc.
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
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.Selfable;

import java.util.Collection;
import java.util.List;

/**
 * @author uzi
 */
public interface Client extends PlatformDocument, AccessControllable, Selfable
{
    // client key and secret
    public final static String FIELD_KEY = "key";
    public final static String FIELD_SECRET = "secret";

    // supported grant types
    public final static String FIELD_AUTHORIZED_GRANT_TYPES = "authorizedGrantTypes";

    // required scopes
    public final static String FIELD_SCOPE = "scope";

    // for Authentication Code grant, the registered redirect uri
    public final static String FIELD_REGISTERED_REDIRECT_URI = "registeredRedirectUri";

    // for any grant, constrain the domain URL (usually REFERRER) to a set of domains
    public final static String FIELD_DOMAIN_URLS = "domainUrls";

    // whether this is a tenant's default client
    public final static String FIELD_IS_TENANT_DEFAULT = "isTenantDefault";
    public final static String FIELD_DEFAULT_TENANT_ID = "defaultTenantId";

    public final static String FIELD_ALLOW_OPENDRIVER_AUTHENTICATION = "allowOpenDriverAuthentication";

    public final static String FIELD_ENABLED = "enabled";

    public final static String FIELD_ALLOW_AUTO_APPROVAL_FOR_IMPLICIT_FLOW = "allowAutoApprovalForImplicitFlow";

    // access and refresh token expiration
    public final static String FIELD_ACCESS_TOKEN_VALIDITY_SECONDS = "accessTokenValiditySeconds";
    public final static String FIELD_REFRESH_TOKEN_VALIDITY_SECONDS = "refreshTokenValiditySeconds";

    // whether to allow guest login for this client?
    public final static String FIELD_ALLOW_GUEST_LOGIN = "allowGuestLogin";


    public String getKey();
    public void setKey(String key);

    public String getSecret();
    public void setSecret(String secret);

    public List<String> getDomainUrls();
    public void setDomainUrls(List<String> domainUrls);

    public boolean getIsTenantDefault();
    public void setIsTenantDefault(boolean isTenantDefault);

    public String getDefaultTenantId();
    public void setDefaultTenantId(String tenantId);

    public Collection<String> getAuthorizedGrantTypes();
    public void setAuthorizedGrantTypes(Collection<String> authorizedGrantTypes);

    public Collection<String> getScope();
    public void setScope(Collection<String> scope);

    public String getRegisteredRedirectUri();
    public void setRegisteredRedirectUri(String registeredRedirectUri);

    public boolean getAllowOpenDriverAuthentication();
    public void setAllowOpenDriverAuthentication(boolean allowOpenDriverAuthentication);

    public boolean getEnabled();
    public void setEnabled(boolean enabled);

    public boolean getAllowAutoApprovalForImplicitFlow();
    public void setAllowAutoApprovalForImplicitFlow(boolean allowAutoApprovalForImplicitFlow);

    public int getAccessTokenValiditySeconds();
    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds);

    public int getRefreshTokenValiditySeconds();
    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds);

    public boolean getAllowGuestLogin();
    public void setAllowGuestLogin(boolean allowGuestLogin);
}
