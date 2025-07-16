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
import org.gitana.http.OAuth2HttpMethodExecutor;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.RemoteImpl;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Map;

/**
 * This is similar to test #1 except we force expiration of the access token by monkeying with the
 * grant and expire times.
 *
 * @author uzi
 */
public class RefreshToken2Test extends TestCase
{
    @Test
    public void test()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create 10 query threads
        RefreshTokenTestQueryRunner r0 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r1 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r2 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r3 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r4 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r5 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r6 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r7 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r8 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);
        RefreshTokenTestQueryRunner r9 = new RefreshTokenTestQueryRunner(DriverContext.getDriver(), platform);

        // create a sabotage thread
        RefreshTokenTestSabotageRunner r10 = new RefreshTokenTestSabotageRunner(DriverContext.getDriver());

        // start 10 threads
        r0.start();
        r1.start();
        r2.start();
        r3.start();
        r4.start();
        r5.start();
        r6.start();
        r7.start();
        r8.start();
        r9.start();
        r10.start();

        // sleep for a minute
        Thread.sleep(60 * 1000);

        // stop all threads
        r0.setTerminate(true);
        r1.setTerminate(true);
        r2.setTerminate(true);
        r3.setTerminate(true);
        r4.setTerminate(true);
        r5.setTerminate(true);
        r6.setTerminate(true);
        r7.setTerminate(true);
        r8.setTerminate(true);
        r9.setTerminate(true);
        r10.setTerminate(true);

        // wait a tick
        Thread.sleep(5 * 1000);
    }

    public class RefreshTokenTestQueryRunner extends Thread
    {
        private Driver driver;
        private Platform platform;
        private boolean terminate;
        private int count;

        public RefreshTokenTestQueryRunner(Driver driver, Platform platform)
        {
            this.driver = driver;
            this.platform = platform;
            this.terminate = false;
            this.count = 0;
        }

        public void setTerminate(boolean terminate)
        {
            this.terminate = terminate;
        }

        @Override
        public void run()
        {
            DriverContext.setDriver(driver);

            while (!terminate)
            {
                try
                {
                    Map<String, Repository> repositories = this.platform.queryRepositories(JsonUtil.createObject());
                    System.out.println("Thread [" + Thread.currentThread().getName() + "] <" + this.count + "> found: " + repositories.size() + " repositories");
                }
                catch (Exception ex)
                {
                    System.out.println("Thread [" + Thread.currentThread().getName() + "] <" + this.count + "> caught exception: " + ex.getMessage());
                    ex.printStackTrace();
                }

                this.count++;
            }
        }
    }

    public class RefreshTokenTestSabotageRunner extends Thread
    {
        private Driver driver;
        private boolean terminate;

        public RefreshTokenTestSabotageRunner(Driver driver)
        {
            this.driver = driver;
            this.terminate = false;
        }

        public void setTerminate(boolean terminate)
        {
            this.terminate = terminate;
        }

        @Override
        public void run()
        {
            DriverContext.setDriver(driver);

            while (!terminate)
            {
                // wait seven seconds
                try
                {
                    Thread.sleep(7000);
                }
                catch (Exception ex) {

                }

                // muck with the access token
                OAuth2HttpMethodExecutor executor = ((OAuth2HttpMethodExecutor) ((RemoteImpl) driver.getRemote()).getHttpMethodExecutor());

                executor.setGrantTime(System.currentTimeMillis());
                executor.setExpiresIn(45); // 45 seconds
            }
        }
    }
}
