/**
 * Copyright 2025 Gitana Software, Inc.
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
 *   info@gitana.io
 */
package org.gitana.platform.client;

import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainGroup;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.util.TestConstants;
import org.junit.Test;

/**
 * @author uzi
 */
public class PrincipalACLTest extends AbstractTestCase
{
    @Test
    public void testACL()
    {
        Platform platform = new Gitana().authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDomain("default");

        // create three users
        String userName1 = "testuser1_" + System.currentTimeMillis();
        DomainUser user1 = domain.createUser(userName1, TestConstants.TEST_PASSWORD);
        String userName2 = "testuser2_" + System.currentTimeMillis();
        DomainUser user2 = domain.createUser(userName2, TestConstants.TEST_PASSWORD);
        String userName3 = "testuser3_" + System.currentTimeMillis();
        DomainUser user3 = domain.createUser(userName3, TestConstants.TEST_PASSWORD);

        // create a group
        String groupId1 = "testgroup1_" + System.currentTimeMillis();
        DomainGroup group1 = domain.createGroup(groupId1);

        // add user1 to group1
        group1.addPrincipal(user1);

        // grant group1 MANAGER rights over user3
        user3.grant(groupId1, "manager");


        // verify that user1 can read and update user3
        new Gitana().authenticate(userName1, TestConstants.TEST_PASSWORD).readDomain("default").readPrincipal(userName1).update();

        // verify that user2 can read but NOT update user3
        DomainUser u3 = (DomainUser) new Gitana().authenticate(userName2, TestConstants.TEST_PASSWORD).readDomain("default").readPrincipal(userName3);
        Exception ex1 = null;
        try
        {
            u3.update();
        }
        catch (Exception e)
        {
            ex1 = e;
        }
        assertNotNull(ex1);

        // verify that user3 can read and update user3 (self manager)
        new Gitana().authenticate(userName3, TestConstants.TEST_PASSWORD).readDomain("default").readPrincipal(userName3).update();

        // revoke EVERYONE CONSUMER from user3
        user3.revokeAll("everyone");

        // verify that user2 cannot read user3
        DomainUser testRead3 = (DomainUser) new Gitana().authenticate(userName2, TestConstants.TEST_PASSWORD).readDomain("default").readPrincipal(userName3);
        assertNull(testRead3);

        // verify that user3 can read user3 (self manager)
        new Gitana().authenticate(userName3, TestConstants.TEST_PASSWORD).readDomain("default").readPrincipal(userName3).update();
    }

}
