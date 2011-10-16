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
import org.gitana.repo.support.RepositoryType;
import org.gitana.repo.support.ResultMap;

import java.util.List;

/**
 * @author uzi
 */
public interface Repository extends Document, AccessControllable, Selfable
{
    public static final String FIELD_REPOSITORY_TYPE = "repositoryType";

    /**
     * @return the repository id
     */
    public String getId();

    /**
     * @return the repository type
     */
    public RepositoryType getType();

    /**
     * @return server
     */
    public Server getServer();

    /**
     * Retrieves branches for the repository
     *
     * @return a map of branch objects keyed by branch id
     */
    public ResultMap<Branch> fetchBranches();

    /**
     * Retrieves branches for the repository
     *
     * @param pagination
     *
     * @return a map of branch objects keyed by branch id
     */
    public ResultMap<Branch> fetchBranches(Pagination pagination);

    /**
     * Retrieves nodes for the branch.
     *
     * @return list of repositories
     */
    public List<Branch> listBranches();

    /**
     * Retrieves nodes for the branch.
     *
     * @param pagination
     *
     * @return list of repositories
     */
    public List<Branch> listBranches(Pagination pagination);

    /**
     * Reads a single branch from the server.
     *
     * @param branchId
     *
     * @return branch
     */
    public Branch readBranch(String branchId);

    /**
     * Creates an empty branch on the server.
     *
     * @param changesetId the root changeset
     * @return branch
     */
    public Branch createBranch(String changesetId);

    /**
     * Creates a branch on the server.
     *
     * @param changesetId the root changeset id
     * @param object
     *
     * @return branch
     */
    public Branch createBranch(String changesetId, ObjectNode object);

    /**
     * Performs a query over the branch index.
     *
     * @param query
     * @return
     */
    public ResultMap<Branch> queryBranches(ObjectNode query);

    /**
     * Performs a query over the branch index.
     *
     * @param query
     * @param pagination
     *
     * @return
     */
    public ResultMap<Branch> queryBranches(ObjectNode query, Pagination pagination);

    /**
     * Uploads a file into the repository file system.
     *
     * @param filename
     * @param bytes
     * @param contentType
     */
    public void uploadFile(String filename, byte[] bytes, String contentType);

    /**
     * Deletes a file from the repository file system.
     *
     * @param filename
     */
    public void deleteFile(String filename);

    /**
     * Downloads a file from the repository file system.
     *
     * @param filename
     *
     * @return the byte array constituting the downloaded file
     */
    public byte[] downloadFile(String filename);


    /**
     * Retrieves changesets for the repository
     *
     * @return a map of changeset objects keyed by changeset id
     */
    public ResultMap<Changeset> fetchChangesets();

    /**
     * Retrieves changesets for the repository
     *
     * @param pagination
     *
     * @return a map of changeset objects keyed by changeset id
     */
    public ResultMap<Changeset> fetchChangesets(Pagination pagination);

    /**
     * Retrieves changesets for the repository.
     *
     * @return list of repositories
     */
    public List<Changeset> listChangesets();

    /**
     * Retrieves changesets for the repository.
     *
     * @param pagination
     *
     * @return list of repositories
     */
    public List<Changeset> listChangesets(Pagination pagination);

    /**
     * Reads a single changeset.
     *
     * @param changesetId
     *
     * @return branch
     */
    public Changeset readChangeset(String changesetId);

    /**
     * Performs a query over the changeset index.
     *
     * @param query
     * @return
     */
    public ResultMap<Changeset> queryChangesets(ObjectNode query);

    /**
     * Performs a query over the changeset index.
     *
     * @param query
     * @param pagination
     *
     * @return
     */
    public ResultMap<Changeset> queryChangesets(ObjectNode query, Pagination pagination);


}