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

import junit.framework.TestCase;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.services.job.JobState;
import org.junit.Ignore;

import java.util.Set;

/**
 * Base class for tests
 * 
 * @author uzi
 */
@Ignore public abstract class AbstractTestCase extends TestCase
{
	public void setUp() throws Exception
	{
        // turn this bad boy on
        System.getProperties().setProperty("gitana.useAutomaticReattempt", "true");
	}

	public void tearDown() throws Exception
	{
        // wait for all jobs to finish
        waitForZeroWaitingJobs();

        // force the garbage collector (during tests)
        System.gc();
	}

    protected void waitForZeroWaitingJobs()
    {
        // authenticate as admin/admin
        Platform platform = new Gitana().authenticate("admin", "admin");

        boolean done = false;
        do
        {
            int count = platform.getCluster().countJobsInState(Set.of(JobState.WAITING));
            if (count == 0)
            {
                done = true;
            }

            if (!done)
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ie)
                {
                    throw new RuntimeException(ie);
                }
            }
        }
        while (!done);
    }

    public String getTestEmailUsername()
    {
        return System.getenv("GITANA_TEST_EMAIL_USERNAME");
    }

    public String getTestEmailPassword()
    {
        return System.getenv("GITANA_TEST_EMAIL_PASSWORD");
    }

    public String getTestEmailTo()
    {
        return System.getenv("GITANA_TEST_EMAIL_TO");
    }

    public String getTestEmailFrom()
    {
        return System.getenv("GITANA_TEST_EMAIL_FROM");
    }

}
