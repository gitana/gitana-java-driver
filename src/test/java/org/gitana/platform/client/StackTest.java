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
import org.gitana.platform.client.stack.Stack;
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

        // create an project
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

}
