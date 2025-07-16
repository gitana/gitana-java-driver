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
package org.gitana.platform.client.webhost;

import org.gitana.platform.client.support.Selfable;

/**
 * @author uzi
 */
public interface AutoClientMapping extends WebHostDocument, Selfable
{
    // the host
    public final static String FIELD_HOST = "host";

    // the target application id
    public final static String FIELD_APPLICATION_ID = "applicationId";

    // the target application key
    public final static String FIELD_APPLICATION_KEY = "applicationKey";

    // the target client key
    public final static String FIELD_CLIENT_KEY = "clientKey";

    // the target authgrant key
    public final static String FIELD_AUTH_GRANT_KEY = "authGrantKey";

    public String getHost();
    public void setHost(String host);

    public String getApplicationKey();
    public void setApplicationKey(String applicationKey);

    public String getApplicationId();
    public void setApplicationId(String applicationId);

    public String getClientKey();
    public void setClientKey(String clientKey);

    public String getAuthGrantKey();
    public void setAuthGrantKey(String authGrantKey);
}
