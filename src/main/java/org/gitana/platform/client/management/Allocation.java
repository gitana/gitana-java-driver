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

package org.gitana.platform.client.management;

/**
 * @author uzi
 */
public interface Allocation extends ManagementDocument
{
    // fields
    public final static String FIELD_TENANT_ID = "tenantId";
    public final static String FIELD_OBJECT_TYPE = "objectType";
    public final static String FIELD_OBJECT_ID = "objectId";

    public String getTenantId();
    public void setTenantId(String tenantId);

    public String getObjectType();
    public void setObjectType(String objectType);

    public String getObjectId();
    public void setObjectId(String objectId);
}
