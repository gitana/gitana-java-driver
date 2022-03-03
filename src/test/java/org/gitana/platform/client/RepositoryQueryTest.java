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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Map;

/**
 * @author uzi
 */
public class RepositoryQueryTest extends AbstractTestCase
{
    @Test
    public void testQuery()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        String test = "test-" + System.currentTimeMillis();

        // create two repos
        // TODO: use object builder
        Repository repo1 = platform.createRepository(JsonUtil.createObject("{'test': '" + test + "', 'tag':['test1','test2']}"));
        assertEquals(2, repo1.getArray("tag").size());
        Repository repo2 = platform.createRepository(JsonUtil.createObject("{'test': '" + test + "', 'tag':['test1'], 'length': 10}"));
        assertEquals(10, repo2.getInt("length"));

        // find the repos with "tag=test1"
        ObjectNode query1 = QueryBuilder.start("test").is(test).and("tag").is("test1").get();
        Map<String, Repository> results1 = platform.queryRepositories(query1);
        assertEquals(2, results1.size());

        // find the repos with "length=10"
        ObjectNode query2 = QueryBuilder.start("test").is(test).and("length").is(10).get();
        Map<String, Repository> results2 = platform.queryRepositories(query2);
        assertEquals(1, results2.size());

        // find the repos with "length > 10"
        // @see http://www.mongodb.org/display/DOCS/Advanced+Queries
        ObjectNode query3 = QueryBuilder.start("test").is(test).and("length").greaterThan(10).get();
        Map<String, Repository> results3 = platform.queryRepositories(query3);
        assertEquals(0, results3.size());

        // find the repos with "tag=test1" and "length > 1" and "length < 19"
        // @see http://www.mongodb.org/display/DOCS/Advanced+Queries
        ObjectNode query4 = QueryBuilder.start("test").is(test).and("tag").is("test1").and("length").greaterThan(1).and("length").lessThan(19).get();
        Map<String, Repository> results4 = platform.queryRepositories(query4);
        assertEquals(1, results4.size());

    }
}
