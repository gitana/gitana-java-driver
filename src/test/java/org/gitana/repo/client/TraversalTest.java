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
import org.gitana.repo.association.Direction;
import org.gitana.repo.client.beans.TraversalResults;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.namespace.QName;
import org.gitana.repo.traversal.TraversalBuilder;
import org.junit.Test;

/**
 * @author uzi
 */
public class TraversalTest extends AbstractTestCase
{
    @Test
    public void testMounts()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = server.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // define a bad guy
        QName baddie = QName.create("boo:baddie");
        master.defineType(baddie);

        // create some wizards
        Node harry = master.createNode();
        Node hermione = master.createNode();
        Node ron = master.createNode();
        Node voldemort = master.createNode(baddie); // type = baddie
        Node dumbledore = master.createNode();
        Node dudley = master.createNode();

        // QNames
        QName friends = QName.create("boo:friends");
        QName enemies = QName.create("boo:enemies");
        QName taught = QName.create("boo:taught");
        QName wantsToKill = QName.create("boo:wants_to_kill");

        // association types
        master.defineAssociationType(friends);
        master.defineAssociationType(enemies);
        master.defineAssociationType(taught);
        master.defineAssociationType(wantsToKill);

        // the three friends
        harry.associate(hermione, friends, Direction.BOTH);
        harry.associate(ron, friends, Direction.BOTH);
        hermione.associate(ron, friends, Direction.BOTH);

        // voldemort wants to kill harry
        voldemort.associate(harry, wantsToKill);

        // dumbledore taught all of them
        dumbledore.associate(harry, taught);
        dumbledore.associate(hermione, taught);
        dumbledore.associate(ron, taught);
        dumbledore.associate(voldemort, taught);

        // dudley
        dudley.associate(harry, friends);



        // some verification



        // who are harry's friends? (depth 1)
        ObjectNode traversal1 = TraversalBuilder.start().depth(1).follow(friends).get();
        TraversalResults results1 = harry.traverse(traversal1);
        assertEquals(3, results1.getNodes().size()); // hermione, ron and dudley
        assertEquals(3, results1.getAssociations().size()); // associations to hermione, ron and dudley

        // who are is harry's teacher?
        ObjectNode traversal2 = TraversalBuilder.start().depth(1).follow(taught, Direction.INCOMING).get();
        TraversalResults results2 = harry.traverse(traversal2);
        assertEquals(1, results2.getNodes().size()); // dumbledore
        assertEquals(1, results2.getAssociations().size()); // dumbledore

        // traverse all friends from dudley (depth 1)
        ObjectNode traversal3 = TraversalBuilder.start().depth(1).follow(friends).get();
        TraversalResults results3 = dudley.traverse(traversal3);
        assertEquals(1, results3.getNodes().size()); // harry

        // traverse all friends from dudley (any depth)
        ObjectNode traversal4 = TraversalBuilder.start().follow(friends).get();
        TraversalResults results4 = dudley.traverse(traversal4);
        assertEquals(3, results4.getNodes().size()); // harry, hermione, ron

        // find any bad guys nearby dudley with no depth constraint
        /*
        ObjectNode traversal5 = TraversalBuilder.start().type(baddie).get();
        TraversalResults results5 = dudley.traverse(traversal5);
        assertEquals(1, results5.getNodes().size()); // voldemort
        */

    }

}
