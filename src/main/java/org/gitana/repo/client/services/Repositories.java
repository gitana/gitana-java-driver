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
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Response;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class Repositories extends AbstractService
{
    public Repositories(Gitana gitana)
    {
        super(gitana);
    }

    /**
     * Retrieves repositories from the server as a map.
     *
     * @return a map of repository objects keyed by repository id
     */
    public Map<String, Repository> map()
    {
        Response response = getRemote().get("/repositories");

        return getFactory().repositories(response);
    }

    /**
     * Retrieves repositories from the server as a list.
     *
     * @return list of repositories
     */
    public List<Repository> list()
    {
        Map<String, Repository> map = map();

        List<Repository> list = new ArrayList<Repository>();
        for (Repository repository : map.values())
        {
            list.add(repository);
        }

        return list;
    }

    /**
     * Reads a single repository from the server.
     *
     * @param repositoryId
     *
     * @return repository
     */
    public Repository read(String repositoryId)
    {
        Repository repository = null;

        try
        {
            Response response = getRemote().get("/repositories/" + repositoryId);
            repository = getFactory().repository(response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return repository;
    }

    /**
     * Creates an empty repository on the server.
     *
     * @return repository
     */
    public Repository create()
    {
        return create(JsonUtil.createObject());
    }

    /**
     * Creates a repository on the server.
     *
     * @param object
     *
     * @return repository
     */
    public Repository create(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/repositories", object);

        String repositoryId = response.getId();
        return read(repositoryId);
    }

    /**
     * Updates a repository.
     *
     * @param repository
     */
    public void update(Repository repository)
    {
        getRemote().put("/repositories/" + repository.getId(), repository.getObject());
    }

    /**
     * Deletes a repository.
     *
     * @param repository
     */
    public void delete(Repository repository)
    {
        delete(repository.getId());
    }

    /**
     * Deletes a repository.
     *
     * @param repositoryId
     */
    public void delete(String repositoryId)
    {
        getRemote().delete("/repositories/" + repositoryId);
    }


}
