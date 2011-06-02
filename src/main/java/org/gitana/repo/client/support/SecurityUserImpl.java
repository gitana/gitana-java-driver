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
import org.gitana.repo.client.types.Person;
import org.gitana.security.PrincipalType;
import org.gitana.util.MD5;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class SecurityUserImpl extends AbstractSecurityPrincipalImpl implements SecurityUser
{
    public SecurityUserImpl(Driver driver, Server server, ObjectNode obj, boolean isSaved)
    {
    	super(driver, server, obj, isSaved);

        init();
    }

    /**
     * Sets the principal type to USER
     */
    protected void init()
    {
        this.setPrincipalType(PrincipalType.USER);
    }

    @Override
    public String getMD5Password()
    {
        return getString(FIELD_MD5_PASSWORD);
    }

    @Override
    public void setPassword(String newPassword)
    {
        if (newPassword != null)
        {
            String passwordHash = MD5.Digest(newPassword.getBytes());

            set(FIELD_MD5_PASSWORD, passwordHash);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // UPDATE AND DELETE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put("/security/users/" + getId(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete("/security/users/" + getId());
    }

    @Override
    public void reload()
    {
        SecurityUser user = getServer().readUser(getId());
        this.reload(user.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MEMBERSHIPS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Map<String, SecurityGroup> fetchParentGroups()
    {
        return fetchParentGroups(false);
    }

    @Override
    public Map<String, SecurityGroup> fetchParentGroups(boolean includeAncestors)
    {
        String url = "/security/users/" + this.getId() + "/memberships";
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
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getFirstName()
    {
        return getString(FIELD_FIRST_NAME);
    }

    @Override
    public void setFirstName(String firstName)
    {
        set(FIELD_FIRST_NAME, firstName);
    }

    @Override
    public String getLastName()
    {
        return getString(FIELD_LAST_NAME);
    }

    @Override
    public void setLastName(String lastName)
    {
        set(FIELD_LAST_NAME,  lastName);
    }

    @Override
    public String getCompanyName()
    {
        return getString(FIELD_COMPANY_NAME);
    }

    @Override
    public void setCompanyName(String companyName)
    {
        set(FIELD_COMPANY_NAME, companyName);
    }

    @Override
    public String getEmail()
    {
        return getString(FIELD_EMAIL);
    }

    @Override
    public void setEmail(String email)
    {
        set(FIELD_EMAIL, email);
    }

    @Override
    public Person readPerson(Branch branch)
    {
        return readPerson(branch, false);
    }

    @Override
    public Person readPerson(Branch branch, boolean createIfNotFound)
    {
        return branch.readPerson(getName(), createIfNotFound);
    }

}
