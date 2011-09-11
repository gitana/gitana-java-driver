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
public class SecurityACLTest extends AbstractTestCase
{
    @Test
    public void testACL()
    {
        Server server = new Gitana().authenticate("admin", "admin");

        // create three users
        String userId1 = "testuser1_" + System.currentTimeMillis();
        String userId2 = "testuser2_" + System.currentTimeMillis();
        String userId3 = "testuser3_" + System.currentTimeMillis();
        SecurityUser user1 = server.createUser(userId1, "test");
        SecurityUser user2 = server.createUser(userId2, "test");
        SecurityUser user3 = server.createUser(userId3, "test");

        // create a group
        String groupId1 = "testgroup1_" + System.currentTimeMillis();
        SecurityGroup group1 = server.createGroup(groupId1);

        // add user1 to group1
        group1.addPrincipal(user1);

        // grant group1 MANAGER rights over user3
        user3.grant(groupId1, "manager");


        // verify that user1 can read and update user3
        new Gitana().authenticate(userId1, "test").readUser(userId3).update();

        // verify that user2 can read but NOT update user3
        SecurityUser u1 = new Gitana().authenticate(userId2, "test").readUser(userId3);
        Exception ex1 = null;
        try
        {
            u1.update();
        }
        catch (Exception e)
        {
            ex1 = e;
        }
        assertNotNull(ex1);

        // verify that user3 can read and update user3 (self manager)
        new Gitana().authenticate(userId3, "test").readUser(userId3).update();

        // revoke EVERYONE CONSUMER from user3
        user3.revokeAll("everyone");

        // verify that user2 cannot read user3
        SecurityUser testRead1 = new Gitana().authenticate(userId2, "test").readUser(userId3);
        assertNull(testRead1);

        // verify that user3 can read user3 (self manager)
        new Gitana().authenticate(userId3, "test").readUser(userId3).update();
    }

}
