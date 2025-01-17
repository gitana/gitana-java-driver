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
 *   info@gitana.io
 */
package org.gitana.platform.client.principal;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.type.Group;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.services.principals.PrincipalType;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;

/**
 * @author uzi
 */
public class DomainGroupImpl extends AbstractDomainPrincipalImpl implements DomainGroup
{
    public DomainGroupImpl(Domain domain, ObjectNode obj, boolean isSaved)
    {
        super(domain, obj, isSaved);

        init();
    }

    /**
     * Sets the principal type to GROUP
     */
    protected void init()
    {
        this.setType(PrincipalType.GROUP);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_DOMAIN_GROUP;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MEMBERS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<DomainPrincipal> listPrincipals()
    {
        return listPrincipals(false);
    }

    @Override
    public ResultMap<DomainPrincipal> listPrincipals(boolean includeInherited)
    {
        String url = getResourceUri() + "/members";
        if (includeInherited)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        return getFactory().domainPrincipals(getPlatform(), response);
    }

    @Override
    public void addPrincipal(DomainPrincipal principal)
    {
        addPrincipal(principal.getDomainQualifiedId());
    }

    @Override
    public void addPrincipal(String principalId)
    {
        getRemote().post(getResourceUri() + "/members/add?id=" + principalId);
    }

    @Override
    public void removePrincipal(DomainPrincipal principal)
    {
        removePrincipal(principal.getDomainQualifiedId());
    }

    @Override
    public void removePrincipal(String principalId)
    {
        getRemote().post(getResourceUri() + "/members/remove?id=" + principalId);
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
