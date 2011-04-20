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

package org.gitana.repo.client.nodes;

import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Document;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.namespace.QName;

import java.util.List;

/**
 * @author uzi
 */
public interface Node extends Document
{
    // default collection location
    public final static String DEFAULT_COLLECTION_ID = "nodes";

	// additional metadata fields
    public final static String FIELD_FEATURES = "_features";
    public final static String FIELD_QNAME = "_qname";
    public final static String FIELD_TYPE_QNAME = "_type";

    // system metadata
    public final static String SYSTEM_CHANGESET = "changeset";
    public final static String SYSTEM_DELETED = "deleted";

    public Repository getRepository();
    public String getRepositoryId();

    public Branch getBranch();
    public String getBranchId();

    // qualified name
    public QName getQName();

    // type
    public QName getTypeQName();

    // changeset information
    public String getChangesetId();
    //public int getChangesetRev();
            
    // flags
    public boolean isDeleted();

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
     * Uploads an attachment.
     *
     * @param attachmentId
     * @param bytes
     * @param contentType
     */
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType);

    /**
     * Downloads an attachment.
     *
     * @param attachmentId
     *
     * @return attachment
     */
    public byte[] downloadAttachment(String attachmentId);

}