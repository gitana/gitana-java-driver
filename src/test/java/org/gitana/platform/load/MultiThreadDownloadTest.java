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

import org.apache.http.client.HttpClient;
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.support.RemoteImpl;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.HttpUtil;
import org.junit.Test;

import java.util.List;

/**
 * 13 concurrent threads.  257 total iterations.
 *
 * @author uzi
 */
public class MultiThreadDownloadTest extends AbstractStepLoadTest<MultiThreadDownloadRunnerResponse>
{
    //private Driver driver = null;
    //private Branch branch = null;

    //private Node node = null;

    private Remote remote = new RemoteImpl(HttpUtil.buildClient(), null);

    @Override
    protected Runner<MultiThreadDownloadRunnerResponse> createRunner(String id)
    {
        MultiThreadDownloadRunner runner = new MultiThreadDownloadRunner(id);
        runner.setRemote(remote);
        //runner.setDriver(this.driver);
        //runner.setNode(node);

        return runner;
    }

    @Test
    public void test1()
        throws Exception
    {
        int incrementSize = 5;
        int startSize = 5;
        int maxSize = 100;
        int iterationsPerStep = 200;

        System.out.println("");
        System.out.println("Timings1");
        System.out.println("--------------------------------");
        for (int numberOfThreads = startSize; numberOfThreads < maxSize; numberOfThreads += incrementSize)
        {
            long t1 = System.currentTimeMillis();
            float average = measure(numberOfThreads, iterationsPerStep);
            long t2 = System.currentTimeMillis();

            float requestsPerSecond = (((float) (t2-t1)) / average);

            System.out.println("Number of Threads: " + numberOfThreads + ", size: " + maxSize + ", avg: " + average + ", total: " + (t2-t1) + ", req/sec: " + requestsPerSecond);
        }
    }

    protected float measure(int numberOfThreads, int totalNumberOfRuns)
        throws Exception
    {
        List<RunnerResult<MultiThreadDownloadRunnerResponse>> runners = execute(numberOfThreads, totalNumberOfRuns);
        float totalResponseTime = (float) 0;

        for (int i = 0; i < runners.size(); i++)
        {
            RunnerResult<MultiThreadDownloadRunnerResponse> runner = runners.get(i);

            MultiThreadDownloadRunnerResponse response = runner.get();
            long responseTime = response.getResponseTime();

            totalResponseTime += ((float) responseTime);
        }

        // average out
        return ((float) totalResponseTime / ((float) runners.size()));
    }

}
