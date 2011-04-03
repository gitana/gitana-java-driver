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

package org.gitana.repo.client.services;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.SecurityGroup;
import org.gitana.security.PrincipalType;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class Groups extends AbstractService
{
    public Groups(Gitana gitana)
    {
        super(gitana);
    }

    public Map<String, SecurityGroup> map()
    {
        Response response = getRemote().get("/security/groups");

        return getFactory().securityGroups(response);
    }

    public List<SecurityGroup> list()
    {
        Map<String, SecurityGroup> map = map();

        List<SecurityGroup> list = new ArrayList<SecurityGroup>();
        for (SecurityGroup group : map.values())
        {
            list.add(group);
        }

        return list;
    }

    public SecurityGroup read(String groupId)
    {
        SecurityGroup group = null;

        try
        {
            Response response = getRemote().get("/security/groups/" + groupId);
            group = getFactory().securityGroup(response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return group;
    }

    public SecurityGroup create(String groupId)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put(SecurityGroup.FIELD_PRINCIPAL_ID, groupId);

        return create(object);
    }

    public SecurityGroup create(ObjectNode object)
    {
        if (object == null)
        {
            throw new RuntimeException("Object required");
        }

        // ensure object has principal id
        if (!object.has(SecurityGroup.FIELD_PRINCIPAL_ID))
        {
            throw new RuntimeException("Missing principal id");
        }

        object.put(SecurityGroup.FIELD_PRINCIPAL_TYPE, PrincipalType.GROUP.toString());

        Response response = getRemote().post("/security/groups", object);

        // NOTE: "_doc" doesn't come back in response?
        //String groupId = response.getId();
        String groupId = object.get(SecurityGroup.FIELD_PRINCIPAL_ID).getTextValue();
        return read(groupId);
    }
}
