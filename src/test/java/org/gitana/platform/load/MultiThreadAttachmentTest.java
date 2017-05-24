/**
 * Copyright 2017 Gitana Software, Inc.
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

package org.gitana.platform.load;

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.Pair;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author uzi
 */
public class MultiThreadAttachmentTest extends AbstractStepLoadTest<RunnerResponse>
{
    private Driver driver = null;
    private BaseNode node = null;

    @Override
    protected Runner<RunnerResponse> createRunner(String id)
    {
        MultiThreadAttachmentRunner runner = new MultiThreadAttachmentRunner(id);
        runner.setDriver(this.driver);
        runner.setNode(this.node);

        return runner;
    }

    @Test
    public void test1()
        throws Exception
    {
        // authenticate
        Platform platform = (new Gitana()).authenticate("admin", "admin");
        this.driver = DriverContext.getDriver();

        // create repository and fetch the branch reference
        Branch branch = platform.createRepository().readBranch("master");

        // create a node
        this.node = branch.createNode();

        // upload attachment
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/load/dog.jpg");
        this.node.uploadAttachment(bytes, MimeTypeMap.IMAGE_JPEG);

        // run the timings

        int maxThreadPoolSize = 100;
        int incrementThreadPoolSize = 5;
        int startSize = 5;
        int iterationsPerStep = 100;

        System.out.println("");
        System.out.println("Timings");
        System.out.println("--------------------------------");
        for (int numberOfThreads = startSize; numberOfThreads < maxThreadPoolSize; numberOfThreads += incrementThreadPoolSize)
        {
            Pair<Float, Float> result = measure(numberOfThreads, iterationsPerStep);

            float totalExecutionTime = result.first();
            float allResponseTimesAddedTogether = result.other();

            //float meanExecutionTimePerThread = (totalExecutionTime / ((float) numberOfThreads));
            float meanResponseTime = (allResponseTimesAddedTogether / ((float) iterationsPerStep * (float) numberOfThreads));

            System.out.println("Number of Threads: " + numberOfThreads + ", iterations: " + iterationsPerStep + ", total execution time: " + totalExecutionTime + ", mean response time: " + meanResponseTime);
        }
    }

    protected Pair<Float, Float> measure(int numberOfThreads, int iterationsPerThread)
        throws Exception
    {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        try
        {
            long t1 = System.currentTimeMillis();

            List<RunnerResult<RunnerResponse>> runners = execute(executorService, iterationsPerThread);
            float totalResponseTime = (float) 0;

            for (int i = 0; i < runners.size(); i++)
            {
                RunnerResult<RunnerResponse> runner = runners.get(i);

                RunnerResponse response = runner.get();
                long responseTime = response.getResponseTime();

                totalResponseTime += ((float) responseTime);
            }

            long t2 = System.currentTimeMillis();

            return new Pair<Float, Float>(((float)(t2 - t1)), totalResponseTime);
        }
        finally
        {
            executorService.shutdownNow();
        }
    }

}
