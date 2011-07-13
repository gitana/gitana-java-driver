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
import org.gitana.repo.client.nodes.BaseNode;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.node.NodeBuilder;
import org.gitana.repo.query.QueryBuilder;
import org.gitana.repo.support.Pagination;
import org.junit.Test;

import java.util.Map;

/**
 * @author uzi
 */
public class NodeQueryTest extends AbstractTestCase
{
    @Test
    public void testQuery()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = server.createRepository();

        // master branch
        Branch master = repo.readBranch("master");

        // create some nodes
        ObjectNode obj1 = NodeBuilder.start("city").is("milwaukee").and("country").is("usa").and("zipcode").is("53221").get();
        Node node1 = (Node) master.createNode(obj1);
        ObjectNode obj2 = NodeBuilder.start("city").is("chicago").and("country").is("usa").and("zipcode").is("60613").get();
        Node node2 = (Node) master.createNode(obj2);
        ObjectNode obj3 = NodeBuilder.start("city").is("ithaca").and("country").is("usa").and("zipcode").is("14850").get();
        Node node3 = (Node) master.createNode(obj3);
        ObjectNode obj4 = NodeBuilder.start("city").is("austin").and("country").is("usa").and("zipcode").is("78730").get();
        Node node4 = (Node) master.createNode(obj4);
        ObjectNode obj5 = NodeBuilder.start("city").is("boston").and("country").is("usa").and("zipcode").is("02138").get();
        Node node5 = (Node) master.createNode(obj5);
        ObjectNode obj6 = NodeBuilder.start("city").is("la paz").and("country").is("bolivia").and("zipcode").is("N/A").get();
        Node node6 = (Node) master.createNode(obj6);

        // query for all nodes in the USA
        ObjectNode query1 = QueryBuilder.start("country").is("usa").get();
        Map<String, BaseNode> results1 = master.queryNodes(query1);
        assertEquals(5, results1.size());

        // query for all nodes in Bolivia (should fine 1)
        ObjectNode query2 = QueryBuilder.start("country").is("bolivia").get();
        Map<String, BaseNode> results2 = master.queryNodes(query2);
        assertEquals(1, results2.size());

        // query for all nodes that are in chicago or ithaca
        ObjectNode query3 = QueryBuilder.start().or(
                QueryBuilder.start("city").is("chicago").get(),
                QueryBuilder.start("city").is("ithaca").get())
                .get();
        Map<String, BaseNode> results3 = master.queryNodes(query3);
        assertEquals(2, results3.size());


        // create a second branch
        Branch branch = repo.createBranch(master.getTipChangesetId());

        // add another city in Bolivia
        ObjectNode obj7 = NodeBuilder.start("city").is("sucre").and("country").is("bolivia").and("zipcode").is("N/A").get();
        Node node7 = (Node) branch.createNode(obj7);

        // query for nodes in bolivia (should now find 2)
        ObjectNode query4 = QueryBuilder.start("country").is("bolivia").get();
        Map<String, BaseNode> results4 = branch.queryNodes(query4);
        assertEquals(2, results4.size());


        // query the master branch (should still return 1)
        ObjectNode query5 = QueryBuilder.start("country").is("bolivia").get();
        Map<String, BaseNode> results5 = master.queryNodes(query5);
        assertEquals(1, results5.size());


        // create a container
        ObjectNode obj8 = NodeBuilder.start("city").is("san francisco").hasType("n:node").isContainer().hasQName("o:123").get();
        Node node8 = (Node) master.createNode(obj8);

        // find all nodes that are containers (using dot notation)
        // there should be three (the root node (qname = n:root), the users node (qname = r:users) and node8)
        ObjectNode query6 = QueryBuilder.start("_features.f:container").is(QueryBuilder.start("$exists").is(true).get()).get();
        Map<String, BaseNode> results6 = master.queryNodes(query6);
        assertEquals(3, results6.size());

        // run a paginated query
        ObjectNode query7 = QueryBuilder.start("country").is("usa").get();
        Pagination pagination7 = new Pagination();
        pagination7.setSkip(0);
        pagination7.setLimit(3);
        Map<String, BaseNode> results7 = master.queryNodes(query7, pagination7);
        assertEquals(3, results7.size());

        // next page
        ObjectNode query8 = QueryBuilder.start("country").is("usa").get();
        Pagination pagination8 = new Pagination();
        pagination8.setSkip(3);
        pagination8.setLimit(3);
        Map<String, BaseNode> results8 = master.queryNodes(query8, pagination8);
        assertEquals(2, results8.size());

    }
}
