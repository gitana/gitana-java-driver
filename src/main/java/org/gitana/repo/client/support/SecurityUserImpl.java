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
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.SecurityGroup;
import org.gitana.repo.client.SecurityUser;
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
    public SecurityUserImpl(Gitana gitana, ObjectNode obj, boolean isSaved)
    {
    	super(gitana, obj, isSaved);

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
        String url = "/security/users/" + this.getId() + "/memberships";
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

}
