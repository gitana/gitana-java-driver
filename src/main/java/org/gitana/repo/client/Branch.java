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

import org.gitana.repo.branch.BranchType;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.services.Definitions;
import org.gitana.repo.client.services.Nodes;

import java.util.List;

/**
 * @author uzi
 */
public interface Branch extends Document
{
    // default collection location
    public final static String DEFAULT_COLLECTION_ID = "branches";
        
    // properties
    public final static String FIELD_ROOT = "root";
    public final static String FIELD_TIP = "tip";
    public final static String FIELD_READONLY = "readonly";
    public final static String FIELD_SNAPSHOT = "snapshot";
    public final static String FIELD_FROZEN = "frozen";

    public final static String FIELD_JOIN_BRANCH = "join-branch";
    public final static String FIELD_ROOT_BRANCH = "root-branch";

    public final static String FIELD_BRANCH_TYPE = "type";

    public Repository getRepository();
    public String getRepositoryId();

    // root
    public void setRootChangesetId(String rootChangesetId);
    public String getRootChangesetId();    

    // current
    public void setTipChangesetId(String tipChangesetId);
    public String getTipChangesetId();
    
    public boolean isReadOnly();
    public boolean isSnapshot();

    public boolean isFrozen();
    
    // helpers
    public String getJoinBranchId();
    public String getRootBranchId();

    public boolean isMaster();
    public BranchType getType();

    public void markDirty();

    /**
     * @return nodes
     */
    public Nodes nodes();

    /**
     * @return definitions
     */
    public Definitions definitions();

    /**
     * Update
     */
    public void update();

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

    public void reload();
}
