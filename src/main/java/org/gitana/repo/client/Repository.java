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

import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.services.Branches;
import org.gitana.repo.client.services.Changesets;
import org.gitana.repo.support.RepositoryType;

import java.util.List;

/**
 * @author uzi
 */
public interface Repository extends Document
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
     * @return branches
     */
    public Branches branches();

    /**
     * @return changesets
     */
    public Changesets changesets();

    /**
     * Update
     */
    public void update();

    /**
     * Delete
     */
    public void delete();

    /**
     * @return access control list
     */
    public ACL getACL();

    /**
     * Retrieve the authorities that a principal has.
     *
     * @param principalId
     * @return list
     */
    public List<String> getAuthorities(String principalId);

    /**
     * Grants an authority to a principal.
     *
     * @param principalId
     * @param authorityId
     */
    public void grant(String principalId, String authorityId);

    /**
     * Revokes an authority for a principal.
     *
     * @param principalId
     * @param authorityId
     */
    public void revoke(String principalId, String authorityId);

    /**
     * Revoke all authorities for a principal.
     *
     * @param principalId
     */
    public void revokeAll(String principalId);

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

}