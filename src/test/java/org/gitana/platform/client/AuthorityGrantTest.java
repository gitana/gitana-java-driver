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
import org.gitana.platform.client.nodes.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainGroup;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

/**
 * @author uzi
 */
public class AuthorityGrantTest extends AbstractTestCase
{
    @Test
    public void testPlatform()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // remove EVERYONE's connector right for the server
        try
        {
            platform.revokeAll("everyone");

            testAuthorityGrants(platform, platform);
        }
        finally
        {
            // be sure to restore EVERYONE's connector right
            platform.grant("everyone", "connector");
        }
    }

    @Test
    public void testRepository()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Repository repository = platform.createRepository();

        testAuthorityGrants(platform, repository);
    }

    @Test
    public void testBranch()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Repository repository = platform.createRepository();

        Branch branch = repository.readBranch("master");

        testAuthorityGrants(platform, branch);
    }

    @Test
    public void testNode()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Repository repository = platform.createRepository();

        Branch branch = repository.readBranch("master");

        Node node = (Node) branch.createNode();

        testAuthorityGrants(platform, node);
    }

    @Test
    public void testStack()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Stack stack = platform.createStack();

        testAuthorityGrants(platform, stack);
    }

    @Test
    public void testSecurityPrincipal()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDefaultDomain();

        // create a group
        String groupName = "testsecurityprincipl-" + System.currentTimeMillis();
        DomainGroup group = domain.createGroup(groupName);

        // revoke everyone's consumer right (which is the default)
        group.revokeAll("everyone");

        testAuthorityGrants(platform, group);
    }

    /**
     * Helper method for testing authority grants against an access controllable.
     *
     * @param platform
     * @param accessControllable
     */
    protected void testAuthorityGrants(Platform platform, AccessControllable accessControllable)
    {
        // default domain
        Domain domain = platform.readDefaultDomain();

        // create two users
        String userName1 = "testuser1_" + System.currentTimeMillis();
        DomainUser user1 = domain.createUser(userName1, "password");
        String userName2 = "testuser2_" + System.currentTimeMillis();
        DomainUser user2 = domain.createUser(userName2, "password");

        // grant user1 consumer rights to the server
        accessControllable.grant(userName1, "consumer");

        // grant user2 manager rights to the server
        accessControllable.grant(userName2, "manager");

        // get the list of authorities that user1 has
        // user1 should have 1 (user1/consumer direct)
        Map<String, AuthorityGrant> authorityGrants1 = accessControllable.getAuthorityGrants(Arrays.asList(user1.getId())).get(user1.getId());
        assertEquals(1, authorityGrants1.size());

        // get the list of authorities that user2 has
        // user1 should have 1 (user2/manager direct)
        Map<String, AuthorityGrant> authorityGrants2 = accessControllable.getAuthorityGrants(Arrays.asList(user2.getId())).get(user2.getId());
        assertEquals(1, authorityGrants2.size());

        // now create a group and add user1 to the group
        String groupId3 = "testgroup3_" + System.currentTimeMillis();
        DomainGroup group3 = domain.createGroup(groupId3);
        group3.addPrincipal(userName1);

        // grant group3 editor rights to the server
        accessControllable.grant(groupId3, "editor");

        // get the list of authorities that user1 has
        // user1 should have 2 (user1/consumer direct, group3/editor indirect)
        Map<String, AuthorityGrant> authorityGrants3 = accessControllable.getAuthorityGrants(Arrays.asList(userName1)).get(userName1);
        assertEquals(2, authorityGrants3.size());

        // revoke user1's consumer right
        accessControllable.revoke(userName1, "consumer");

        // get the list of authorities that user1 has
        // user1 should have 1 (group3/editor indirect)
        Map<String, AuthorityGrant> authorityGrants4 = accessControllable.getAuthorityGrants(Arrays.asList(userName1)).get(userName1);
        assertEquals(1, authorityGrants4.size());

        // get the list of authorities that user2 has
        // user1 should have 1 (user2/manager direct)
        Map<String, AuthorityGrant> authorityGrants5 = accessControllable.getAuthorityGrants(Arrays.asList(userName2)).get(userName2);
        assertEquals(1, authorityGrants5.size());
    }
}
