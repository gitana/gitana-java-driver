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
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

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

        // touch the node for giggles
        node2.touch();

        // delete the second node
        node2.delete();

        // verify that we can't read it
        Node verify5 = (Node) master.readNode(node2.getId());
        assertNull(verify5);

        // create a couple more nodes
        Node node4 = (Node) master.createNode();
        Node node5 = (Node) master.createNode();
        Node node6 = (Node) master.createNode();

        // Do a bulk delete
        List<String> toDelete = Arrays.asList(node1.getId(), node3.getId(), node4.getId(), node5.getId(), node6.getId());
        List<String> deleted = master.deleteNodes(toDelete);
        assertEquals(toDelete.size(), deleted.size());
    }

    @Test
    public void testAttachments()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // create a node
        Node node = (Node) master.createNode();

        // upload
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/daffy.jpeg");
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
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

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
