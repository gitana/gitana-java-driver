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
import org.gitana.repo.client.types.Group;
import org.gitana.repo.support.ResultMap;
import org.gitana.security.PrincipalType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class SecurityGroupImpl extends AbstractSecurityPrincipalImpl implements SecurityGroup
{
    public SecurityGroupImpl(Driver driver, Server server, ObjectNode obj, boolean isSaved)
    {
        super(driver, server, obj, isSaved);

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

    @Override
    public void reload()
    {
        SecurityGroup group = getServer().readGroup(getId());
        this.reload(group.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MEMBERSHIPS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<SecurityGroup> fetchParentGroups()
    {
        return fetchParentGroups(false);
    }

    @Override
    public ResultMap<SecurityGroup> fetchParentGroups(boolean includeAncestors)
    {
        String url = "/security/groups/" + this.getId() + "/memberships";
        if (includeAncestors)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        return getFactory().securityGroups(getServer(), response);
    }

    @Override
    public List<SecurityGroup> listParentGroups()
    {
        return listParentGroups(false);
    }

    @Override
    public List<SecurityGroup> listParentGroups(boolean includeAncestors)
    {
        Map<String, SecurityGroup> map = fetchParentGroups(includeAncestors);

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
    public ResultMap<SecurityPrincipal> fetchPrincipals()
    {
        return fetchPrincipals(false);
    }

    @Override
    public ResultMap<SecurityPrincipal> fetchPrincipals(boolean includeInherited)
    {
        String url = "/security/groups/" + getId() + "/members";
        if (includeInherited)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        return getFactory().securityPrincipals(getServer(), response);
    }

    @Override
    public List<SecurityPrincipal> listPrincipals()
    {
        return listPrincipals(false);
    }

    @Override
    public List<SecurityPrincipal> listPrincipals(boolean includeInherited)
    {
        Map<String, SecurityPrincipal> map = fetchPrincipals(includeInherited);

        List<SecurityPrincipal> list = new ArrayList<SecurityPrincipal>();
        for (SecurityPrincipal principal : map.values())
        {
            list.add(principal);
        }

        return list;
    }

    @Override
    public void addPrincipal(SecurityPrincipal principal)
    {
        addPrincipal(principal.getId());
    }

    @Override
    public void addPrincipal(String principalId)
    {
        getRemote().post("/security/groups/" + getId() + "/add/" + principalId);
    }

    @Override
    public void removePrincipal(SecurityPrincipal principal)
    {
        removePrincipal(principal.getId());
    }

    @Override
    public void removePrincipal(String principalId)
    {
        getRemote().post("/security/groups/" + getId() + "/remove/" + principalId);
    }

    @Override
    public Group readGroup(Branch branch)
    {
        return readGroup(branch, false);
    }

    @Override
    public Group readGroup(Branch branch, boolean createIfNotFound)
    {
        return branch.readGroup(getName(), createIfNotFound);
    }
}
