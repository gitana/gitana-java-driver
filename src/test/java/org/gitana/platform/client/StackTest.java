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

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class StackTest extends AbstractTestCase
{
    @Test
    public void testCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a stack
        String title = "project1-" + System.currentTimeMillis();
        Stack stack1 = platform.createStack();
        stack1.setTitle(title);
        stack1.update();

        // validate
        ResultMap<Stack> stacks = platform.listStacks();
        assertTrue(stacks.size() > 0);
        assertNotNull(stacks.get(stack1.getId()));

        // query
        ObjectNode query = QueryBuilder.start("title").is(title).get();
        stacks = platform.queryStacks(query);
        assertEquals(1, stacks.size());

        // delete
        stack1.delete();
        stacks = platform.queryStacks(query);
        assertEquals(0, stacks.size());
    }

    @Test
    public void testDataStores()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create some datastores
        Repository repository = platform.createRepository();
        repository.set("abc", "123");
        repository.update();
        Vault vault = platform.createVault();
        vault.set("abc", "123");
        vault.set("def", "456");
        vault.update();

        // create a stack
        Stack stack = platform.createStack();

        // allocate these guys
        stack.assignDataStore(repository);
        stack.assignDataStore(vault);

        // list
        assertEquals(2, stack.listDataStores().size());

        // query #1
        ObjectNode query1 = QueryBuilder.start("abc").is("123").get();
        assertEquals(2, stack.queryDataStores(query1).size());

        // query #2
        ObjectNode query2 = QueryBuilder.start("def").is("456").get();
        assertEquals(1, stack.queryDataStores(query2).size());

        // query #3
        ObjectNode query3 = QueryBuilder.start("xyz").is("789").get();
        assertEquals(0, stack.queryDataStores(query3).size());

        // remove the vault
        stack.unassignDataStore(vault.getId());

        // query #4
        ObjectNode query4 = QueryBuilder.start("abc").is("123").get();
        assertEquals(1, stack.queryDataStores(query4).size());
    }

}
