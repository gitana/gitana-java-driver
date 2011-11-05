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
import org.gitana.repo.client.nodes.Node;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * @author uzi
 */
public class AuthorityGrantTest extends AbstractTestCase
{
    @Test
    public void testServer()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // remove EVERYONE's collaborator right for the server
        try
        {
            server.revokeAll("everyone");

            testAuthorityGrants(server, server);
        }
        finally
        {
            // be sure to restore EVERYONE's collaborator right
            server.grant("everyone", "collaborator");
        }
    }

    @Test
    public void testRepository()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        Repository repository = server.createRepository();

        testAuthorityGrants(server, repository);
    }

    @Test
    public void testBranch()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        Repository repository = server.createRepository();

        Branch branch = repository.readBranch("master");

        testAuthorityGrants(server, branch);
    }

    @Test
    public void testNode()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        Repository repository = server.createRepository();

        Branch branch = repository.readBranch("master");

        Node node = (Node) branch.createNode();

        testAuthorityGrants(server, node);
    }

    @Test
    public void testOrganization()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        Organization organization = server.createOrganization();

        testAuthorityGrants(server, organization);
    }

    @Test
    public void testSecurityPrincipal()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a group
        String groupId = "testsecurityprincipl-" + System.currentTimeMillis();
        SecurityGroup group = server.createGroup(groupId);

        // revoke everyone's consumer right (which is the default)
        group.revokeAll("everyone");

        testAuthorityGrants(server, group);
    }

    /**
     * Helper method for testing authority grants against an access controllable.
     *
     * @param server
     * @param accessControllable
     */
    protected void testAuthorityGrants(Server server, AccessControllable accessControllable)
    {
        // create two users
        String userId1 = "testuser1_" + System.currentTimeMillis();
        server.createUser(userId1, "password");
        String userId2 = "testuser2_" + System.currentTimeMillis();
        server.createUser(userId2, "password");

        // grant user1 consumer rights to the server
        accessControllable.grant(userId1, "consumer");

        // grant user2 manager rights to the server
        accessControllable.grant(userId2, "manager");

        // get the list of authorities that user1 has
        // user1 should have 1 (user1/consumer direct)
        Map<String, AuthorityGrant> authorityGrants1 = accessControllable.getAuthorityGrants(Arrays.asList(userId1)).get(userId1);
        assertEquals(1, authorityGrants1.size());

        // get the list of authorities that user2 has
        // user1 should have 1 (user2/manager direct)
        Map<String, AuthorityGrant> authorityGrants2 = accessControllable.getAuthorityGrants(Arrays.asList(userId2)).get(userId2);
        assertEquals(1, authorityGrants2.size());

        // now create a group and add user1 to the group
        String groupId3 = "testgroup3_" + System.currentTimeMillis();
        SecurityGroup group3 = server.createGroup(groupId3);
        group3.addPrincipal(userId1);

        // grant group3 editor rights to the server
        accessControllable.grant(groupId3, "editor");

        // get the list of authorities that user1 has
        // user1 should have 2 (user1/consumer direct, group3/editor indirect)
        Map<String, AuthorityGrant> authorityGrants3 = accessControllable.getAuthorityGrants(Arrays.asList(userId1)).get(userId1);
        assertEquals(2, authorityGrants3.size());

        // revoke user1's consumer right
        accessControllable.revoke(userId1, "consumer");

        // get the list of authorities that user1 has
        // user1 should have 1 (group3/editor indirect)
        Map<String, AuthorityGrant> authorityGrants4 = accessControllable.getAuthorityGrants(Arrays.asList(userId1)).get(userId1);
        assertEquals(1, authorityGrants4.size());

        // get the list of authorities that user2 has
        // user1 should have 1 (user2/manager direct)
        Map<String, AuthorityGrant> authorityGrants5 = accessControllable.getAuthorityGrants(Arrays.asList(userId2)).get(userId2);
        assertEquals(1, authorityGrants5.size());
    }
}
