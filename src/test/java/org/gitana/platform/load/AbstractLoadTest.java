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

package org.gitana.platform.load;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.gitana.platform.client.AbstractTestCase;
import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.webhost.DeployedApplication;
import org.gitana.platform.client.webhost.WebHost;
import org.gitana.util.HttpUtil;
import org.gitana.util.HttpUtilEx;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author uzi
 */
public abstract class AbstractLoadTest<V> extends AbstractTestCase
{
    protected abstract Runner<V> createRunner(String id);

    protected MetricRegistry metrics()
    {
        return MetricUtil.registry();
    }

    protected abstract int getNumberOfRunners();

    protected abstract int getIterationCount();

    protected ExecutorService createExecutorService()
    {
        int corePoolSize = getNumberOfRunners();
        //int maximumPoolSize = getNumberOfRunners();
        //int keepAliveTime = 1;
        //TimeUnit timeUnit = TimeUnit.MINUTES;
        //BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(maximumPoolSize, true);
        //RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();

        //int nrOfProcessors = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(corePoolSize);


        //return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, rejectedExecutionHandler);
    }

    protected List<RunnerResult<V>> execute() throws Exception
    {
        ExecutorService executorService = createExecutorService();

        CompletionService<V> cs = new ExecutorCompletionService<V>(executorService);

        for (int i = 0; i < getIterationCount(); i++)
        {
            // create the runner
            Runner<V> runner = createRunner("runner-" + i);
            runner.init();

            cs.submit(runner);
        }

        // wait for everything to finish
        List<RunnerResult<V>> results = new ArrayList<RunnerResult<V>>();
        for (int i = 0; i < getIterationCount(); i++)
        {
            RunnerResult<V> result = null;

            try
            {
                V v = cs.take().get();

                result = new RunnerResult<V>(v);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();

                result = new RunnerResult<V>();
                result.setException(ex);
            }

            results.add(result);
        }

        return results;
    }

    protected Histogram histogram(String histogramId)
    {
        return metrics().histogram(histogramId);
    }

}
