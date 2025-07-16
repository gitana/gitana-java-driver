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
package org.gitana.platform.load;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.support.Pagination;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.List;

/**
 * 13 concurrent threads.  257 total iterations.
 *
 * @author uzi
 */
public class MultiThreadTest extends AbstractLoadTest<Void>
{
    private Driver driver = null;
    private Branch branch = null;

    @Override
    protected Runner<Void> createRunner(String id, int index)
    {
        MultiThreadRunner runner = new MultiThreadRunner(id);
        runner.setDriver(this.driver);
        runner.setBranch(this.branch);

        return runner;
    }

    @Override
    protected int getNumberOfRunners()
    {
        return 13;
    }

    @Override
    protected int getIterationCount()
    {
        return 257;
    }

    @Test
    public void test1()
        throws Exception
    {
        // authenticate
        Platform platform = (new Gitana()).authenticate("admin", "admin");
        this.driver = DriverContext.getDriver();

        // create repository and fetch the branch reference
        this.branch = platform.createRepository().readBranch("master");

        ObjectNode q = JsonUtil.createObject();
        q.put("_type", "n:node");

        // count the number of nodes
        int nodeCount1 = this.branch.queryNodes(q, Pagination.limit(99999)).size();

        long t1 = System.currentTimeMillis();
        List<RunnerResult<Void>> runners = execute();
        long totalTime = System.currentTimeMillis() - t1;

        float timePerRunner = ((float) ((float) totalTime) / ((float) getIterationCount()));

        // read back the histogram
        System.out.println("Total time: " + totalTime + ", time per runner: " + timePerRunner);

        // count the number of nodes
        int nodeCount2 = this.branch.queryNodes(q, Pagination.limit(99999)).size();

        System.out.println("a1: " + getIterationCount());
        System.out.println("a2: " + (nodeCount2 - nodeCount1));

        assertEquals(getIterationCount(), nodeCount2 - nodeCount1);
    }

}
