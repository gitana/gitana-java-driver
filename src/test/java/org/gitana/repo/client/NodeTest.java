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

import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.services.Nodes;
import org.junit.Test;

/**
 * @author uzi
 */
public class NodeTest extends AbstractTestCase
{
    @Test
    public void testBranches()
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = gitana.repositories().create();

        // get the master branch
        Branch master = gitana.branches(repository).read("master");

        // nodes
        Nodes nodes = gitana.nodes(master);

        // create three nodes
        Node node1 = nodes.create();
        Node node2 = nodes.create();
        Node node3 = nodes.create();

        // read nodes back (verify)
        Node verify1 = nodes.read(node1.getId());
        assertNotNull(verify1);
        Node verify2 = nodes.read(node2.getId());
        assertNotNull(verify2);
        Node verify3 = nodes.read(node3.getId());
        assertNotNull(verify3);

        // update a node
        node2.set("axl", "rose");
        node2.update();

        // read node back to verify
        Node verify4 = nodes.read(node2.getId());
        assertEquals("rose", verify4.getString("axl"));

        // delete the second node
        node2.delete();

        // verify that we can't read it
        Node verify5 = nodes.read(node2.getId());
        assertNull(verify5);
    }
}
