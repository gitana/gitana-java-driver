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

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.*;
import org.gitana.security.PrincipalType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class SecurityGroupImpl extends AbstractSecurityPrincipalImpl implements SecurityGroup
{
    public SecurityGroupImpl(Gitana gitana, ObjectNode obj, boolean isSaved)
    {
        super(gitana, obj, isSaved);

        init();
    }

    /**
     * Sets the principal type to GROUP
     */
    protected void init()
    {
        this.setPrincipalType(PrincipalType.GROUP);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // UPDATE AND DELETE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put("/security/groups/" + getId(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete("/security/groups/" + getId());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MEMBERSHIPS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Map<String, SecurityGroup> parentMap()
    {
        return parentMap(false);
    }

    @Override
    public Map<String, SecurityGroup> parentMap(boolean includeAncestors)
    {
        String url = "/security/groups/" + this.getId() + "/memberships";
        if (includeAncestors)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        return getFactory().securityGroups(response);
    }

    @Override
    public List<SecurityGroup> parentList()
    {
        return parentList(false);
    }

    @Override
    public List<SecurityGroup> parentList(boolean includeAncestors)
    {
        Map<String, SecurityGroup> map = parentMap(includeAncestors);

        List<SecurityGroup> list = new ArrayList<SecurityGroup>();
        for (SecurityGroup group : map.values())
        {
            list.add(group);
        }

        return list;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MEMBERS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Map<String, SecurityPrincipal> childMap()
    {
        return childMap(false);
    }

    @Override
    public Map<String, SecurityPrincipal> childMap(boolean includeInherited)
    {
        String url = "/security/groups/" + getId() + "/members";
        if (includeInherited)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        return getFactory().securityPrincipals(response);
    }

    @Override
    public List<SecurityPrincipal> childList()
    {
        return childList(false);
    }

    @Override
    public List<SecurityPrincipal> childList(boolean includeInherited)
    {
        Map<String, SecurityPrincipal> map = childMap(includeInherited);

        List<SecurityPrincipal> list = new ArrayList<SecurityPrincipal>();
        for (SecurityPrincipal principal : map.values())
        {
            list.add(principal);
        }

        return list;
    }

    @Override
    public void add(SecurityPrincipal principal)
    {
        add(principal.getId());
    }

    @Override
    public void add(String principalId)
    {
        getRemote().post("/security/groups/" + getId() + "/add/" + principalId);
    }

    @Override
    public void remove(SecurityPrincipal principal)
    {
        remove(principal.getId());
    }

    @Override
    public void remove(String principalId)
    {
        getRemote().post("/security/groups/" + getId() + "/remove/" + principalId);
    }

}
