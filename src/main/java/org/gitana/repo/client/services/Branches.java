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
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Response;
import org.gitana.util.JsonUtil;

import java.util.Map;

/**
 * @author uzi
 */
public class Branches extends AbstractService
{
    private Repository repository;

    public Branches(Gitana gitana, Repository repository)
    {
        super(gitana);

        this.repository = repository;
    }

    public Repository getRepository()
    {
        return this.repository;
    }

    public String getRepositoryId()
    {
        return getRepository().getId();
    }

    /**
     * Retrieves branches for the repository
     *
     * @return a map of branch objects keyed by branch id
     */
    public Map<String, Branch> list()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches");

        return getFactory().branches(getRepository(), response);
    }

    /**
     * Reads a single branch from the server.
     *
     * @param branchId
     *
     * @return branch
     */
    public Branch read(String branchId)
    {
        Branch branch = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + branchId);
            branch = getFactory().branch(getRepository(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return branch;
    }

    /**
     * Creates an empty branch on the server.
     *
     * @param changesetId the root changeset
     * @return branch
     */
    public Branch create(String changesetId)
    {
        return create(changesetId, JsonUtil.createObject());
    }

    /**
     * Creates a branch on the server.
     *
     * @param changesetId the root changeset id
     * @param object
     *
     * @return branch
     */
    public Branch create(String changesetId, ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/repositories/" + getRepositoryId() + "/branches?changeset=" + changesetId, object);

        String branchId = response.getId();
        return read(branchId);
    }

    /**
     * Updates a branch.
     *
     * @param branch
     */
    public void update(Branch branch)
    {
        getRemote().put("/repositories/" + getRepositoryId() + "/branches/" + branch.getId(), branch.getObject());
    }

    /**
     * Deletes a branch.
     *
     * NOTE: this isn't yet implemented
     *
     * @param branch
     */
    /*
    public void delete(Branch branch)
    {
        delete(branch.getId());
    }
    */

    /**
     * Deletes a branch.
     *
     * NOTE: this isn't yet implemented
     *
     * @param branchId
     */
    /*
    public void delete(String branchId)
    {
        getRemote().delete("/repositories/" + getRepositoryId() + "/branches/" + branchId);
    }
    */


}
