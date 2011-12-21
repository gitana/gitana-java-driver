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

import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.team.Team;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author uzi
 */
@Ignore
public class StackTeamTest extends AbstractTestCase
{
    @Test
    public void testTeams()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Domain domain = platform.readDefaultDomain();

        // create a stack
        Stack stack = platform.createStack();

        // get the default owners team
        Team owners = stack.readTeam("owners");
        assertEquals(1, owners.listMembers().size()); // admin

        // create three users
        DomainUser user1 = domain.createUser("user1-" + System.currentTimeMillis(), "pw");
        DomainUser user2 = domain.createUser("user2-" + System.currentTimeMillis(), "pw");
        DomainUser user3 = domain.createUser("user3-" + System.currentTimeMillis(), "pw");

        // add users to the owners team
        owners.addMember(user1.getId());
        owners.addMember(user2.getId());
        assertEquals(3, owners.listMembers().size()); // admin + user1 + user2

        // check each principal's organizations
        // TODO
        //assertEquals(1, user1.listOrganizations().size());
        //assertEquals(1, user2.listOrganizations().size());

        // remove a member
        owners.removeMember(user2.getId());
        assertEquals(2, owners.listMembers().size()); // admin + user1

        // check each principal's organizations
        // TODO
        //assertEquals(1, user1.listOrganizations().size());
        //assertEquals(0, user2.listOrganizations().size());

        // create a new team
        Team grifters = stack.createTeam("grifters");
        grifters.addMember(user2.getId());
        assertEquals(1, grifters.listMembers().size()); // user2
        assertEquals(0, grifters.listAuthorities().size());
        grifters.grant("consumer");
        assertEquals(1, grifters.listAuthorities().size());

        // create a repository
        Repository repository = platform.createRepository();

        // revoke "everyone"'s collaborator role
        repository.revokeAll("everyone");

        // assign to organization
        // TODO
        //organization.assignRepository(repository.getId());

        // user 1 should be able to UPDATE the repository since they have manager rights
        Platform platform1 = gitana.authenticate(user1.getName(), "pw");
        platform1.readRepository(repository.getId()).update();

        // user 2 should be able to READ the repository but not update it
        Platform platform2 = gitana.authenticate(user2.getName(), "pw");
        Repository repo2 = platform2.readRepository(repository.getId());
        Exception ex2 = null;
        try
        {
            repo2.update();
        }
        catch (Exception ex)
        {
            ex2 = ex;
        }
        assertNotNull(ex2);

        // user 3 cannot even read repository
        // not a member of any teams
        Platform platform3 = gitana.authenticate(user3.getName(), "pw");
        Repository repo3 = platform3.readRepository(repository.getId());
        assertNull(repo3);

        // unassign the repo
        // TODO
        //organization.unassignRepository(repository.getId());

        // user 1 should no longer be able to do anything with the repo
        platform1 = gitana.authenticate(user1.getName(), "pw");
        Repository repo1 = platform1.readRepository(repository.getId());
        assertNull(repo1);
    }
}
