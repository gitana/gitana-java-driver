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

import org.gitana.JSONBuilder;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.project.Project;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.transfer.TransferImportJob;
import org.gitana.platform.client.transfer.TransferImportJobData;
import org.gitana.platform.client.transfer.TransferImportJobResult;
import org.gitana.platform.services.job.JobState;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author mwhitman
 */
public class CopyFrozenTest extends AbstractTestCase
{
    @Test
    public void testCopyFrozen() throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate as "admin"
        Platform platform = gitana.authenticate("admin", "admin");

        Project project1 = platform.createProject(QueryBuilder.start("title").is("Test Project " + System.currentTimeMillis()).get());
        Repository repository = (Repository) project1.getStack().readDataStore("content");

        /////////////////////////////////////////////////////////////////////////////

        Branch master = repository.readBranch("master");

        // block direct commits on master
        master.set("blockDirectCommits", true);
        master.update();

        Branch branch = repository.createBranch(master.getId(), master.getTipChangesetId(), JsonUtil.createObject());

        BaseNode myNode = branch.createNode(JSONBuilder.start("title").is("copy time").get());

        List<String> nodeIds = Arrays.asList(myNode.getId());

        // try to copy onto master
        // this should fail because BLOCK MASTER is set
        Job job1 = master.startCopyFrom(branch.getRepositoryId(), branch.getId(), nodeIds, JsonUtil.createObject());
        job1.pollForCompletion();
        assertEquals(JobState.ERROR, job1.getState());

        // try again with flag "allowWriteToFrozenBranches" set high
        Job job2 = master.startCopyFrom(branch.getRepositoryId(), branch.getId(), nodeIds, JSONBuilder.start("allowWriteToFrozenBranches").is(true).get());
        job2.pollForCompletion();
        assertEquals(JobState.FINISHED, job2.getState());

        BaseNode myNodeMaster = master.readNode(myNode.getId());
        assertNotNull(myNodeMaster);
    }
}
