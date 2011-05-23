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

import org.gitana.util.ClasspathUtil;
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
        Server server = gitana.authenticate("admin", "admin");

        // create a user
        String name = "testuser" + System.currentTimeMillis();
        SecurityUser user = server.createUser(name, "password");

        // user map (verify)
        Map<String, SecurityUser> map = server.fetchUsers();
        assertNotNull(map.get(name));

        // read the user back to verify
        SecurityUser verify = server.readUser(user.getId());
        assertNotNull(verify);

        // update the user
        user.set("abc", "def");
        user.setFirstName("turbo");
        user.setLastName("ozone");
        user.setCompanyName("specialk");
        user.setEmail("breakin@breakin.com");
        user.update();

        // read back and verify
        verify = server.readUser(name);
        assertEquals("def", verify.getString("abc"));
        assertEquals("turbo", verify.getFirstName());
        assertEquals("ozone", verify.getLastName());
        assertEquals("specialk", verify.getCompanyName());
        assertEquals("breakin@breakin.com", verify.getEmail());

        // delete the user
        user.delete();

        // read back and verify
        verify = server.readUser(name);
        assertNull(verify);
    }

    @Test
    public void testGroupCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a group
        String name = "testgroup" + System.currentTimeMillis();
        SecurityGroup group = server.createGroup(name);

        // user map (verify)
        Map<String, SecurityGroup> map = server.fetchGroups();
        assertNotNull(map.get(name));

        // read the user back to verify
        SecurityGroup verify = server.readGroup(group.getId());
        assertNotNull(verify);

        // update the group
        group.set("abc", "def");
        group.update();

        // read back and verify
        verify = server.readGroup(name);
        assertEquals("def", verify.getString("abc"));

        // delete the group
        group.delete();

        // read back and verify
        verify = server.readGroup(name);
        assertNull(verify);
    }

    @Test
    public void testMembership()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create six users
        SecurityUser user1 = server.createUser("testuser-" + System.currentTimeMillis() + "_1", "password");
        SecurityUser user2 = server.createUser("testuser-" + System.currentTimeMillis() + "_2", "password");
        SecurityUser user3 = server.createUser("testuser-" + System.currentTimeMillis() + "_3", "password");
        SecurityUser user4 = server.createUser("testuser-" + System.currentTimeMillis() + "_4", "password");
        SecurityUser user5 = server.createUser("testuser-" + System.currentTimeMillis() + "_5", "password");
        SecurityUser user6 = server.createUser("testuser-" + System.currentTimeMillis() + "_6", "password");

        // create three groups
        SecurityGroup group1 = server.createGroup("testgroup-" + System.currentTimeMillis() + "_1");
        SecurityGroup group2 = server.createGroup("testgroup-" + System.currentTimeMillis() + "_2");
        SecurityGroup group3 = server.createGroup("testgroup-" + System.currentTimeMillis() + "_3");

        // add users 1 and 2 into group 1
        // verify
        group1.addPrincipal(user1);
        group1.addPrincipal(user2);
        assertEquals(2, group1.listPrincipals(false).size());
        assertEquals(2, group1.listPrincipals(true).size());
        assertEquals(1, user1.listParentGroups().size());
        assertEquals(1, user2.listParentGroups().size());

        // add users 3, 4, 5 into group 2
        // verify
        group2.addPrincipal(user3);
        group2.addPrincipal(user4);
        group2.addPrincipal(user5);
        assertEquals(3, group2.listPrincipals(false).size());
        assertEquals(3, group2.listPrincipals(true).size());
        assertEquals(1, user3.listParentGroups().size());
        assertEquals(1, user4.listParentGroups().size());
        assertEquals(1, user5.listParentGroups().size());

        // add user 6 into group 3
        // verify
        group3.addPrincipal(user6);
        assertEquals(1, group3.listPrincipals(false).size());
        assertEquals(1, group3.listPrincipals(true).size());
        assertEquals(1, user6.listParentGroups().size());

        // now add group2 as a child of group 3
        group3.addPrincipal(group2);

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
        assertEquals(5, group3.listPrincipals(true).size());
        assertEquals(2, user3.listParentGroups(true).size());
        assertEquals(2, user4.listParentGroups(true).size());
        assertEquals(2, user5.listParentGroups(true).size());
    }

    @Test
    public void testAttachments()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create two users
        SecurityUser daffy = server.createUser("testuser-" + System.currentTimeMillis() + "_1", "password");
        SecurityUser bugs = server.createUser("testuser-" + System.currentTimeMillis() + "_2", "password");

        // upload
        byte[] daffyBytes = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/daffy.jpeg");
        byte[] bugsBytes = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/bugs.jpeg");

        // upload daffy avatar
        daffy.uploadAttachment("avatar", daffyBytes, "image/jpeg");
        byte[] test1 = daffy.downloadAttachment("avatar");
        assertEquals(daffyBytes.length, test1.length);

        // upload bugs avatar
        bugs.uploadAttachment("avatar", bugsBytes, "image/jpeg");
        byte[] test2 = bugs.downloadAttachment("avatar");
        assertEquals(bugsBytes.length, test2.length);
    }
}
