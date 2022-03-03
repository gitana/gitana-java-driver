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
 *   info@cloudcms.com
 */
package org.gitana.platform.client.permission;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.GitanaObjectImpl;

/**
 * @author uzi
 */
public class PermissionCheck extends GitanaObjectImpl
{
    public final static String FIELD_PERMISSIONED_ID = "permissionedId";
    public final static String FIELD_PRINCIPAL_ID = "principalId";
    public final static String FIELD_PERMISSION_ID = "permissionId";

    public PermissionCheck(ObjectNode object)
    {
        super(object);
    }

    public PermissionCheck(String permissionedId, String principalId, String permissionId)
    {
        super();

        set(FIELD_PERMISSIONED_ID, permissionedId);
        set(FIELD_PRINCIPAL_ID, principalId);
        set(FIELD_PERMISSION_ID, permissionId);
    }

    public String getPermissionedId()
    {
        return getString(FIELD_PERMISSIONED_ID);
    }

    public String getPrincipalId()
    {
        return getString(FIELD_PRINCIPAL_ID);
    }

    public String getPermissionId()
    {
        return getString(FIELD_PERMISSION_ID);
    }

}