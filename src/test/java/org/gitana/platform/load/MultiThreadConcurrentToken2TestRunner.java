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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.http.OAuth2HttpMethodExecutor;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.RemoteImpl;
import org.gitana.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * @author uzi
 */
public class MultiThreadConcurrentToken2TestRunner
    extends AbstractRunner<Void>
{
    //private final static long WAIT_TIME_MS = 1000 * 60; // one minute
    private final static long WAIT_TIME_MS = 250;

    private Platform platform = null;
    private Driver driver = null;
    private int index = -1;

    private Node node = null;

    public MultiThreadConcurrentToken2TestRunner(int index)
    {
        super("runner-" + index);

        this.index = index;
    }

    @Override
    protected void doBeforeExecute() throws Exception
    {
        println("Initial authentication");

        // delay execution by 1 minute (if we're second thread)
        //Thread.sleep(index * WAIT_TIME_MS);

        // authenticate
        //Platform platform = (new Gitana()).authenticate("admin", "admin");

        ObjectNode config = JsonUtil.createObject();
        config.put("clientKey", "7d801884-6e32-424b-939a-c5dac354a30f");
        config.put("clientSecret", "cs4xzX0nlTpXtVOcsh7Dvp55wwZHVVZF9tXZN+z9JGgDzBOuiy7C5Xz1hgW7efUQkvOkL5qbX74UEqN6paIJw7nF3AHHnM3ItW/uVYF8Vrw=");
        config.put("username", "668b8b56-4342-4f59-868b-10a7734ca1ec");
        config.put("password", "KRAzxj5cnbdwn/B2HoiCUe40+F0cBSHRpBtgQ9rzAWE+s3KwKq3UH7Kchgs/r0dIADjPnuhzaWay9tZGIVjh2RXTtzhBnZPMp2ICXrisIeo=");
        config.put("baseURL", "https://api.cloudcms.com");
        this.platform = Gitana.connect(config);
        this.driver = DriverContext.getDriver();

        Repository repository = this.platform.readRepository("c4ef7a4a9d057a5031d6");
        Branch branch = repository.readBranch("master");
        this.node = (Node) branch.readNode("2c8f59aa76b74465622b");

        // get the current access token and refresh token
        OAuth2HttpMethodExecutor executor = (OAuth2HttpMethodExecutor) ((RemoteImpl) this.driver.getRemote()).getHttpMethodExecutor();

        // before
        String accessToken = executor.getState().getAccessToken();
        String refreshToken = executor.getState().getRefreshToken();

        println("After authentication (access token: " + accessToken + ", refresh token: " + refreshToken + ")");

        //Thread.sleep(1 * WAIT_TIME_MS);
    }

    @Override
    protected void doAfterExecute() throws Exception
    {
    }

    protected void println(String text)
    {
        Date d = new Date();
        System.out.println("[" + d.toString() + "] [" + Thread.currentThread().getName() + "] " + text);
    }

    @Override
    protected synchronized Void doExecute() throws Exception
    {
        // get the current access token and refresh token
        OAuth2HttpMethodExecutor executor = (OAuth2HttpMethodExecutor) ((RemoteImpl) this.driver.getRemote()).getHttpMethodExecutor();

        // delay execution by 1 minute
        //Thread.sleep(1 * WAIT_TIME_MS);

        int errorCount = 0;
        for (int i = 0; i < 5000000; i++)
        {
            String accessToken = executor.getState().getAccessToken();
            String refreshToken = executor.getState().getRefreshToken();

            println("[" + i + "] before (access token: " + accessToken + ", refresh token: " + refreshToken + ")");

            // list repositories
            //((OAuth2HttpMethodExecutor) ((RemoteImpl) DriverContext.getDriver().getRemote()).getHttpMethodExecutor()).setAccessToken("asd");
            InputStream in = null;
            try
            {
                // download the file
                in = new ByteArrayInputStream(node.downloadAttachment());

                ObjectNode obj = JsonUtil.createObject(in, ObjectNode.class);

                println("[" + i + "] downloaded: " + JsonUtil.stringify(obj, false).length() + " chars");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                errorCount++;
            }
            finally
            {
                try { in.close(); } catch (Exception ex0) { }
            }

            // after
            String accessToken1 = executor.getState().getAccessToken();
            String refreshToken1 = executor.getState().getRefreshToken();

            println("[" + i + "] after (access token: " + accessToken1 + ", refresh token: " + refreshToken1 + ")");

            // sleep three minutes
            //Thread.sleep(3 * WAIT_TIME_MS);

            // sleep up to 5 seconds
            long sleepTime = (long) (Math.random() * 5000);
            Thread.sleep(sleepTime);

            println("Total error count: " + errorCount);
        }

        return null;
    }
}
