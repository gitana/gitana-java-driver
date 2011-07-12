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
import org.gitana.repo.query.QueryBuilder;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class NodeTest extends AbstractTestCase
{
    @Test
    public void testCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = server.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // create three nodes
        Node node1 = (Node) master.createNode();
        Node node2 = (Node) master.createNode();
        Node node3 = (Node) master.createNode();

        // read nodes back (verify)
        Node verify1 = (Node) master.readNode(node1.getId());
        assertNotNull(verify1);
        Node verify2 = (Node) master.readNode(node2.getId());
        assertNotNull(verify2);
        Node verify3 = (Node) master.readNode(node3.getId());
        assertNotNull(verify3);

        // update a node
        node2.set("axl", "rose");
        node2.update();

        // read node back to verify
        Node verify4 = (Node) master.readNode(node2.getId());
        assertEquals("rose", verify4.getString("axl"));

        // delete the second node
        node2.delete();

        // verify that we can't read it
        Node verify5 = (Node) master.readNode(node2.getId());
        assertNull(verify5);
    }

    @Test
    public void testAttachments()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = server.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // create a node
        Node node = (Node) master.createNode();

        // upload
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/daffy.jpeg");
        node.uploadAttachment("thumb", bytes, "image/jpeg");

        // download and verify
        byte[] verify = node.downloadAttachment("thumb");
        assertEquals(bytes.length, verify.length);

        // update a node
        node.set("axl", "rose");
        node.update();

        // read node back to verify
        Node verify2 = (Node) master.readNode(node.getId());
        assertEquals("rose", verify2.getString("axl"));

        verify = node.downloadAttachment("thumb");
        assertEquals(bytes.length, verify.length);
    }

    @Test
    public void testPagination()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = server.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // create a whole bunch of nodes
        for (int i = 0; i < 20; i++)
        {
            ObjectNode data = JsonUtil.createObject();
            data.put("value", i + 1);

            master.createNode(data);
        }

        // pagination size 10, offset 0
        Pagination pagination = new Pagination();
        pagination.setSkip(0);
        pagination.setLimit(10);

        // query
        ObjectNode query = QueryBuilder.start("value").greaterThan(0).get();

        // first ten
        ResultMap<BaseNode> nodes1 = master.queryNodes(query, pagination);
        assertEquals(10, nodes1.size());
        assertEquals(20, nodes1.totalRows());
        assertEquals(0, nodes1.offset());

        // second ten
        pagination.setSkip(10);
        ResultMap<BaseNode> nodes2 = master.queryNodes(query, pagination);
        assertEquals(10, nodes2.size());
        assertEquals(20, nodes2.totalRows());
        assertEquals(10, nodes2.offset());

        // ensure first set is correct
        for (BaseNode node: nodes1.values())
        {
            int value = node.getInt("value");
            assertTrue(value > 0 && value <= 10);
        }

        // ensure second set is correct
        for (BaseNode node: nodes2.values())
        {
            int value = node.getInt("value");
            assertTrue(value > 10 && value <= 20);
        }
    }
}
