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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.node.type.NodeList;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.services.node.NodeBuilder;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.I18NUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author uzi
 */
public class NodeListI18NTest
    extends AbstractTestCase
{
    @Test
    public void test1()
    {
        // locales
        Locale SPANISH = I18NUtil.parseLocale("es_ES");
        Locale GERMAN = I18NUtil.parseLocale("de_DE");

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

        // update list to support locales
        list.set("support_multiple_locales", true);
        ArrayNode localesArray = JsonUtil.createArray();
        localesArray.add("de-DE");
        localesArray.add("es-ES");
        list.set("locales", localesArray);
        list.update();

        // read the list back
        list = master.readList("foo");
        assertNotNull(list);

        // add three items
        Node n1 = list.addItem(JSONBuilder.start("title").is("Greendale Default").and("state").is("Wisconsin").get());
        Node n2 = list.addItem(JSONBuilder.start("title").is("Ithaca Default").and("state").is("New York").get());
        Node n3 = list.addItem(JSONBuilder.start("title").is("Syracuse Default").and("state").is("New York").get());

        // create a translation into spanish (edition = 1.0)
        ObjectNode spanish1 = NodeBuilder.start("title").is("Greendale Spanish").get();
        n1.createTranslation("1.0", SPANISH, spanish1);
        ObjectNode spanish2 = NodeBuilder.start("title").is("Ithaca Spanish").get();
        n2.createTranslation("1.0", SPANISH, spanish1);
        ObjectNode spanish3 = NodeBuilder.start("title").is("Syracuse Spanish").get();
        n3.createTranslation("1.0", SPANISH, spanish1);

        // create a translation into german (edition = 1.0)
        ObjectNode german1 = NodeBuilder.start("title").is("Greendale German").get();
        n1.createTranslation("1.0", GERMAN, german1);
        ObjectNode german2 = NodeBuilder.start("title").is("Ithaca German").get();
        n2.createTranslation("1.0", GERMAN, german2);
        ObjectNode german3 = NodeBuilder.start("title").is("Syracuse German").get();
        n3.createTranslation("1.0", GERMAN, german3);

        // list items from the list
        ResultMap<Node> items = list.listItems();
        assertEquals(3, items.size());
        for (Node item: items.values())
        {
            assertTrue(item.getTitle().indexOf("Default") > -1);
        }

        // get the items in SPANISH
        DriverContext.getDriver().setLocale(SPANISH);
        items = list.listItems();
        assertEquals(3, items.size());
        for (Node item: items.values())
        {
            assertTrue(item.getTitle().indexOf("Spanish") > -1);
        }

        // get the items in GERMAN
        DriverContext.getDriver().setLocale(GERMAN);
        items = list.listItems();
        assertEquals(3, items.size());
        for (Node item: items.values())
        {
            assertTrue(item.getTitle().indexOf("German") > -1);
        }

    }
}
