/**
 * Copyright 2016 Gitana Software, Inc.
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

package org.gitana.platform.load;

import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.junit.Test;

/**
 * 10 concurrent users, creating nodes 1000
 *
 * @author uzi
 */
public class NodeCreationLoadTest extends AbstractLoadTest<Void>
{
    private String repositoryId = null;
    private String branchId = null;

    @Override
    protected Runner<Void> createRunner(String id)
    {
        NodeCreationRunner runner = new NodeCreationRunner(id);
        runner.setRepositoryId(repositoryId);
        runner.setBranchId(branchId);

        return runner;
    }

    @Override
    protected int getNumberOfRunners()
    {
        return 10;
    }

    @Override
    protected int getIterationCount()
    {
        return 100;
    }

    @Test
    public void test1()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();
        this.repositoryId = repository.getId();

        // branch
        Branch branch = repository.readBranch("master");
        this.branchId = branch.getId();

        long t1 = System.currentTimeMillis();
        execute();
        long totalTime = System.currentTimeMillis() - t1;

        float timePerRunner = ((float) ((float) totalTime) / ((float) getIterationCount()));

        // read back the histogram
        System.out.println("Total time: " + totalTime + ", time per runner: " + timePerRunner);
    }

}
