/**
 * Copyright 2025 Gitana Software, Inc.
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

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.job.JobState;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Set;

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
        Platform platform = gitana.authenticate("admin", "admin");

        // cluster
        Cluster cluster = platform.getCluster();

        // create a repository
        Repository repo = platform.createRepository();

        // master branch
        Branch master = repo.readBranch("master");

        // count jobs
        int count1 = countTotalJobs(cluster);

        // create a node
        // the indexing for this node runs in-proc
        Node node = (Node) master.createNode();

        // candidate jobs are jobs that are started but not running
        // running jobs are jobs that are started AND running
        int inflight1 = cluster.countJobsInState(Set.of(JobState.WAITING, JobState.RUNNING));

        // upload an attachment
        // the indexing for this attachment will run as a job
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/gone.pdf");
        node.uploadAttachment(bytes, MimeTypeMap.APPLICATION_PDF);

        // wait for indexing to finish
        Thread.sleep(12000);

        // count jobs
        int count2 = countTotalJobs(cluster);

        assertTrue(count2 > count1);

        // pop last job off
        Pagination pagination = new Pagination();
        pagination.setLimit(1);
        pagination.getSorting().addSortDescending("_system.created_on.ms");
        ResultMap<Job> results = cluster.queryJobs(JsonUtil.createObject(), pagination);
        Job job = results.values().iterator().next();

        // read job manually
//        job = cluster.readJob(job.getId());
//        assertEquals("bulkIndex", job.getType());

        // test out various methods
        int c1 = cluster.countJobsInState(Set.of(JobState.ERROR, JobState.FINISHED));
        int c2 = cluster.countUnstartedJobs();
        ResultMap<Job> all = cluster.queryJobs(JsonUtil.createObject());
        assertTrue(all.size() > 0);
    }

    private int countTotalJobs(Cluster cluster)
    {
        Pagination pagination = new Pagination();
        pagination.setLimit(1);
        pagination.getOptions().setCountTotal(true);
        ResultMap<Job> results = cluster.queryJobs(JsonUtil.createObject(), pagination);

        return results.totalRows();
    }
}
