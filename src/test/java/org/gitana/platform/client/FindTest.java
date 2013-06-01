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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.JSONBuilder;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.node.NodeBuilder;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class FindTest extends AbstractTestCase
{
    @Test
    public void testBranchFind()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = platform.createRepository();

        // master branch
        Branch master = repo.readBranch("master");

        // create some nodes
        ObjectNode obj1 = NodeBuilder.start("city").is("milwaukee").and("country").is("usa").and("zipcode").is("53221").get();
        Node node1 = (Node) master.createNode(obj1);
        ObjectNode obj2 = NodeBuilder.start("city").is("chicago").and("country").is("usa").and("zipcode").is("60613").get();
        Node node2 = (Node) master.createNode(obj2);
        ObjectNode obj3 = NodeBuilder.start("city").is("ithaca").and("country").is("usa").and("zipcode").is("14850").get();
        Node node3 = (Node) master.createNode(obj3);
        ObjectNode obj4 = NodeBuilder.start("city").is("austin").and("country").is("usa").and("zipcode").is("78730").get();
        Node node4 = (Node) master.createNode(obj4);
        ObjectNode obj5 = NodeBuilder.start("city").is("boston").and("country").is("usa").and("zipcode").is("02138").get();
        Node node5 = (Node) master.createNode(obj5);
        ObjectNode obj6 = NodeBuilder.start("city").is("la paz").and("country").is("bolivia").and("zipcode").is("N/A").get();
        Node node6 = (Node) master.createNode(obj6);
        ObjectNode obj7 = NodeBuilder.start("city").is("chicago").and("country").is("usa").and("zipcode").is("60657").get();
        Node node7 = (Node) master.createNode(obj7);
        ObjectNode obj8 = NodeBuilder.start("city").is("chicago").and("country").is("canada").get();
        Node node8 = (Node) master.createNode(obj8);

        // wait until indexing finishes
        Thread.sleep(15000);

        // find all nodes with the text "usa" in them
        // should be 6
        ResultMap<BaseNode> search1 = master.searchNodes("usa");
        assertEquals(6, search1.size());

        // use a FIND
        // find where city == "chicago" and has text "usa"
        ObjectNode query2 = QueryBuilder.start("city").is("chicago").get();
        ResultMap<BaseNode> search2 = master.findNodes(query2, "usa");
        assertEquals(2, search2.size());

        // use a FIND
        // empty query, just text "usa"
        ResultMap<BaseNode> search3 = master.findNodes(null, "usa");
        assertEquals(6, search3.size());

        // use a FIND
        // query but no text
        ObjectNode query4 = QueryBuilder.start("city").is("chicago").get();
        ResultMap<BaseNode> search4 = master.findNodes(query4, null);
        assertEquals(3, search4.size());
    }

    @Test
    public void testNodeFind()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = platform.createRepository();

        // master branch
        Branch master = repo.readBranch("master");


        //
        // PLANETS
        //

        // earth
        ObjectNode earthObject = NodeBuilder.start("title").is("Earth").and("type").is("planet").and("population").is(6775235).get();
        Node earth = (Node) master.createNode(earthObject);
        earth.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bio_earth.txt"), "text/plain");


        //
        // COUNTRIES
        //

        // usa
        ObjectNode usaObject = NodeBuilder.start("title").is("USA").and("type").is("country").and("population").is(307006).get();
        Node usa = (Node) master.createNode(usaObject);
        usa.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bio_usa.txt"), "text/plain");

        // bolivia
        ObjectNode boliviaObject = NodeBuilder.start("title").is("Bolivia").and("type").is("country").and("population").is(9863).get();
        Node bolivia = (Node) master.createNode(boliviaObject);
        bolivia.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bio_bolivia.txt"), "text/plain");


        //
        // CITIES
        //

        // milwaukee
        ObjectNode milwaukeeObject = NodeBuilder.start("title").is("Milwaukee").and("type").is("city").and("population").is(959).get();
        Node milwaukee = (Node) master.createNode(milwaukeeObject);
        milwaukee.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bio_milwaukee.txt"), "text/plain");

        // boston
        ObjectNode bostonObject = NodeBuilder.start("title").is("Boston").and("type").is("city").and("population").is(600).get();
        Node boston = (Node) master.createNode(bostonObject);
        boston.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bio_boston.txt"), "text/plain");

        // chicago
        ObjectNode chicagoObject = NodeBuilder.start("title").is("Chicago").and("type").is("city").and("population").is(2896).get();
        Node chicago = (Node) master.createNode(chicagoObject);
        chicago.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bio_chicago.txt"), "text/plain");

        // la paz
        ObjectNode lapazObject = NodeBuilder.start("title").is("La Paz").and("type").is("city").and("population").is(877).get();
        Node lapaz = (Node) master.createNode(lapazObject);
        lapaz.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bio_lapaz.txt"), "text/plain");

        // sucre
        ObjectNode sucreObject = NodeBuilder.start("title").is("Sucre").and("type").is("city").and("population").is(247).get();
        Node sucre = (Node) master.createNode(sucreObject);
        sucre.uploadAttachment(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bio_sucre.txt"), "text/plain");

        // build associations
        earth.associate(usa, QName.create("a:child"));
        earth.associate(bolivia, QName.create("a:child"));
        usa.associate(milwaukee, QName.create("a:child"));
        usa.associate(boston, QName.create("a:child"));
        usa.associate(chicago, QName.create("a:child"));
        bolivia.associate(sucre, QName.create("a:child"));
        bolivia.associate(lapaz, QName.create("a:child"));

        // wait until indexing finishes
        Thread.sleep(20000);
        waitForZeroWaitingJobs();

        // full search for gitana111
        ResultMap<BaseNode> search1 = master.searchNodes("gitana111");
        assertEquals(4, search1.size());

        // full search for gitana222
        ResultMap<BaseNode> search2 = master.searchNodes("gitana222");
        assertEquals(4, search2.size());


        // BRANCH FIND
        // query for cities with text "gitana111"
        ResultMap<BaseNode> find1 = master.findNodes(QueryBuilder.start("type").is("city").get(), "gitana111");
        assertEquals(3, find1.size());

        // query for cities with text "gitana222"
        ResultMap<BaseNode> find2 = master.findNodes(QueryBuilder.start("type").is("city").get(), "gitana222");
        assertEquals(2, find2.size());


        // NODE FIND
        // query for things with population > 100 that are one hop away from earth
        // should find USA and Bolivia
        ResultMap<BaseNode> find3 = earth.findNodes(QueryBuilder.start("population").greaterThan(100).get(), null, JSONBuilder.start("depth").is(1).get());
        assertEquals(2, find3.size());

        // NODE FIND
        // query for things with population > 10000 that are one hop away from earth
        // should find USA
        ResultMap<BaseNode> find4 = earth.findNodes(QueryBuilder.start("population").greaterThan(10000).get(), null, JSONBuilder.start("depth").is(1).get());
        assertEquals(1, find4.size());

        // NODE FIND
        // query for cities that are within one hop away from earth
        // should find zero
        ResultMap<BaseNode> find5 = earth.findNodes(QueryBuilder.start("type").is("city").get(), null, JSONBuilder.start("depth").is(1).get());
        assertEquals(0, find5.size());

        // NODE FIND
        // query for cities that are within two hop away from earth
        // should find five
        ResultMap<BaseNode> find6 = earth.findNodes(QueryBuilder.start("type").is("city").get(), null, JSONBuilder.start("depth").is(2).get());
        assertEquals(5, find6.size());

        // NODE FIND
        // query for cities that are within two hop away from earth
        // and have text "gitana111"
        // should find three
        ResultMap<BaseNode> find7 = earth.findNodes(QueryBuilder.start("type").is("city").get(), "gitana111", JSONBuilder.start("depth").is(2).get());
        assertEquals(3, find7.size());

    }

}
