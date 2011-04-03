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

import java.util.Map;

/**
 * @author uzi
 */
public class SecurityTest extends AbstractTestCase
{
    @Test
    public void testUserCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a user
        String name = "testuser" + System.currentTimeMillis();
        SecurityUser user = gitana.users().create(name, "password");

        // user map (verify)
        Map<String, SecurityUser> map = gitana.users().map();
        assertNotNull(map.get(name));

        // read the user back to verify
        SecurityUser verify = gitana.users().read(user.getId());
        assertNotNull(verify);

        // update the user
        user.set("abc", "def");
        user.update();

        // read back and verify
        verify = gitana.users().read(name);
        assertEquals("def", verify.getString("abc"));

        // delete the user
        user.delete();

        // read back and verify
        verify = gitana.users().read(name);
        assertNull(verify);
    }

    @Test
    public void testGroupCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a group
        String name = "testgroup" + System.currentTimeMillis();
        SecurityGroup group = gitana.groups().create(name);

        // user map (verify)
        Map<String, SecurityGroup> map = gitana.groups().map();
        assertNotNull(map.get(name));

        // read the user back to verify
        SecurityGroup verify = gitana.groups().read(group.getId());
        assertNotNull(verify);

        // update the group
        group.set("abc", "def");
        group.update();

        // read back and verify
        verify = gitana.groups().read(name);
        assertEquals("def", verify.getString("abc"));

        // delete the group
        group.delete();

        // read back and verify
        verify = gitana.groups().read(name);
        assertNull(verify);
    }

    @Test
    public void testMembership()
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create six users
        SecurityUser user1 = gitana.users().create("testuser-" + System.currentTimeMillis() + "_1", "password");
        SecurityUser user2 = gitana.users().create("testuser-" + System.currentTimeMillis() + "_2", "password");
        SecurityUser user3 = gitana.users().create("testuser-" + System.currentTimeMillis() + "_3", "password");
        SecurityUser user4 = gitana.users().create("testuser-" + System.currentTimeMillis() + "_4", "password");
        SecurityUser user5 = gitana.users().create("testuser-" + System.currentTimeMillis() + "_5", "password");
        SecurityUser user6 = gitana.users().create("testuser-" + System.currentTimeMillis() + "_6", "password");

        // create three groups
        SecurityGroup group1 = gitana.groups().create("testgroup-" + System.currentTimeMillis() + "_1");
        SecurityGroup group2 = gitana.groups().create("testgroup-" + System.currentTimeMillis() + "_2");
        SecurityGroup group3 = gitana.groups().create("testgroup-" + System.currentTimeMillis() + "_3");

        // add users 1 and 2 into group 1
        // verify
        group1.add(user1);
        group1.add(user2);
        assertEquals(2, group1.childList(false).size());
        assertEquals(2, group1.childList(true).size());
        assertEquals(1, user1.parentList().size());
        assertEquals(1, user2.parentList().size());

        // add users 3, 4, 5 into group 2
        // verify
        group2.add(user3);
        group2.add(user4);
        group2.add(user5);
        assertEquals(3, group2.childList(false).size());
        assertEquals(3, group2.childList(true).size());
        assertEquals(1, user3.parentList().size());
        assertEquals(1, user4.parentList().size());
        assertEquals(1, user5.parentList().size());

        // add user 6 into group 3
        // verify
        group3.add(user6);
        assertEquals(1, group3.childList(false).size());
        assertEquals(1, group3.childList(true).size());
        assertEquals(1, user6.parentList().size());

        // now add group2 as a child of group 3
        group3.add(group2);

        /**
         * graph looks like:
         *
         *      group1
         *          user1
         *          user2
         *      group3
         *          user6
         *          group2
         *              user3
         *              user4
         *              user5
         */

        // verify inherited memberships
        assertEquals(5, group3.childList(true).size());
        assertEquals(2, user3.parentList(true).size());
        assertEquals(2, user4.parentList(true).size());
        assertEquals(2, user5.parentList(true).size());
    }
}
