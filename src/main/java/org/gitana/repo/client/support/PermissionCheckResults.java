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

package org.gitana.repo.client.support;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.support.GitanaObjectImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class PermissionCheckResults extends GitanaObjectImpl
{
    private Map<String, PermissionCheckResult> map;

    public PermissionCheckResults(ObjectNode object)
    {
        super(object);
    }

    public synchronized Map<String, PermissionCheckResult> map()
    {
        if (map == null)
        {
            Map<String, PermissionCheckResult> theMap = new HashMap<String, PermissionCheckResult>();

            ArrayNode array = (ArrayNode) getObject().get("results");
            for (int i = 0; i < array.size(); i++)
            {
                ObjectNode object = (ObjectNode) array.get(i);

                PermissionCheckResult result = new PermissionCheckResult(object);

                String key = result.getPermissionedId() + "_" + result.getPrincipalId() + "_" + result.getPermissionId();

                theMap.put(key, result);
            }

            map = theMap;
        }

        return map;
    }

    public boolean check(String permissionedId, String principalId, String permissionId)
    {
        boolean check = false;

        String key = permissionedId + "_" + principalId + "_" + permissionId;

        PermissionCheckResult result = map().get(key);
        if (result != null)
        {
            check = result.getResult();
        }

        return check;
    }

}