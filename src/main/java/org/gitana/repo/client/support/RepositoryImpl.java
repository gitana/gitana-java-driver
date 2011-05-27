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
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.RepositoryType;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class RepositoryImpl extends DocumentImpl implements Repository
{
    private Driver driver;
    private Server server;

    protected RepositoryImpl(Driver driver, Server server, ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);

        this.driver = driver;
        this.server = server;
    }

    protected Remote getRemote()
    {
        return driver.getRemote();
    }

    protected ObjectFactory getFactory()
    {
        return driver.getFactory();
    }

    @Override
    public Server getServer()
    {
        return this.server;
    }

    @Override
    public RepositoryType getType()
    {
        return RepositoryType.valueOf(getString(Repository.FIELD_REPOSITORY_TYPE));
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Repository)
        {
            Repository other = (Repository) object;

            equals = (this.getId().equals(other.getId())) && (this.getType().equals(other.getType()));
        }

        return equals;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELF
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put("/repositories/" + getId(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete("/repositories/" + getId());
    }

    @Override
    public void reload()
    {
        Repository repository = getServer().readRepository(getId());
        this.reload(repository.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get("/repositories/" + getId() + "/acl");

        return DriverUtil.toACL(response);
    }


    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/repositories/" + getId() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getId() + "/acl/" + principalId + "/grant/" + authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getId() + "/acl/" + principalId + "/revoke/" + authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BRANCHES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Map<String, Branch> fetchBranches()
    {
        return fetchBranches(null);
    }

    @Override
    public Map<String, Branch> fetchBranches(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/repositories/" + getId() + "/branches", params);
        return getFactory().branches(this, response);
    }

    @Override
    public List<Branch> listBranches()
    {
        return listBranches(null);
    }

    @Override
    public List<Branch> listBranches(Pagination pagination)
    {
        Map<String, Branch> map = fetchBranches(pagination);

        List<Branch> list = new ArrayList<Branch>();
        for (Branch branch : map.values())
        {
            list.add(branch);
        }

        return list;
    }

    @Override
    public Branch readBranch(String branchId)
    {
        Branch branch = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getId() + "/branches/" + branchId);
            branch = getFactory().branch(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return branch;
    }

    @Override
    public Branch createBranch(String changesetId)
    {
        return createBranch(changesetId, JsonUtil.createObject());
    }

    @Override
    public Branch createBranch(String changesetId, ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/repositories/" + getId() + "/branches?changeset=" + changesetId, object);

        String branchId = response.getId();
        return readBranch(branchId);
    }

    @Override
    public Map<String, Branch> queryBranches(ObjectNode query)
    {
        return queryBranches(query, null);
    }

    @Override
    public Map<String, Branch> queryBranches(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/repositories/" + getId() + "/branches/query", params, query);
        return getFactory().branches(this, response);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // FILES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void uploadFile(String filename, byte[] bytes, String contentType)
    {
        try
        {
            getRemote().upload("/repositories/" + this.getId() + "/files/" + filename, bytes, contentType);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteFile(String filename)
    {
        getRemote().delete("/repositories/" + this.getId() + "/files/" + filename);
    }

    @Override
    public byte[] downloadFile(String filename)
    {
        byte[] bytes = null;

        try
        {
            bytes = getRemote().downloadBytes("/repositories/" + this.getId() + "/files/" + filename);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }
}
