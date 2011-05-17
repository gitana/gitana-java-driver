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

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.services.Branches;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class BranchTest extends AbstractTestCase
{
    @Test
    public void testCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = gitana.repositories().create();

        Branches branches = repository.branches();

        // list branches (should have 1)
        assertEquals(1, branches.list().size());

        // get the master branch
        Branch master = branches.read("master");
        assertNotNull(master);
        assertTrue(master.isMaster());

        // create three new branches
        Branch branch1 = branches.create(master.getRootChangesetId());
        Branch branch2 = branches.create(master.getRootChangesetId());
        Branch branch3 = branches.create(master.getRootChangesetId());

        // list branches (should be 4)
        assertEquals(4, branches.list().size());

        // read branch 2 to verify
        Branch verify2 = branches.read(branch2.getId());
        assertNotNull(verify2);
        assertEquals(branch2, verify2);
        assertFalse(verify2.isMaster());

        // update branch 1
        branch1.update();

        // create a branch with obj data
        ObjectNode obj = JsonUtil.createObject();
        obj.put("top", "jimmy");
        Branch branch4 = branches.create(master.getRootChangesetId(), obj);
        Branch verify4 = branches.read(branch4.getId());
        assertEquals("jimmy", verify4.getString("top"));

    }

    @Test
    public void testAuthority()
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = gitana.repositories().create();

        Branches branches = repository.branches();

        // list branches (should have 1)
        assertEquals(1, branches.list().size());

        // get the master branch
        Branch master = branches.read("master");
        assertNotNull(master);
        assertTrue(master.isMaster());

        // add authority to test users
        SecurityUser daffy = gitana.users().create("testuser-" + System.currentTimeMillis() + "_1", "password");
        SecurityUser bugs = gitana.users().create("testuser-" + System.currentTimeMillis() + "_2", "password");

        master.grant(daffy.getId(),"manager");
        master.grant(bugs.getId(),"consumer");

        assertNotNull(master.getAuthorities(daffy.getId()));
        assertTrue(master.getAuthorities(daffy.getId()).size() > 0);
        assertNotNull(master.getAuthorities(bugs.getId()));
        assertTrue(master.getAuthorities(bugs.getId()).size() > 0);
    }
}
