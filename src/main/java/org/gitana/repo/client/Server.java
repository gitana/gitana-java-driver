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

package org.gitana.repo.client;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.ResultMap;

import java.util.List;

/**
 * @author uzi
 */
public interface Server extends AccessControllable
{
    /**
     * Retrieves repositories from the server as a map.
     *
     * @return a map of repository objects keyed by repository id
     */
    public ResultMap<Repository> fetchRepositories();

    /**
     * Retrieves repositories from the server as a map.
     *
     * @param pagination
     *
     * @return a map of repository objects keyed by repository id
     */
    public ResultMap<Repository> fetchRepositories(Pagination pagination);

    /**
     * Retrieves repositories from the server as a list.
     *
     * @return list of repositories
     */
    public List<Repository> listRepositories();

    /**
     * Retrieves repositories from the server as a list.
     *
     * @param pagination
     *
     * @return list of repositories
     */
    public List<Repository> listRepositories(Pagination pagination);

    /**
     * Reads a single repository from the server.
     *
     * @param repositoryId
     *
     * @return repository
     */
    public Repository readRepository(String repositoryId);

    /**
     * Creates an empty repository on the server.
     *
     * @return repository
     */
    public Repository createRepository();

    /**
     * Creates a repository on the server.
     *
     * @param object
     *
     * @return repository
     */
    public Repository createRepository(ObjectNode object);

    /**
     * Performs a query over the repository index.
     *
     * @param query
     * @return
     */
    public ResultMap<Repository> queryRepositories(ObjectNode query);

    public ResultMap<Repository> queryRepositories(ObjectNode query, Pagination pagination);

    public ResultMap<SecurityGroup> fetchGroups();

    public ResultMap<SecurityGroup> fetchGroups(Pagination pagination);

    public List<SecurityGroup> listGroups();

    public List<SecurityGroup> listGroups(Pagination pagination);

    public SecurityGroup readGroup(String groupId);

    public SecurityGroup createGroup(String groupId);

    public SecurityGroup createGroup(ObjectNode object);

    public ResultMap<SecurityUser> fetchUsers();

    public ResultMap<SecurityUser> fetchUsers(Pagination pagination);

    public List<SecurityUser> listUsers();

    public List<SecurityUser> listUsers(Pagination pagination);

    public SecurityUser readUser(String userId);

    public SecurityUser createUser(String userId, String password);

    public SecurityUser createUser(ObjectNode object);

    public void updateUser(SecurityUser user);

    public void deleteUser(SecurityUser user);

    public void deleteUser(String userId);

    public ResultMap<SecurityGroup> fetchMemberships(SecurityUser user);

    public ResultMap<SecurityGroup> fetchMemberships(String userId);

    public ResultMap<SecurityGroup> fetchMemberships(SecurityUser user, boolean includeIndirectMemberships);

    public ResultMap<SecurityGroup> fetchMemberships(String userId, boolean includeIndirectMemberships);

    public List<SecurityGroup> listMemberships(SecurityUser user);

    public List<SecurityGroup> listMemberships(String userId);

    public List<SecurityGroup> listMemberships(SecurityUser user, boolean includeIndirectMemberships);

    public List<SecurityGroup> listMemberships(String userId, boolean includeIndirectMemberships);


    ////////////////////
    // JOBS
    ////////////////////

    public ResultMap<Job> queryJobs(ObjectNode query);

    public ResultMap<Job> queryJobs(ObjectNode query, Pagination pagination);

    public Job readJob(String jobId);

}