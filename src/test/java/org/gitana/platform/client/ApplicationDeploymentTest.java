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

package org.gitana.platform.client;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.webhost.DeployedApplication;
import org.gitana.platform.client.webhost.WebHost;
import org.gitana.util.HttpUtil;
import org.gitana.util.HttpUtilEx;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author uzi
 */
@Ignore
public class ApplicationDeploymentTest extends AbstractTestCase
{
    @Test
    public void testDeployments()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a web host
        WebHost webhost = platform.createWebHost();
        webhost.setDeployerTypes(Arrays.asList("dev-cloudcms.net", "cloudcms.net"));
        webhost.update();

        // create an application
        Application application = platform.createApplication();
        //application.addDeployment("test", webhost.getId(), "gitana-build-test-" + System.currentTimeMillis(), "dev-cloudcms.net", JSONBuilder.start("test").is(true).get());
        //application.setSource("github", true, "https://github.com/gitana/app-html5-test.git");
        application.addDeployment("test", webhost.getId(), "gitana-build-test-" + System.currentTimeMillis(), "cloudcms.net", JSONBuilder.start("test").is(true).get());
        application.setSource("github", true, "https://github.com/solocal/hplace.git");
        application.update();

        // deploy the application
        DeployedApplication deployedApplication = application.deploy("test");
        String url = deployedApplication.getUrls().get(0);

        // load to verify it's up
        HttpResponse response = HttpUtilEx.get(HttpUtil.buildClient(), url);
        assertEquals(200, response.getStatusLine().getStatusCode());

        // consume
        EntityUtils.consume(response.getEntity());

        // now undeploy
        deployedApplication.undeploy();
    }

}
