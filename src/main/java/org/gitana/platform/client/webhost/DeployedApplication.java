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
package org.gitana.platform.client.webhost;

import org.gitana.platform.client.support.Selfable;

import java.util.List;

/**
 * @author uzi
 */
public interface DeployedApplication extends WebHostDocument, Selfable
{
    // user fields
    public final static String FIELD_APPLICATION_ID = "applicationId";

    public final static String FIELD_DEPLOYMENT_KEY = "deploymentKey";
    public final static String FIELD_DEPLOYMENT_WEBHOST = "deploymentWebhost";
    public final static String FIELD_DEPLOYMENT_DOMAIN = "deploymentDomain";
    public final static String FIELD_DEPLOYMENT_SUBDOMAIN = "deploymentSubdomain";

    public final static String FIELD_URLS = "urls";

    public String getApplicationId();
    public void setApplicationId(String applicationId);

    public String getDeploymentKey();
    public void setDeploymentKey(String deploymentKey);

    public String getDeploymentWebhost();
    public void setDeploymentWebhost(String deploymentWebhost);

    public String getDeploymentSubdomain();
    public void setDeploymentSubdomain(String deploymentSubdomain);

    public String getDeploymentDomain();
    public void setDeploymentDomain(String deploymentDomain);

    public List<String> getUrls();
    public void setUrls(List<String> urls);


    // operations

    public void undeploy();
    public void redeploy();
    public void start();
    public void stop();
    public void restart();
}
