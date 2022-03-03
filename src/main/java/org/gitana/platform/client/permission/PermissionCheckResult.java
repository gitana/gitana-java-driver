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


/**
 * @author uzi
 */
public class PermissionCheckResult extends PermissionCheck
{
    public final static String FIELD_RESULT = "result";

    public PermissionCheckResult(ObjectNode object)
    {
        super(object);
    }

    public boolean getResult()
    {
        return getBoolean(FIELD_RESULT);
    }

}