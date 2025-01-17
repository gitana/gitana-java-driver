/**
 * Copyright 2022 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

import java.util.List;

/**
 * @author uzi
 */
public interface WebHost extends PlatformDataStore
{
    // fields
    public final static String FIELD_AUTOMANAGED_URL_PATTERNS = "urlPatterns";
    public final static String FIELD_DEPLOYER_TYPES = "deployerTypes";

    public final static String FIELD_KEY = "key";

    public String getKey();
    public void setKey(String key);

    public List<String> getAutoManagedUrlPatterns();
    public void setAutoManagedUrlPatterns(List<String> urlPatterns);

    public List<String> getDeployerTypes();
    public void setDeployerTypes(List<String> deployerTypes);
    public boolean hasDeployerType(String deployerType);

    /**
     * @return platform
     */
    public Platform getPlatform();



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // AUTO CLIENT MAPPINGS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<AutoClientMapping> listAutoClientMappings();
    public ResultMap<AutoClientMapping> listAutoClientMappings(Pagination pagination);

    public AutoClientMapping readAutoClientMapping(String autoClientMappingId);

    public AutoClientMapping createAutoClientMapping(String host, String appKey, String applicationId, String clientKey, String authGrantKey);

    public ResultMap<AutoClientMapping> queryAutoClientMappings(ObjectNode query);
    public ResultMap<AutoClientMapping> queryAutoClientMappings(ObjectNode query, Pagination pagination);

    public void updateAutoClientMapping(AutoClientMapping autoClientMapping);

    public void deleteAutoClientMapping(AutoClientMapping autoClientMapping);
    public void deleteAutoClientMapping(String autoClientMappingId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DEPLOYED APPLICATIONS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<DeployedApplication> listDeployedApplications();
    public ResultMap<DeployedApplication> listDeployedApplications(Pagination pagination);

    public DeployedApplication readDeployedApplication(String deployedApplicationId);

    public ResultMap<DeployedApplication> queryDeployedApplications(ObjectNode query);
    public ResultMap<DeployedApplication> queryDeployedApplications(ObjectNode query, Pagination pagination);

}