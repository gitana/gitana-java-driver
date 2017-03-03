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

package org.gitana.platform.client.team;

import org.gitana.platform.GitanaObject;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

import java.util.List;

/**
 *
 * @author uzi
 */
public interface Team extends GitanaObject
{
    public final static String FIELD_GROUP_ID = "groupId";
    public final static String FIELD_GROUP_DOMAIN_ID = "groupDomainId";

    public final static String FIELD_ROLE_KEYS = "roleKeys";

    public final static String FIELD_KEY = "key";

    public final static String FIELD_TEAMABLE_TYPE_ID = "teamableObjectTypeId";
    public final static String FIELD_TEAMABLE_ID = "teamableObjectId";


    /**
     * @return the teamable that this team is a part of
     */
    public Teamable getTeamable();

    /**
     * @return the team key
     */
    public String getKey();

    /**
     * @return the team group id
     */
    public String getGroupId();

    /**
     * @return the granted authority role keys that are bestowed unto members of this team
     */
    public List<String> getRoleKeys();


    // HELPER METHODS


    public void addMember(String principalId);

    public void removeMember(String principalId);

    public boolean hasMember(String principalId);

    public ResultMap<DomainPrincipal> listMembers();

    public ResultMap<DomainPrincipal> listMembers(Pagination pagination);

    public void grant(String authorityId);

    public void revoke(String authorityId);

    public List<String> listAuthorities();
}
