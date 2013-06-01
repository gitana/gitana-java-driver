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

package org.gitana.platform.client.webhost;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.Map;

/**
 * @author uzi
 */
public class WebHostImpl extends AbstractPlatformDataStoreImpl implements WebHost
{
    public WebHostImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getDeployerType()
    {
        return getString(FIELD_DEPLOYER_TYPE);
    }

    @Override
    public void setDeployerType(String deployerType)
    {
        set(FIELD_DEPLOYER_TYPE, deployerType);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_WEB_HOST;
    }

    @Override
    public String getResourceUri()
    {
        return "/webhosts/" + getId();
    }

    @Override
    public void reload()
    {
        WebHost webhost = getPlatform().readWebHost(getId());
        this.reload(webhost.getObject());
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // AUTO CLIENT MAPPINGS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<AutoClientMapping> listAutoClientMappings()
    {
        return listAutoClientMappings(null);
    }

    @Override
    public ResultMap<AutoClientMapping> listAutoClientMappings(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/autoclientmappings", params);
        return getFactory().autoClientMappings(this, response);
    }

    @Override
    public AutoClientMapping readAutoClientMapping(String autoClientMappingId)
    {
        AutoClientMapping autoClientMapping = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/autoclientmappings/" + autoClientMappingId);
            autoClientMapping = getFactory().autoClientMapping(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return autoClientMapping;
    }

    @Override
    public AutoClientMapping createAutoClientMapping(String uri, String applicationId, String clientKey)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put(AutoClientMapping.FIELD_URI, uri);
        object.put(AutoClientMapping.FIELD_APPLICATION_ID, applicationId);
        object.put(AutoClientMapping.FIELD_CLIENT_KEY, clientKey);

        Response response = getRemote().post(getResourceUri() + "/autoclientmappings", object);

        String autoClientMappingId = response.getId();
        return readAutoClientMapping(autoClientMappingId);
    }

    @Override
    public ResultMap<AutoClientMapping> queryAutoClientMappings(ObjectNode query)
    {
        return queryAutoClientMappings(query, null);
    }

    @Override
    public ResultMap<AutoClientMapping> queryAutoClientMappings(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/autoclientmappings/query", params, query);
        return getFactory().autoClientMappings(this, response);
    }

    @Override
    public void updateAutoClientMapping(AutoClientMapping autoClientMapping)
    {
        getRemote().put(getResourceUri() + "/autoclientmappings/" + autoClientMapping.getId(), autoClientMapping.getObject());
    }

    @Override
    public void deleteAutoClientMapping(AutoClientMapping autoClientMapping)
    {
        deleteAutoClientMapping(autoClientMapping.getId());
    }

    @Override
    public void deleteAutoClientMapping(String autoClientMappingId)
    {
        getRemote().delete(getResourceUri() + "/autoclientmappings/" + autoClientMappingId);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DEPLOYED APPLICATIONS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<DeployedApplication> listDeployedApplications()
    {
        return listDeployedApplications(null);
    }

    @Override
    public ResultMap<DeployedApplication> listDeployedApplications(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/applications", params);
        return getFactory().deployedApplications(this, response);
    }

    @Override
    public DeployedApplication readDeployedApplication(String deployedApplicationId)
    {
        DeployedApplication deployedApplication = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/applications/" + deployedApplicationId);
            deployedApplication = getFactory().deployedApplication(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return deployedApplication;
    }

    @Override
    public ResultMap<DeployedApplication> queryDeployedApplications(ObjectNode query)
    {
        return queryDeployedApplications(query, null);
    }

    @Override
    public ResultMap<DeployedApplication> queryDeployedApplications(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/applications/query", params, query);
        return getFactory().deployedApplications(this, response);
    }

}
