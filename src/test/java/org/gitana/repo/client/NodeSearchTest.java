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
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

import java.util.Map;

/**
 * @author uzi
 */
public class NodeSearchTest extends AbstractTestCase
{
    @Test
    public void testSearch()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = server.createRepository();

        // master branch
        Branch master = repo.readBranch("master");

        String test = "test-" + System.currentTimeMillis();

        // create first node
        ObjectNode obj1 = NodeBuilder.start("title").is("Gone with the Wind").and("author").is("Margaret Mitchell").get();
        Node node1 = master.createNode(obj1);

        // create second node
        ObjectNode obj2 = NodeBuilder.start("title").is("To Kill a Mockingbird").and("author").is("Harper Lee").get();
        Node node2 = master.createNode(obj2);

        // create third node
        ObjectNode obj3 = NodeBuilder.start("title").is("War and Peace").and("author").is("Leo Tolstoy").get();
        Node node3 = master.createNode(obj3);

        // create fourth node
        ObjectNode obj4 = NodeBuilder.start("title").is("The Sound and the Fury").and("author").is("William Faulkner").get();
        Node node4 = master.createNode(obj4);

        // here we wait a little bit for the asynchronous indexing on the server side to complete
        Thread.sleep(2000);


        // search #1
        Map<String, Node> results1 = master.searchNodes("Gone");
        assertEquals(1, results1.size());

        // search #2
        Map<String, Node> results2 = master.searchNodes("Harper");
        assertEquals(1, results2.size());


        // now upload an attachment to the first node
        // here, we upload the full pdf for "gone with the wind"
        node4.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/gone.pdf"), "application/pdf");


        // here we wait a little bit for the asynchronous indexing on the server side to complete
        // NOTE: the call over to ElasticSearch is, in fact, synchronous but the indexing engine itself returns
        // before the indexing is entirely completed.
        // as such, since this is a pretty large document (3.5 megs) of a pretty wordy book, we wait a good amount of
        // time here to make sure the indexing truly finishes.
        Thread.sleep(20000);


        // now let's query for some text from the PDF
        Map<String, Node> results3 = master.searchNodes("Georgia");
        assertEquals(1, results3.size());

        // now let's query for some text from the PDF
        Map<String, Node> results4 = master.searchNodes("Miss Scarlett");
        assertEquals(1, results4.size());

    }
}
