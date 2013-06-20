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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.support.QName;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class FileFolderTest extends AbstractTestCase
{
    private static QName A_CHILD = QName.create("a:child");

    private Node createFolder(Branch branch, Node parent, String name)
    {
        Node node = (Node) branch.createNode();
        node.addFeature("f:filename", JsonUtil.createObject());
        node.addFeature("f:container", JsonUtil.createObject());
        node.setTitle(name);
        node.update();

        parent.associate(node, A_CHILD, Directionality.DIRECTED);

        return node;
    }

    private Node createFile(Branch branch, Node parent, String name)
    {
        Node node = (Node) branch.createNode();
        node.addFeature("f:filename", JSONBuilder.start("filename").is(name).get());

        parent.associate(node, A_CHILD, Directionality.DIRECTED);

        return node;
    }

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

        // create a tree structure from ROOT
        Node root = master.rootNode();

        // tree structure
        /**
         *
         *    /
         *    /a1
         *    /a1/b1
         *    /a1/b1/c1
         *    /a1/b1/c1/d1
         *    /a1/b1/c1/d2
         *    /a1/b1/c1/d3
         *    /a1/b1/c2
         *    /a1/b1/c2/d4
         *    /a1/b1/c2/d5
         *    /a1/b2
         *    /a2
         *    /a2/b3
         */
        Node a1 = createFolder(master, root, "a1");
        Node a2 = createFolder(master, root, "a2");
        Node b1 = createFolder(master, a1, "b1");
        Node b2 = createFolder(master, a1, "b2");
        Node b3 = createFolder(master, a2, "b2");
        Node c1 = createFolder(master, b1, "c1");
        Node c2 = createFolder(master, b1, "c2");
        Node d1 = createFile(master, c1, "d1");
        Node d2 = createFile(master, c1, "d2");
        Node d3 = createFile(master, c1, "d3");
        Node d4 = createFile(master, c2, "d4");
        Node d5 = createFile(master, c2, "d5");

        // read the WHOLE tree
        ObjectNode tree1 = root.fileFolderTree();
        System.out.println(JsonUtil.stringify(tree1, true));

        // a few tests to ensure this is good
        ArrayNode children = (ArrayNode) tree1.get("children");
        assertNotNull(children);
        assertEquals(2, children.size());
        assertTrue(children.get(0).get("container").booleanValue());
        assertNotNull(children.get(0).get("filename").textValue());
        assertNotNull(children.get(0).get("label").textValue());
        assertNotNull(children.get(0).get("path").textValue());
        assertNotNull(children.get(0).get("typeQName").textValue());
        assertNotNull(children.get(0).get("qname").textValue());

        // read the tree from root with the "/a1/b1/c1" path already expanded
        ObjectNode tree2 = root.fileFolderTree("/", "/a1/b1/c1");
        System.out.println(JsonUtil.stringify(tree2, true));

        // read the tree segment starting at "/a1/b1"
        ObjectNode tree3 = root.fileFolderTree("/a1/b1");
        System.out.println(JsonUtil.stringify(tree3, true));
    }
}
