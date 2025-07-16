/**
 * Copyright 2025 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.association.Direction;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QName;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class FileFolder2Test extends AbstractTestCase
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
        node.setTitle(name);
        node.update();

        parent.associate(node, A_CHILD, Directionality.DIRECTED);

        return node;
    }

    @Test
    public void testRelativesAndChildren()
        throws Exception
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

        // test out list children
        assertEquals(2, root.listChildren().size());
        assertEquals(2, a1.listChildren().size());
        assertEquals(1, a2.listChildren().size());
        assertEquals(2, b1.listChildren().size());
        assertEquals(0, b2.listChildren().size());
        assertEquals(3, c1.listChildren().size());
        assertEquals(2, c2.listChildren().size());
        assertEquals(0, d1.listChildren().size());
        assertEquals(0, d2.listChildren().size());
        assertEquals(0, d3.listChildren().size());
        assertEquals(0, d4.listChildren().size());
        assertEquals(0, d5.listChildren().size());

        // test out list children pagination
        assertEquals(1, c1.listChildren(Pagination.limit(1)).size());

        // test out query children
        assertEquals(2, root.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(2, a1.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(1, a2.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(2, b1.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(0, b2.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(3, c1.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(2, c2.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(0, d1.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(0, d2.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(0, d3.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(0, d4.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());
        assertEquals(0, d5.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject()).size());

        // query with filter
        assertEquals(1, c1.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject("{'title': 'd1'}")).size());
        assertEquals(0, c1.queryRelatives(A_CHILD, Direction.OUTGOING, JsonUtil.createObject("{'title': 'd10'}")).size());
    }
}
