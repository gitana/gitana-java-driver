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

import org.gitana.repo.authority.AuthorityGrant;
import org.gitana.repo.client.beans.ACL;

import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public interface AccessControllable
{
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
     * Checks whether the principal has the given authority over this object.
     *
     * @param principalId
     * @param authorityId
     * @return
     */
    public boolean hasAuthority(String principalId, String authorityId);

    /**
     * Acquires a map of authority grants for a set of principals.
     *
     * @param principalIds
     * @return
     */
    public Map<String, AuthorityGrant> getAuthorityGrants(List<String> principalIds);
}
