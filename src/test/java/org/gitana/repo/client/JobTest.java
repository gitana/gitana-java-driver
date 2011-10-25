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

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class JobTest extends AbstractTestCase
{
    @Test
    public void testQuery()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = server.createRepository();

        // master branch
        Branch master = repo.readBranch("master");

        // count jobs
        int count1 = countJobs(server);

        // create a node
        // the indexing for this node runs in-proc
        Node node = (Node) master.createNode();

        // upload an attachment
        // the indexing for this attachment will run as a job
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/gone.pdf");
        node.uploadAttachment(bytes, MimeTypeMap.APPLICATION_PDF);

        // check candidates
        ResultMap<Job> candidates = server.listCandidateJobs();
        assertTrue(candidates.size() > 0);

        Thread.sleep(4000);

        // count jobs
        int count2 = countJobs(server);

        assertTrue(count2 > count1);

        // pop last job off
        Pagination pagination = new Pagination();
        pagination.setLimit(1);
        pagination.getSorting().addSortDescending("_system.created_on.ms");
        ResultMap<Job> results = server.queryJobs(JsonUtil.createObject(), pagination);
        Job job = results.values().iterator().next();

        // read job manually
        job = server.readJob(job.getId());
        assertEquals("index", job.getType());

        // test out various methods
        ResultMap<Job> unstarted = server.listUnstartedJobs();
        ResultMap<Job> failed = server.listFailedJobs();
        ResultMap<Job> finished = server.listFinishedJobs();
        ResultMap<Job> all = server.queryJobs(JsonUtil.createObject());
        assertTrue(all.size() > 0);
    }

    private int countJobs(Server server)
    {
        Pagination pagination = new Pagination();
        pagination.setLimit(1);
        ResultMap<Job> results = server.queryJobs(JsonUtil.createObject(), pagination);

        return results.totalRows();
    }
}
