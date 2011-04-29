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

import org.gitana.repo.association.Direction;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.services.Nodes;
import org.gitana.repo.namespace.QName;
import org.junit.Test;

/**
 * @author uzi
 */
public class AssociationTest extends AbstractTestCase
{
    @Test
    public void testCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = gitana.repositories().create();

        // get the master branch
        Branch master = repository.branches().read("master");

        // nodes
        Nodes nodes = master.nodes();

        // define association type: "custom:isRelatedTo"
        QName isRelatedTo = QName.create("custom:isRelatedTo");
        master.definitions().defineAssociationType(isRelatedTo);

        // define association type: "custom:references"
        QName references = QName.create("custom:references");
        master.definitions().defineAssociationType(references);

        // create some nodes
        Node node1 = nodes.create();
        Node node2 = nodes.create();
        Node node3 = nodes.create();
        Node node4 = nodes.create();
        Node node5 = nodes.create();
        Node node6 = nodes.create();

        // relate them
        //
        // node1
        //   node2
        //   node3
        //     node4
        //       node5
        //       node6
        //

        node1.associate(node2, isRelatedTo);
        node1.associate(node3, isRelatedTo);
        node3.associate(node4, isRelatedTo);
        node4.associate(node5, isRelatedTo);
        node4.associate(node6, references);

        // all association checks
        assertEquals(3, node1.associations().size()); // node2, node3 [custom:isRelatedTo] and a:created
        assertEquals(2, node2.associations().size()); // node1 [custom:isRelatedTo] and a:created
        assertEquals(3, node3.associations().size()); // node1 [custom:isRelatedTo] and node4 [custom:isRelatedTo] and a:created
        assertEquals(4, node4.associations().size()); // node3, node5 [custom:isRelatedTo], node4 [custom:references] and a:created
        assertEquals(2, node5.associations().size()); // node4 [custom:isRelatedTo] and a:created
        assertEquals(2, node6.associations().size()); // node4 [custom:references] and a:created

        // directional
        assertEquals(2, node4.associations(Direction.INCOMING).size()); // node4 and "admin" (a:created)
        assertEquals(2, node4.associations(Direction.OUTGOING).size()); // node5 and node6

        // typed checks
        assertEquals(1, node4.associations(isRelatedTo, Direction.OUTGOING).size()); // node5
        assertEquals(1, node4.associations(references, Direction.OUTGOING).size()); // node6
        assertEquals(1, node4.associations(QName.create("a:created"), Direction.INCOMING).size()); // admin

        // additional
        assertEquals(0, node1.associations(references, Direction.OUTGOING).size());
        assertEquals(0, node1.associations(references, Direction.INCOMING).size());
        assertEquals(1, node1.associations(Direction.INCOMING).size()); // a:created

    }

}
