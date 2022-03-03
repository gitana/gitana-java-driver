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
import org.gitana.platform.client.deletion.Deletion;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class DeletionTest extends AbstractTestCase
{
    @Test
    public void test1()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // create a few nodes
        ObjectNode template = JsonUtil.createObject();
        template.put("category", "test1");
        Node node1 = (Node) master.createNode(template);
        Node node2 = (Node) master.createNode(template);
        Node node3 = (Node) master.createNode(template);
        Node node4 = (Node) master.createNode(template);
        Node node5 = (Node) master.createNode(template);

        // query for nodes to verify they're there - should be 5
        assertEquals(5, master.queryNodes(template).size());

        // query for deletions - should be 0
        assertEquals(0, master.queryDeletions(template).size());

        // delete two nodes
        node4.delete();
        node5.delete();

        // query for nodes to verify they're there - should be 3
        assertEquals(3, master.queryNodes(template).size());

        // query for deletions - should be 2
        assertEquals(2, master.queryDeletions(template).size());

        // read back delete #4
        Deletion deletion4 = master.readDeletion(node4.getId());
        assertNotNull(deletion4);
        Deletion deletion5 = master.readDeletion(node5.getId());
        assertNotNull(deletion5);

        // restore deletion4
        deletion4.restore();

        // query for nodes to verify they're there - should be 4
        assertEquals(4, master.queryNodes(template).size());

        // query for deletions - should be 1
        assertEquals(1, master.queryDeletions(template).size());
    }
}
