/**
 * Copyright 2022 Gitana Software, Inc.
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
 *   info@cloudcms.com
 */
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.node.NodeBuilder;
import org.gitana.platform.support.Pagination;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Map;

/**
 * Tests out using pagination and the full ES dsl.
 * 
 * @author uzi
 */
public class NodeSearch5Test extends AbstractTestCase
{
    @Test
    public void testSearch()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = platform.createRepository();

        // master branch
        Branch master = repo.readBranch("master");

        // create first node
        ObjectNode obj1 = NodeBuilder.start("title").is("Harry Potter and the Philosopher's Stone").and("author").is("J.K. Rowling").get();
        master.createNode(obj1);

        // create second node
        ObjectNode obj2 = NodeBuilder.start("title").is("Harry Potter and the Chamber of Secrets").and("author").is("J.K. Rowling").get();
        master.createNode(obj2);

        // create third node
        ObjectNode obj3 = NodeBuilder.start("title").is("Harry Potter and the Prisoner of Azkaban").and("author").is("J.K. Rowling").get();
        master.createNode(obj3);

        // create fourth node
        ObjectNode obj4 = NodeBuilder.start("title").is("Harry Potter and the Goblet of Fire").and("author").is("J.K. Rowling").get();
        master.createNode(obj4);

        // create fifth node
        ObjectNode obj5 = NodeBuilder.start("title").is("Harry Potter and the Order of the Phoenix").and("author").is("J.K. Rowling").get();
        master.createNode(obj5);

        // create sixth node
        ObjectNode obj6 = NodeBuilder.start("title").is("Harry Potter and the Half-Blood Prince").and("author").is("J.K. Rowling").get();
        master.createNode(obj6);

        // create seventh node
        ObjectNode obj7 = NodeBuilder.start("title").is("Harry Potter and the Deathly Hallows").and("author").is("J.K. Rowling").get();
        master.createNode(obj7);

        // here we wait a little bit for the asynchronous indexing on the server side to complete
        Thread.sleep(5000);
        waitForZeroWaitingJobs();

        // use the ES DSL
        ObjectNode searchObject = JsonUtil.createObject("{'search': {'query_string': { 'query': 'harry' }}}");

        // search and verify scores descending
        System.out.println("DESCENDING SCORES");
        Pagination pagination1 = new Pagination();
        pagination1.getSorting().addSort("_score", -1);
        float last = 100000;
        Map<String, BaseNode> results1 = master.searchNodes(searchObject, pagination1);
        for (BaseNode node: results1.values())
        {
            String title = node.getTitle();
            float current = node.getObject("_search").get("score").floatValue();

            System.out.println("  [" + title + "]: " + current);

            assertTrue(current <= last);

            last = current;
        }

        // search and verify scores ascending
        System.out.println("ASCENDING SCORES");
        Pagination pagination2 = new Pagination();
        pagination2.getSorting().addSort("_score", 1);
        last = -10000;
        results1 = master.searchNodes(searchObject, pagination2);
        for (BaseNode node: results1.values())
        {
            String title = node.getTitle();
            float current = node.getObject("_search").get("score").floatValue();

            System.out.println("  [" + title + "]: " + current);

            assertTrue(current >= last);

            last = current;
        }

    }
}
