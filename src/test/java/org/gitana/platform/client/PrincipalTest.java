/**
 * Copyright 2013 Gitana Software, Inc.
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
import org.gitana.platform.client.principal.DomainGroup;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Map;

/**
 * @author uzi
 */
public class PrincipalTest extends AbstractTestCase
{
    @Test
    public void testUserCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDomain("default");

        // create a user
        String name = "testuser" + System.currentTimeMillis();
        DomainUser user = domain.createUser(name, "password");

        // user map (verify)
        Pagination pagination = new Pagination(0, 10000);
        Map<String, DomainUser> map = domain.listUsers(pagination);
        assertNotNull(map.get(user.getId()));

        // read the user back to verify
        DomainUser verify = (DomainUser) domain.readPrincipal(user.getId());
        assertNotNull(verify);

        // update the user
        user.set("abc", "def");
        user.setFirstName("turbo");
        user.setLastName("ozone");
        user.setCompanyName("specialk");
        user.setEmail("breakin@breakin.com");
        user.update();

        // read back and verify
        verify = (DomainUser) domain.readPrincipal(name);
        assertEquals("def", verify.getString("abc"));
        assertEquals("turbo", verify.getFirstName());
        assertEquals("ozone", verify.getLastName());
        assertEquals("specialk", verify.getCompanyName());
        assertEquals("breakin@breakin.com", verify.getEmail());

        // delete the user
        user.delete();

        // read back and verify
        verify = (DomainUser) domain.readPrincipal(name);
        assertNull(verify);
    }

    @Test
    public void testGroupCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDomain("default");

        // create a group
        String name = "testgroup" + System.currentTimeMillis();
        DomainGroup group = domain.createGroup(name);

        // user map (DomainGroup)
        Pagination pagination = new Pagination(0, 10000);
        Map<String, DomainGroup> map = domain.listGroups(pagination);
        assertNotNull(map.get(group.getId()));

        // read the user back to verify
        DomainGroup verify = (DomainGroup) domain.readPrincipal(group.getId());
        assertNotNull(verify);

        // update the group
        group.set("abc", "def");
        group.update();

        // read back and verify
        verify = (DomainGroup) domain.readPrincipal(name);
        assertEquals("def", verify.getString("abc"));

        // delete the group
        group.delete();

        // read back and verify
        verify = (DomainGroup) domain.readPrincipal(name);
        assertNull(verify);
    }

    @Test
    public void testMembership()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDomain("default");

        // create six users
        DomainUser user1 = domain.createUser("testuser-" + System.currentTimeMillis() + "_1", "password");
        DomainUser user2 = domain.createUser("testuser-" + System.currentTimeMillis() + "_2", "password");
        DomainUser user3 = domain.createUser("testuser-" + System.currentTimeMillis() + "_3", "password");
        DomainUser user4 = domain.createUser("testuser-" + System.currentTimeMillis() + "_4", "password");
        DomainUser user5 = domain.createUser("testuser-" + System.currentTimeMillis() + "_5", "password");
        DomainUser user6 = domain.createUser("testuser-" + System.currentTimeMillis() + "_6", "password");

        // create three groups
        DomainGroup group1 = domain.createGroup("testgroup-" + System.currentTimeMillis() + "_1");
        DomainGroup group2 = domain.createGroup("testgroup-" + System.currentTimeMillis() + "_2");
        DomainGroup group3 = domain.createGroup("testgroup-" + System.currentTimeMillis() + "_3");

        int BASE = 1; // OneTeam Users

        // add users 1 and 2 into group 1
        // verify
        group1.addPrincipal(user1);
        group1.addPrincipal(user2);
        assertEquals(2, group1.listPrincipals(false).size());
        assertEquals(2, group1.listPrincipals(true).size());
        assertEquals(BASE + 1, user1.listParentGroups().size());
        assertEquals(BASE + 1, user2.listParentGroups().size());

        // add users 3, 4, 5 into group 2
        // verify
        group2.addPrincipal(user3);
        group2.addPrincipal(user4);
        group2.addPrincipal(user5);
        assertEquals(3, group2.listPrincipals(false).size());
        assertEquals(3, group2.listPrincipals(true).size());
        assertEquals(BASE + 1, user3.listParentGroups().size());
        assertEquals(BASE + 1, user4.listParentGroups().size());
        assertEquals(BASE + 1, user5.listParentGroups().size());

        // add user 6 into group 3
        // verify
        group3.addPrincipal(user6);
        assertEquals(1, group3.listPrincipals(false).size());
        assertEquals(1, group3.listPrincipals(true).size());
        assertEquals(BASE + 1, user6.listParentGroups().size());

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
        assertEquals(BASE + 2, user3.listParentGroups(true).size());
        assertEquals(BASE + 2, user4.listParentGroups(true).size());
        assertEquals(BASE + 2, user5.listParentGroups(true).size());
    }

    @Test
    public void testAttachments()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDomain("default");

        // create two users
        DomainUser daffy = domain.createUser("testuser-" + System.currentTimeMillis() + "_1", "password");
        DomainUser bugs = domain.createUser("testuser-" + System.currentTimeMillis() + "_2", "password");

        // upload
        byte[] daffyBytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/daffy.jpeg");
        byte[] bugsBytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg");

        // upload daffy avatar
        daffy.uploadAttachment("avatar", daffyBytes, "image/jpeg");
        byte[] test1 = daffy.downloadAttachment("avatar");
        assertEquals(daffyBytes.length, test1.length);

        // upload bugs avatar
        bugs.uploadAttachment("avatar", bugsBytes, "image/jpeg");
        byte[] test2 = bugs.downloadAttachment("avatar");
        assertEquals(bugsBytes.length, test2.length);
    }

    @Test
    public void testUserQuery()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDomain("default");

        String tag = "tag_" + System.currentTimeMillis();

        // create test user #1
        String name1 = "testuser1_" + System.currentTimeMillis();
        DomainUser user1 = domain.createUser(name1, "password");
        user1.set("tag", tag);
        user1.set("season", "summer");
        user1.set("month", "june");
        user1.update();

        // create test user #2
        String name2 = "testuser2_" + System.currentTimeMillis();
        DomainUser user2 = domain.createUser(name2, "password");
        user2.set("tag", tag);
        user2.set("season", "summer");
        user2.set("month", "july");
        user2.update();

        // create test user #3
        String name3 = "testuser3_" + System.currentTimeMillis();
        DomainUser user3 = domain.createUser(name3, "password");
        user3.set("tag", tag);
        user3.set("season", "summer");
        user3.set("month", "august");
        user3.update();

        // create test user #4
        String name4 = "testuser4_" + System.currentTimeMillis();
        DomainUser user4 = domain.createUser(name4, "password");
        user4.set("tag", tag);
        user4.set("season", "autumn");
        user4.set("month", "august");
        user4.update();

        // query test #1
        ResultMap<DomainUser> users1 = domain.queryUsers(JsonUtil.createObject("{'tag': '" + tag + "', 'season':'summer'}"));
        assertEquals(3, users1.size());

        // query test #2
        ResultMap<DomainUser> users2 = domain.queryUsers(JsonUtil.createObject("{'tag': '" + tag + "', 'season':'autumn'}"));
        assertEquals(1, users2.size());
    }

    @Test
    public void testGroupQuery()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDomain("default");

        String tag = "tag_" + System.currentTimeMillis();

        // create test group #1
        String name1 = "testgroup1_" + System.currentTimeMillis();
        DomainGroup group1 = domain.createGroup(name1);
        group1.set("tag", tag);
        group1.set("season", "summer");
        group1.set("month", "june");
        group1.update();

        // create test group #2
        String name2 = "testgroup2_" + System.currentTimeMillis();
        DomainGroup group2 = domain.createGroup(name2);
        group2.set("tag", tag);
        group2.set("season", "summer");
        group2.set("month", "july");
        group2.update();

        // create test group #3
        String name3 = "testgroup3_" + System.currentTimeMillis();
        DomainGroup group3 = domain.createGroup(name3);
        group3.set("tag", tag);
        group3.set("season", "summer");
        group3.set("month", "august");
        group3.update();

        // create test group #4
        String name4 = "testgroup4_" + System.currentTimeMillis();
        DomainGroup group4 = domain.createGroup(name4);
        group4.set("tag", tag);
        group4.set("season", "autumn");
        group4.set("month", "august");
        group4.update();

        // query test #1
        ResultMap<DomainGroup> groups1 = domain.queryGroups(JsonUtil.createObject("{'tag': '" + tag + "', 'season':'summer'}"));
        assertEquals(3, groups1.size());

        // query test #2
        ResultMap<DomainGroup> groups2 = domain.queryGroups(JsonUtil.createObject("{'tag': '" + tag + "', 'season':'autumn'}"));
        assertEquals(1, groups2.size());
    }

    @Test
    public void testInvitations()
            throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a domain
        // create a user
        Domain mikeDomain = platform.createDomain();
        DomainUser mike = mikeDomain.createUser("mike", "abc");

        // create a domain
        // create a user
        Domain tonyDomain = platform.createDomain();
        DomainUser tony = tonyDomain.createUser("tony", "abc");
        
        // invite tony into mike's domain
        DomainUser tony2 = mikeDomain.inviteUser(tony);
        
        assertNotSame(tony2.getId(), tony.getId());
    }

    @Test
    public void testChangePassword()
            throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a domain
        // create a user
        Domain mikeDomain = platform.createDomain();
        DomainUser mike = mikeDomain.createUser("mike", "abc");
        
        // change password
        mike.readIdentity().changePassword("def", "def");

        // authenticate with old password (should fail)
        Throwable ex1 = null;
        try
        {
            gitana.authenticate(mike.getDomainQualifiedName(), "abc");
        }
        catch (Exception ex)
        {
            ex1 = ex;
        }
        assertNotNull(ex1);

        // authenticate with new password (should succeed)
        Throwable ex2 = null;
        try
        {
            gitana.authenticate(mike.getDomainQualifiedName(), "def");
        }
        catch (Exception ex)
        {
            ex2 = ex;
        }
        assertNull(ex2);
    }

}
