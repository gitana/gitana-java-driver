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

package org.gitana.platform.client.domain;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.client.principal.DomainGroup;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.services.principals.PrincipalType;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

import java.util.List;

/**
 * @author uzi
 */
public interface Domain extends PlatformDataStore
{
    public ResultMap<DomainPrincipal> listPrincipals();
    public ResultMap<DomainPrincipal> listPrincipals(Pagination pagination);

    public DomainPrincipal readPrincipal(String principalId);

    public DomainPrincipal createPrincipal(PrincipalType type, ObjectNode object);

    public DomainUser createUser(String name, String password);
    public DomainGroup createGroup(String name);

    public ResultMap<DomainPrincipal> queryPrincipals(ObjectNode query);
    public ResultMap<DomainPrincipal> queryPrincipals(ObjectNode query, Pagination pagination);

    public void updatePrincipal(DomainPrincipal principal);

    public void deletePrincipal(DomainPrincipal principal);
    public void deletePrincipal(String principalId);

    public ResultMap<DomainGroup> listMemberships(DomainPrincipal principal);
    public ResultMap<DomainGroup> listMemberships(String principalId);
    public ResultMap<DomainGroup> listMemberships(DomainPrincipal principal, boolean includeIndirectMemberships);
    public ResultMap<DomainGroup> listMemberships(String principalId, boolean includeIndirectMemberships);

    public PermissionCheckResults checkPrincipalPermissions(List<PermissionCheck> list);

    public ResultMap<DomainUser> listUsers();
    public ResultMap<DomainUser> listUsers(Pagination pagination);

    public ResultMap<DomainGroup> listGroups();
    public ResultMap<DomainGroup> listGroups(Pagination pagination);

    public ResultMap<DomainUser> queryUsers(ObjectNode query);
    public ResultMap<DomainUser> queryUsers(ObjectNode query, Pagination pagination);

    public ResultMap<DomainGroup> queryGroups(ObjectNode query);
    public ResultMap<DomainGroup> queryGroups(ObjectNode query, Pagination pagination);

    public DomainUser inviteUser(DomainUser invitee);
}