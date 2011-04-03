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
import org.gitana.repo.client.*;
import org.gitana.security.PrincipalType;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class Users extends AbstractService
{
    public Users(Gitana gitana)
    {
        super(gitana);
    }

    public Map<String, SecurityUser> map()
    {
        Response response = getRemote().get("/security/users");

        return getFactory().securityUsers(response);
    }

    public List<SecurityUser> list()
    {
        Map<String, SecurityUser> map = map();

        List<SecurityUser> list = new ArrayList<SecurityUser>();
        for (SecurityUser user : map.values())
        {
            list.add(user);
        }

        return list;
    }

    public SecurityUser read(String userId)
    {
        SecurityUser user = null;

        try
        {
            Response response = getRemote().get("/security/users/" + userId);
            user = getFactory().securityUser(response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return user;
    }

    public SecurityUser create(String userId, String password)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put(SecurityUser.FIELD_PRINCIPAL_ID, userId);

        SecurityUser user = getFactory().securityUser(object);
        user.setPassword(password);

        return create(user.getObject());
    }

    public SecurityUser create(ObjectNode object)
    {
        if (object == null)
        {
            throw new RuntimeException("Object required");
        }

        // ensure object has principal id and password
        if (!object.has(SecurityUser.FIELD_PRINCIPAL_ID))
        {
            throw new RuntimeException("Missing principal id");
        }
        if (!object.has(SecurityUser.FIELD_MD5_PASSWORD))
        {
            throw new RuntimeException("Missing MD5 password");
        }

        object.put(SecurityUser.FIELD_PRINCIPAL_TYPE, PrincipalType.USER.toString());

        Response response = getRemote().post("/security/users", object);

        // NOTE: "_doc" doesn't come back for the user...
        //String userId = response.getId();
        String userId = object.get(SecurityUser.FIELD_PRINCIPAL_ID).getTextValue();
        return read(userId);
    }

    public void update(SecurityUser user)
    {
        getRemote().put("/security/users/" + user.getId(), user.getObject());
    }

    public void delete(SecurityUser user)
    {
        delete(user.getId());
    }

    public void delete(String userId)
    {
        getRemote().delete("/security/users/" + userId);
    }

    public Map<String, SecurityGroup> membershipMap(SecurityUser user)
    {
        return membershipMap(user.getId());
    }

    public Map<String, SecurityGroup> membershipMap(String userId)
    {
        return membershipMap(userId, false);
    }

    public Map<String, SecurityGroup> membershipMap(SecurityUser user, boolean includeIndirectMemberships)
    {
        return membershipMap(user.getId(), includeIndirectMemberships);
    }

    public Map<String, SecurityGroup> membershipMap(String userId, boolean includeIndirectMemberships)
    {
        String url = "/security/users/" + userId + "/memberships";
        if (includeIndirectMemberships)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        return getFactory().securityGroups(response);
    }

    public List<SecurityGroup> membershipList(SecurityUser user)
    {
        return membershipList(user.getId());
    }

    public List<SecurityGroup> membershipList(String userId)
    {
        return membershipList(userId, false);
    }

    public List<SecurityGroup> membershipList(SecurityUser user, boolean includeIndirectMemberships)
    {
        return membershipList(user.getId(), includeIndirectMemberships);
    }

    public List<SecurityGroup> membershipList(String userId, boolean includeIndirectMemberships)
    {
        Map<String, SecurityGroup> map = membershipMap(userId);

        List<SecurityGroup> list = new ArrayList<SecurityGroup>();
        for (SecurityGroup group : map.values())
        {
            list.add(group);
        }

        return list;
    }

}
