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
import org.gitana.repo.branch.BranchType;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.services.Definitions;
import org.gitana.repo.client.services.Nodes;
import org.gitana.repo.client.util.DriverUtil;

import java.util.List;

/**
 * @author uzi
 */
public class BranchImpl extends DocumentImpl implements Branch
{
    private Gitana gitana;
    private Repository repository;

    protected BranchImpl(Gitana gitana, Repository repository, ObjectNode obj, boolean isSaved)
    {
    	super(obj, isSaved);

        this.gitana = gitana;
        this.repository = repository;
    }

    protected Remote getRemote()
    {
        return gitana.getRemote();
    }

    @Override
    public Repository getRepository()
    {
        return this.repository;
    }

    @Override
    public String getRepositoryId()
    {
        return getRepository().getId();
    }
    
    @Override
    public void setTipChangesetId(String changesetId)
    {
        set(FIELD_TIP, changesetId);
    }
        
    @Override
    public String getTipChangesetId()
    {
    	return getString(FIELD_TIP);
    }
    
    @Override
	public void setRootChangesetId(String rootChangesetId) 
	{
    	set(FIELD_ROOT, rootChangesetId);
	}
	
    @Override
    public String getRootChangesetId() 
    {
        return getString(FIELD_ROOT);
    }
    
    @Override
    public boolean isReadOnly()
    {
    	return getBoolean(FIELD_READONLY);
    }
    
    @Override
    public boolean isSnapshot()
    {
    	return getBoolean(FIELD_SNAPSHOT);
    }

	@Override
	public boolean isFrozen() 
	{
		return getBoolean(Branch.FIELD_FROZEN);
	}

    @Override
    public String getJoinBranchId()
    {
        return getString(FIELD_JOIN_BRANCH);
    }

    @Override
    public String getRootBranchId()
    {
        return getString(FIELD_ROOT_BRANCH);
    }

    @Override
    public boolean isMaster()
    {
        return BranchType.MASTER.equals(getType());
    }

    @Override
    public BranchType getType()
    {
        BranchType type = null;

        String typeId = getString(FIELD_BRANCH_TYPE);
        if (typeId != null)
        {
            type = BranchType.valueOf(typeId);
        }

        return type;
    }


    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Branch)
        {
            Branch other = (Branch) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    @Override
    public Nodes nodes()
    {
        return new Nodes(this.gitana, this);
    }

    @Override
    public Definitions definitions()
    {
        return new Definitions(this.gitana, this);
    }

    @Override
    public void update()
    {
        getRemote().put("/repositories/" + getRepositoryId() + "/branches/" + getId(), getObject());
    }

    /**
     * @return access control list
     */
    public ACL getACL()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/acl");

        return DriverUtil.toACL(response);
    }

    /**
     * Retrieve the authorities that a principal has.
     *
     * @param principalId
     * @return list
     */
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    /**
     * Grants an authority to a principal.
     *
     * @param principalId
     * @param authorityId
     */
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/acl/" + principalId + "/grant/" + authorityId);
    }

    /**
     * Revokes an authority for a principal.
     *
     * @param principalId
     * @param authorityId
     */
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/acl/" + principalId + "/revoke/" + authorityId);
    }

    /**
     * Revoke all authorities for a principal.
     *
     * @param principalId
     */
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

}
