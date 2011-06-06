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
import org.gitana.JSONBuilder;
import org.gitana.repo.query.QueryBuilder;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.ResultMap;
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
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = server.createRepository();

        // list branches (should have 1)
        assertEquals(1, repository.listBranches().size());

        // get the master branch
        Branch master = repository.readBranch("master");
        assertNotNull(master);
        assertTrue(master.isMaster());

        // create three new branches
        Branch branch1 = repository.createBranch(master.getRootChangesetId());
        Branch branch2 = repository.createBranch(master.getRootChangesetId());
        Branch branch3 = repository.createBranch(master.getRootChangesetId());

        // list branches (should be 4)
        assertEquals(4, repository.listBranches().size());

        // read branch 2 to verify
        Branch verify2 = repository.readBranch(branch2.getId());
        assertNotNull(verify2);
        assertEquals(branch2, verify2);
        assertFalse(verify2.isMaster());

        // update branch 1
        branch1.update();

        // create a branch with obj data
        ObjectNode obj = JsonUtil.createObject();
        obj.put("top", "jimmy");
        Branch branch4 = repository.createBranch(master.getRootChangesetId(), obj);
        Branch verify4 = repository.readBranch(branch4.getId());
        assertEquals("jimmy", verify4.getString("top"));

    }

    @Test
    public void testAuthority()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = server.createRepository();

        // list branches (should have 1)
        assertEquals(1, repository.listBranches().size());

        // get the master branch
        Branch master = repository.readBranch("master");
        assertNotNull(master);
        assertTrue(master.isMaster());

        // add authority to test users
        SecurityUser daffy = server.createUser("testuser-" + System.currentTimeMillis() + "_1", "password");
        SecurityUser bugs = server.createUser("testuser-" + System.currentTimeMillis() + "_2", "password");

        master.grant(daffy.getId(),"manager");
        master.grant(bugs.getId(),"consumer");

        assertNotNull(master.getAuthorities(daffy.getId()));
        assertTrue(master.getAuthorities(daffy.getId()).size() > 0);
        assertNotNull(master.getAuthorities(bugs.getId()));
        assertTrue(master.getAuthorities(bugs.getId()).size() > 0);
    }

    @Test
    public void testPagination()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = server.createRepository();

        // create a bunch of branches
        repository.createBranch("0:root", JSONBuilder.start("value").is(1).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(2).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(3).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(4).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(5).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(6).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(7).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(8).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(9).get());
        repository.createBranch("0:root", JSONBuilder.start("value").is(10).get());

        // list branches (should have 11)
        assertEquals(11, repository.listBranches().size());

        // test size 5
        Pagination pagination = new Pagination();
        pagination.setSkip(0);
        pagination.setLimit(5);
        assertEquals(5, repository.listBranches(pagination).size());

        // test size 7 offset 2
        pagination.setSkip(2);
        assertEquals(5, repository.listBranches(pagination).size());

        // query for all branches with a "value" > 0
        ObjectNode query = QueryBuilder.start("value").greaterThan(0).get();

        // query size 5, offset 0
        pagination.setSkip(0);
        ResultMap<Branch> branches1 = repository.queryBranches(query, pagination);
        assertTrue(branches1.offset() == 0);
        assertTrue(branches1.totalRows() == 10);
        assertTrue(branches1.size() == 5);

        // query next 5
        pagination.setSkip(5);
        ResultMap<Branch> branches2 = repository.queryBranches(query, pagination);
        assertTrue(branches2.offset() == 5);
        assertTrue(branches2.totalRows() == 10);
        assertTrue(branches2.size() == 5);

        // ensure first batch is correct
        for (Branch branch: branches1.values())
        {
            assertTrue(branch.getInt("value") <= 5);
        }

        // ensure second batch is correct
        for (Branch branch: branches2.values())
        {
            assertTrue(branch.getInt("value") > 5 && branch.getInt("value") <= 10);
        }
    }

}
