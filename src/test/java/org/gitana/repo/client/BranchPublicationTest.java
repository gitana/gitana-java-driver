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

import org.gitana.JSONBuilder;
import org.gitana.repo.binary.BinaryObject;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.namespace.QName;
import org.gitana.util.StreamUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * @author uzi
 */
public class BranchPublicationTest extends AbstractTestCase
{
    @Test
    public void test()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo1 = server.createRepository();

        // get the master branch
        Branch master1 = repo1.readBranch("master");

        // create a bunch of nodes
        Node node1 = (Node) master1.createNode();
        Node node2 = (Node) master1.createNode();
        Node node3 = (Node) master1.createNode(JSONBuilder.start("def").is("jam").get());
        Node node4 = (Node) master1.createNode();
        Node node5 = (Node) master1.createNode();

        // connect together
        QName aChild = QName.create("a:child");
        node1.associate(node2, aChild);
        node1.associate(node3, aChild);
        node1.associate(node4, aChild);
        node4.associate(node5, aChild);

        // export
        BinaryObject exportArchive = master1.exportPublication();

        // NOTE: the "exportArchive" doesn't contain the data - it's just a descriptor of what was exported
        // it has an input stream
        // so let's pull down the bytes
        byte[] bytes = StreamUtil.getBytes(exportArchive.getInputStream());




        // create a new repository
        Repository repo2 = server.createRepository();

        // master branch
        Branch master2 = repo2.readBranch("master");

        // import
        master2.importPublication(new ByteArrayInputStream(bytes), exportArchive.getLength(), exportArchive.getContentType());



        // verify it worked
        Node x1 = (Node) master2.readNode(node1.getId());
        assertNotNull(x1);
        assertEquals(node1.associations().size(), x1.associations().size());

    }

}
