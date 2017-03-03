/**
 * Copyright 2017 Gitana Software, Inc.
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
 *   info@cloudcms.com
 */

package org.gitana.platform.client.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.client.datastore.DataStore;
import org.gitana.platform.client.log.LogEntry;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

import java.util.List;

/**
 * @author uzi
 */
public interface Repository extends PlatformDataStore
{
    /**
     * Retrieves branches for the repository
     *
     * @return a map of branch objects keyed by branch id
     */
    public ResultMap<Branch> listBranches();

    /**
     * Retrieves branches for the repository
     *
     * @param pagination
     *
     * @return a map of branch objects keyed by branch id
     */
    public ResultMap<Branch> listBranches(Pagination pagination);

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
     * Check branch permissions.
     *
     * @param list
     * @return
     */
    public PermissionCheckResults checkBranchPermissions(List<PermissionCheck> list);

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
    public ResultMap<Changeset> listChangesets();

    /**
     * Retrieves changesets for the repository
     *
     * @param pagination
     *
     * @return a map of changeset objects keyed by changeset id
     */
    public ResultMap<Changeset> listChangesets(Pagination pagination);

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