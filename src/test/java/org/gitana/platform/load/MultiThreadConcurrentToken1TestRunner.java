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
package org.gitana.platform.load;

import org.apache.commons.lang.math.RandomUtils;
import org.gitana.http.OAuth2HttpMethodExecutor;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.RemoteImpl;

/**
 * @author uzi
 */
public class MultiThreadConcurrentToken1TestRunner
        extends AbstractRunner<Void>
{
    private Branch branch = null;
    private Driver driver = null;

    public MultiThreadConcurrentToken1TestRunner(String id, Branch branch)
    {
        super(id);

        this.branch = branch;
    }

    @Override
    protected void doBeforeExecute() throws Exception
    {
        System.out.println("[" + Thread.currentThread().getName() + "] initial authentication");

        // authenticate
        Platform platform = (new Gitana()).authenticate("admin", "admin");
        this.driver = DriverContext.getDriver();

        // get the current access token and refresh token
        OAuth2HttpMethodExecutor executor = (OAuth2HttpMethodExecutor) ((RemoteImpl) this.driver.getRemote()).getHttpMethodExecutor();

        // before
        String accessToken = executor.getState().getAccessToken();
        String refreshToken = executor.getState().getRefreshToken();

        System.out.println("[" + Thread.currentThread().getName() + "] after authentication (access token: " + accessToken + ", refresh token: " + refreshToken + ")");
    }

    @Override
    protected void doAfterExecute() throws Exception
    {
    }

    @Override
    protected Void doExecute() throws Exception
    {
        // get the current access token and refresh token
        OAuth2HttpMethodExecutor executor = (OAuth2HttpMethodExecutor) ((RemoteImpl) this.driver.getRemote()).getHttpMethodExecutor();

        for (int i = 0; i < 100; i++)
        {
            // before
            String accessToken = executor.getState().getAccessToken();
            String refreshToken = executor.getState().getRefreshToken();

            System.out.println("[" + Thread.currentThread().getName() + "] [" + i + "] before (access token: " + accessToken + ", refresh token: " + refreshToken + ")");

            // randomly do vile things to the access token
            int rand = RandomUtils.nextInt(10);
            if (rand == 0)
            {
                if (accessToken != null)
                {
                    System.out.println("[" + Thread.currentThread().getName() + "] [" + i + "] corrupting access token");

                    // corrupt the access token
                    executor.getState().setAccessToken(new StringBuilder(accessToken).reverse().toString());
                }
            }
            else if (rand == 1)
            {
                System.out.println("[" + Thread.currentThread().getName() + "] [" + i + "] deleting access token");

                // delete the access token
                executor.getState().setAccessToken(null);
            }

            // attempt to create a node
            // this should fail on the initial call (with a 401) and then do a refresh token acquire to get a new access token
            // and then re-attempt automatically and succeed
            this.branch.createNode();

            // after
            String accessToken1 = executor.getState().getAccessToken();
            String refreshToken1 = executor.getState().getRefreshToken();

            System.out.println("[" + Thread.currentThread().getName() + "] [" + i + "] after (access token: " + accessToken1 + ", refresh token: " + refreshToken1 + ")");
        }

        return null;
    }
}
