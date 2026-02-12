/**
 * Copyright 2026 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.load.AbstractRunner;
import org.gitana.platform.load.Runner;
import org.gitana.platform.load.RunnerResult;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author uzi
 */
public class Repository4Test extends AbstractTestCase
{
    private final static int THREAD_COUNT = 10;
    private final static int ITERATIONS = 200;

    @Test
    public void test()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();
        repository.set("releases", JsonUtil.createObject());
        repository.setTitle("My Repository");

        // add a thousand properties to it
        for (int i = 0; i < 1000; i++)
        {
            repository.set("p" + i, "v" + i);
        }

        // update the repo
        repository.update();

        // executor service
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        try
        {
            // run threads in parallel
            CompletionService<Void> cs = new ExecutorCompletionService<Void>(executorService);
            for (int i = 0; i < ITERATIONS; i++)
            {
                // create the runner
                Runner<Void> runner = createRunner("runner-" + i, DriverContext.getDriver(), repository);
                runner.init();

                cs.submit(runner);
            }

            // wait for everything to finish
            List<RunnerResult<Void>> results = new ArrayList<RunnerResult<Void>>();
            for (int i = 0; i < ITERATIONS; i++)
            {
                RunnerResult<Void> result = null;

                try
                {
                    Void v = cs.take().get();

                    result = new RunnerResult<Void>(v);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();

                    result = new RunnerResult<Void>();
                    result.setException(ex);
                }

                results.add(result);
            }

            for (int i = 0; i < ITERATIONS; i++)
            {
                RunnerResult<Void> result = results.get(i);
                assertNull(result.getException());
            }
        }
        finally
        {
            try { executorService.shutdown(); } catch (Exception ex) { }
        }

        // verify repository has what it should have
        repository.reload();

        System.out.println(JsonUtil.stringify(repository.getObject(), true));

        // ensure all properties are still there
        for (int i = 0; i < 1000; i++)
        {
            String v = repository.getString("p" + i);
            assertNotNull(v);
        }

        // ensure the title is still there
        String title = repository.getTitle();
        assertEquals("My Repository", title);
    }

    protected Runner<Void> createRunner(String runnerId, Driver driver, Repository repository)
    {
        RepositoryUpdateRunner runner = new RepositoryUpdateRunner(runnerId);
        runner.setDriver(driver);
        runner.setRepository(repository);

        return runner;
    }

    public class RepositoryUpdateRunner extends AbstractRunner<Void>
    {
        private Repository repository = null;
        private Driver driver = null;

        public RepositoryUpdateRunner(String runnerId)
        {
            super(runnerId);
        }

        public void setDriver(Driver driver)
        {
            this.driver = driver;
        }

        public void setRepository(Repository repository)
        {
            this.repository = repository;
        }

        @Override
        protected void doBeforeExecute() throws Exception
        {
            DriverContext.setDriver(this.driver);
        }

        @Override
        protected void doAfterExecute() throws Exception
        {
            DriverContext.releaseDriver();
            this.repository = null;
        }

        @Override
        protected Void doExecute() throws Exception
        {
            toggle(false);
            toggle(true);

            return null;
        }

        private void toggle(boolean blockMaster)
        {
            repository.reload();

            ObjectNode releases = repository.getObject("releases");
            releases.put("blockMaster", blockMaster);
            repository.update();
        }
    }
}