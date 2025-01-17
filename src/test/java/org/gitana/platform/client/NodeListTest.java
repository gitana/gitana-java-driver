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

import org.gitana.JSONBuilder;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.node.type.NodeList;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class NodeListTest extends AbstractTestCase
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

        // create a custom type
        Node foo = master.defineType(QName.create("custom:foo"));

        // create a list
        NodeList list = master.createList("foo", foo.getTypeQName());
        assertNotNull(list);

        // read the list back
        list = master.readList("foo");
        assertNotNull(list);

        // add three items
        Node n1 = list.addItem(JSONBuilder.start("city").is("Greendale").and("state").is("Wisconsin").get());
        Node n2 = list.addItem(JSONBuilder.start("city").is("Ithaca").and("state").is("New York").get());
        Node n3 = list.addItem(JSONBuilder.start("city").is("Syracuse").and("state").is("New York").get());

        // list items from the list
        ResultMap<Node> items = list.listItems();
        assertEquals(3, items.size());

        // query for items in new york
        items = list.queryItems(JSONBuilder.start("state").is("New York").get());
        assertEquals(2, items.size());

        // delete an item (one of the new yorkers)
        n3.delete();

        // query for items in new york
        items = list.queryItems(JSONBuilder.start("state").is("New York").get());
        assertEquals(1, items.size());
    }
}
