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
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.node.NodeBuilder;
import org.gitana.util.I18NUtil;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

/**
 * @author uzi
 */
public class NodeI18NTest extends AbstractTestCase
{
    @Test
    public void testTranslations()
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = gitana.repositories().create();

        // get the master branch
        Branch master = repository.branches().read("master");

        // locales
        Locale SPANISH = I18NUtil.parseLocale("es_ES");
        Locale GERMAN = I18NUtil.parseLocale("de_DE");
        Locale POLISH = I18NUtil.parseLocale("pl_PL");

        //
        // EDITION 1.0
        //

        // create a node
        ObjectNode data1 = NodeBuilder.start("title").is("We didn't start the fire").and("author").is("Billy Joel").get();
        Node node1 = master.nodes().create(data1);

        // create a translation into spanish (edition = 1.0)
        ObjectNode spanish1 = NodeBuilder.start("title").is("No hemos encendido el fuego").and("author").is("Guillermo Joel").get();
        node1.translations().create("1.0", SPANISH, spanish1);

        // create a translation into german (edition = 1.0)
        ObjectNode german1 = NodeBuilder.start("title").is("Wir begannen auch nicht das Feuer").and("author").is("Billy Joel").get();
        node1.translations().create("1.0", GERMAN, german1);

        // verify spanish translation
        Node spanishTranslation1 = node1.translations().read("1.0", SPANISH);
        assertEquals("Guillermo Joel", spanishTranslation1.getString("author"));

        // verify german translation
        Node germanTranslation1 = node1.translations().read("1.0", GERMAN);
        assertEquals("Wir begannen auch nicht das Feuer", germanTranslation1.getString("title"));


        //
        // EDITION 2.0
        //

        // create an edition 2.0 translation into polish
        ObjectNode polish1 = NodeBuilder.start("title").is("Nie rozpocz?li?my ogie?").and("author").is("Joel").get();
        node1.translations().create("2.0", POLISH, polish1);

        // verify polish translation for edition 2.0
        Node polishTranslation1 = node1.translations().read("2.0", POLISH);
        assertEquals("Joel", polishTranslation1.getString("author"));


        // EDGE CASES

        // verify that no polish translation exists for 1.0
        // we should get back the original node (english)
        Node polishTranslation2 = node1.translations().read("1.0", POLISH);
        assertEquals("Billy Joel", polishTranslation2.getString("author"));

        // verify that no spanish translation exists for 2.0
        // we should get back the original node (english)
        Node spanishTranslation2 = node1.translations().read("2.0", SPANISH);
        assertEquals("Billy Joel", spanishTranslation2.getString("author"));

        // verify that a spanish translation still exists for 1.0
        Node spanishTranslation3 = node1.translations().read("1.0", SPANISH);
        assertEquals("Guillermo Joel", spanishTranslation3.getString("author"));


        // EDITIONS

        // check the editions available for the master node
        List<String> editions = node1.translations().getEditions();
        assertEquals(2, editions.size());

        // check the number of locales in edition 1.0
        // spanish and german
        List<Locale> locales1 = node1.translations().getLocales("1.0");
        assertEquals(2, locales1.size());

        // check the number of locales in edition 2.0
        // just polish
        List<Locale> locales2 = node1.translations().getLocales("2.0");
        assertEquals(1, locales2.size());

    }

}
