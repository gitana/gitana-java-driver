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

package org.gitana.platform.client.warehouse;

import org.gitana.platform.client.support.Selfable;

/**
 * @author uzi
 */
public interface InteractionApplication extends WarehouseDocument, Selfable
{
    public final static String FIELD_APPLICATION_ID = "applicationId";
    public final static String FIELD_KEY = "key";
    public final static String FIELD_URL = "url";
    public final static String FIELD_HOST = "host";
    public final static String FIELD_PORT = "port";
    public final static String FIELD_PROTOCOL = "protocol";

    public String getApplicationId();
    public void setApplicationId(String applicationId);

    public String getKey();
    public void setKey(String key);

    public String getHost();
    public void setHost(String host);

    public int getPort();
    public void setPort(int port);

    public String getProtocol();
    public void setProtocol(String protocol);

    public String getUrl();
    public void setUrl(String url);
}
