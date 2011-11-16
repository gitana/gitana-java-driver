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

import org.junit.Test;

/**
 * @author uzi
 */
public class OrganizationTeamTest extends AbstractTestCase
{
    @Test
    public void testTeams()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create an organization
        Organization organization = server.createOrganization();

        // get the default owners team
        Team owners = organization.readTeam("owners");
        assertEquals(1, owners.listMembers().size()); // admin

        // create three users
        SecurityUser user1 = server.createUser("user1-" + System.currentTimeMillis(), "pw");
        SecurityUser user2 = server.createUser("user2-" + System.currentTimeMillis(), "pw");
        SecurityUser user3 = server.createUser("user3-" + System.currentTimeMillis(), "pw");

        // add users to the owners team
        owners.addMember(user1.getId());
        owners.addMember(user2.getId());
        assertEquals(3, owners.listMembers().size()); // admin + user1 + user2

        // check each principal's organizations
        assertEquals(1, user1.fetchOrganizations().size());
        assertEquals(1, user2.fetchOrganizations().size());

        // remove a member
        owners.removeMember(user2.getId());
        assertEquals(2, owners.listMembers().size()); // admin + user1

        // check each principal's organizations
        assertEquals(1, user1.fetchOrganizations().size());
        assertEquals(0, user2.fetchOrganizations().size());

        // create a new team
        Team grifters = organization.createTeam("grifters");
        grifters.addMember(user2.getId());
        assertEquals(1, grifters.listMembers().size()); // user2
        assertEquals(0, grifters.listAuthorities().size());
        grifters.grant("consumer");
        assertEquals(1, grifters.listAuthorities().size());

        // create a repository
        Repository repository = server.createRepository();

        // revoke "everyone"'s collaborator role
        repository.revokeAll("everyone");

        // assign to organization
        organization.assignRepository(repository.getId());

        // user 1 should be able to UPDATE the repository since they have manager rights
        Server server1 = gitana.authenticate(user1.getName(), "pw");
        server1.readRepository(repository.getId()).update();

        // user 2 should be able to READ the repository but not update it
        Server server2 = gitana.authenticate(user2.getName(), "pw");
        Repository repo2 = server2.readRepository(repository.getId());
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
        Server server3 = gitana.authenticate(user3.getName(), "pw");
        Repository repo3 = server3.readRepository(repository.getId());
        assertNull(repo3);

        // unassign the repo
        organization.unassignRepository(repository.getId());

        // user 1 should no longer be able to do anything with the repo
        server1 = gitana.authenticate(user1.getName(), "pw");
        Repository repo1 = server1.readRepository(repository.getId());
        assertNull(repo1);
    }
}
