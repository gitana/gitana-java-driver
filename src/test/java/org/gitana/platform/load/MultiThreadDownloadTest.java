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

import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.support.RemoteImpl;
import org.gitana.util.HttpUtil;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author uzi
 */
public class MultiThreadDownloadTest extends AbstractStepLoadTest<RunnerResponse>
{
    private Remote remote = new RemoteImpl(HttpUtil.buildClient(), null);

    @Override
    public void tearDown() throws Exception
    {
        System.gc();
    }

    @Override
    protected Runner<RunnerResponse> createRunner(String id)
    {
        MultiThreadDownloadRunner runner = new MultiThreadDownloadRunner(id);
        runner.setRemote(remote);

        return runner;
    }

    @Test
    public void test1()
        throws Exception
    {
        int numberOfThreads = 100;

        String _numberOfThreads = System.getProperty("numberOfThreads");
        if (_numberOfThreads != null)
        {
            numberOfThreads = Integer.parseInt(_numberOfThreads);
        }

        doTest(numberOfThreads);
    }

    protected void doTest(int numberOfThreads)
        throws Exception
    {
        int iterationsPerThread = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        int count = 0;
        while (count < 10)
        {
            float avgMsPerThread = measure(executorService, iterationsPerThread);

            //float totalMeanAcrossAllConcurrentRequestsMs = (avgMsPerThread / ((float) numberOfThreads));

            System.out.println(count + "> Number of Threads: " + numberOfThreads + ", iterations: " + iterationsPerThread + ", mean concurrent request ms: " + avgMsPerThread);
            count++;
        }
    }

    protected float measure(ExecutorService executorService, int iterationsPerThread)
        throws Exception
    {
        List<RunnerResult<RunnerResponse>> runners = execute(executorService, iterationsPerThread);
        float totalResponseTime = (float) 0;

        for (int i = 0; i < runners.size(); i++)
        {
            RunnerResult<RunnerResponse> runner = runners.get(i);

            RunnerResponse response = runner.get();
            long responseTime = response.getResponseTime();

            totalResponseTime += ((float) responseTime);
        }

        // average out
        return ((float) totalResponseTime / ((float) iterationsPerThread));
    }

}
