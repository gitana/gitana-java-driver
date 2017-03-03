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

package org.gitana.platform.client.webhost;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public class DeployedApplicationImpl extends AbstractWebHostDocumentImpl implements DeployedApplication
{
    public DeployedApplicationImpl(WebHost webhost, ObjectNode obj, boolean isSaved)
    {
        super(webhost, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_DEPLOYED_APPLICATION;
    }

    @Override
    public String getResourceUri()
    {
        return "/webhosts/" + getWebHostId() + "/applications/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof DeployedApplication)
        {
            DeployedApplication other = (DeployedApplication) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELFABLE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put(getResourceUri(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete(getResourceUri());
    }

    @Override
    public void reload()
    {
        AutoClientMapping settings = getWebHost().readAutoClientMapping(getId());

        this.reload(settings.getObject());
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getApplicationId()
    {
        return getString(FIELD_APPLICATION_ID);
    }

    @Override
    public void setApplicationId(String applicationId)
    {
        set(FIELD_APPLICATION_ID, applicationId);
    }

    @Override
    public String getDeploymentKey()
    {
        return getString(FIELD_DEPLOYMENT_KEY);
    }

    @Override
    public void setDeploymentKey(String deploymentKey)
    {
        set(FIELD_DEPLOYMENT_KEY, deploymentKey);
    }

    @Override
    public String getDeploymentWebhost()
    {
        return getString(FIELD_DEPLOYMENT_WEBHOST);
    }

    @Override
    public void setDeploymentWebhost(String deploymentWebhost)
    {
        set(FIELD_DEPLOYMENT_WEBHOST, deploymentWebhost);
    }

    @Override
    public String getDeploymentSubdomain()
    {
        return getString(FIELD_DEPLOYMENT_SUBDOMAIN);
    }

    @Override
    public void setDeploymentSubdomain(String deploymentSubdomain)
    {
        set(FIELD_DEPLOYMENT_SUBDOMAIN, deploymentSubdomain);
    }

    @Override
    public String getDeploymentDomain()
    {
        return getString(FIELD_DEPLOYMENT_DOMAIN);
    }

    @Override
    public void setDeploymentDomain(String deploymentDomain)
    {
        set(FIELD_DEPLOYMENT_DOMAIN, deploymentDomain);
    }

    @Override
    public List<String> getUrls()
    {
        List<String> urls = new ArrayList<String>();

        ArrayNode array = getArray(FIELD_URLS);
        if (array != null)
        {
            urls.addAll((List) JsonUtil.toValue(array));
        }

        return urls;
    }

    @Override
    public void setUrls(List<String> urls)
    {
        JsonUtil.objectPut(getObject(), FIELD_URLS, urls);
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DEPLOYMENT
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void undeploy()
    {
        getRemote().post(getResourceUri() + "/undeploy");
    }

    @Override
    public void redeploy()
    {
        getRemote().post(getResourceUri() + "/redeploy");
    }

    @Override
    public void start()
    {
        getRemote().post(getResourceUri() + "/start");
    }

    @Override
    public void stop()
    {
        getRemote().post(getResourceUri() + "/stop");
    }

    @Override
    public void restart()
    {
        getRemote().post(getResourceUri() + "/restart");
    }

}
