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
 *   info@gitana.io
 */
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.node.NodeBuilder;
import org.gitana.platform.support.QueryBuilder;
import org.junit.Test;

import java.util.Map;

/**
 * Tests out queries that filter on _fields.
 *
 * @author uzi
 */
public class NodeQuery2Test extends AbstractTestCase
{
    @Test
    public void testQuery()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = platform.createRepository();

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
        // only return zipcode
        // note that we also must return "_qname" in order for the java driver to work properly
        // note that we also must return "_type" in order for the java driver to work properly
        ObjectNode _fields1 = QueryBuilder.start("zipcode").is(1).and("_qname").is(1).and("_type").is(1).get();
        ObjectNode query1 = QueryBuilder.start("country").is("usa").and("_fields").is(_fields1).get();
        Map<String, BaseNode> results1 = master.queryNodes(query1);
        assertEquals(5, results1.size());

        // walk each node and ensure it only has zipcode and _doc
        for (BaseNode node: results1.values())
        {
            String _doc = node.getId();
            assertNotNull(_doc);

            String zipcode = node.getString("zipcode");
            assertNotNull(zipcode);

            String country = node.getString("country");
            assertNull(country);
        }
    }
}
