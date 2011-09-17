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
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.types.NodeList;
import org.gitana.repo.namespace.QName;
import org.gitana.repo.support.ResultMap;
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
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = server.createRepository();

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
