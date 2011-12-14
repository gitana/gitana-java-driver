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

package org.gitana.platform.client;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.team.Team;
import org.junit.Test;

/**
 * @author uzi
 */
public class RepositoryTeamTest extends AbstractTestCase
{
    @Test
    public void testTeams()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Domain domain = platform.readDefaultDomain();

        // repository
        Repository repository = platform.createRepository();

        // give "everyone" consumer on the repo
        repository.grant("everyone", "consumer");

        // get the default owners team
        Team owners = repository.readTeam("owners");
        assertEquals(1, owners.listMembers().size()); // admin

        // create three users
        DomainUser user1 = domain.createUser("user1-" + System.currentTimeMillis(), "pw");
        DomainUser user2 = domain.createUser("user2-" + System.currentTimeMillis(), "pw");
        DomainUser user3 = domain.createUser("user3-" + System.currentTimeMillis(), "pw");

        // add users to the owners team
        owners.addMember(user1.getId());
        owners.addMember(user2.getId());
        assertEquals(3, owners.listMembers().size()); // admin + user1 + user2

        // remove a member
        owners.removeMember(user2.getId());
        assertEquals(2, owners.listMembers().size()); // admin + user1

        // create a new team
        Team grifters = repository.createTeam("grifters");
        grifters.addMember(user2.getId());
        assertEquals(1, grifters.listMembers().size()); // user2
        assertEquals(0, grifters.listAuthorities().size());
        grifters.grant("consumer");
        assertEquals(1, grifters.listAuthorities().size());

        // create a branch
        Branch branch = repository.createBranch("0:root");

        // revoke any rights that "everyone" may have by default
        branch.revokeAll("everyone");

        // user 1 should be able to UPDATE the branch since they have manager rights
        gitana.authenticate(user1.getName(), "pw");
        platform.readRepository(repository.getId()).readBranch(branch.getId()).update();

        // user 2 should be able to READ the branch but not update it
        gitana.authenticate(user2.getName(), "pw");
        Exception ex2 = null;
        try
        {
            platform.readRepository(repository.getId()).readBranch(branch.getId()).update();
        }
        catch (Exception ex)
        {
            ex2 = ex;
        }
        assertNotNull(ex2);

        // user 3 cannot even read branch
        // not a member of any teams
        gitana.authenticate(user3.getName(), "pw");
        Branch branch3 = platform.readRepository(repository.getId()).readBranch(branch.getId());
        assertNull(branch3);

        // delete the grifters team
        gitana.authenticate("admin", "admin");
        repository.deleteTeam("grifters");

        // user 2 can no longer read
        gitana.authenticate(user2.getName(), "pw");
        Branch branch20 = platform.readRepository(repository.getId()).readBranch(branch.getId());
        assertNull(branch20);
    }
}
