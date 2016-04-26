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

package org.gitana.platform.client;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.load.Runner;
import org.gitana.platform.load.RunnerResult;
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
public class ParallelUploadTest extends AbstractTestCase
{
    @Test
    public void testUpload()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = platform.createRepository();

        // master branch
        Branch master = repo.readBranch("master");

        // run 30 uploads
        int count = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletionService<Void> cs = new ExecutorCompletionService<Void>(executorService);
        for (int i = 0; i < count; i++)
        {
            // create the runner
            Runner<Void> runner = createRunner("runner-" + i, DriverContext.getDriver(), master);
            runner.init();

            cs.submit(runner);
        }

        // wait for everything to finish
        List<RunnerResult<Void>> results = new ArrayList<RunnerResult<Void>>();
        for (int i = 0; i < count; i++)
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
    }

    protected Runner<Void> createRunner(String runnerId, Driver driver, Branch branch)
    {
        ParallelUploadRunner runner = new ParallelUploadRunner(runnerId);
        runner.setDriver(driver);
        runner.setBranch(branch);

        return runner;
    }

}
