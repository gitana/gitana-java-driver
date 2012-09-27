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

package org.gitana.platform.client.warehouse;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.support.Selfable;

/**
 * @author uzi
 */
public interface InteractionReportEntry extends WarehouseDocument, Selfable
{
    // key fields
    public final static String FIELD_REPORT_ID = "reportId";
    public final static String FIELD_KEY = "key";

    public String getReportId();
    public void setReportId(String reportId);

    public String getKey();
    public void setKey(String key);

    // fields

    // application
    public final static String FIELD_APPLICATION_ID = "applicationId";
    public final static String FIELD_APPLICATION_TITLE = "applicationTitle";
    public final static String FIELD_APPLICATION_DESCRIPTION = "applicationDescription";

    // interaction session
    public final static String FIELD_SESSION_ID = "sessionId";
    public final static String FIELD_SESSION_TITLE = "sessionTitle";
    public final static String FIELD_SESSION_DESCRIPTION = "sessionDescription";

    // interaction page
    public final static String FIELD_PAGE_ID = "pageId";
    public final static String FIELD_PAGE_URI = "pageUri";
    public final static String FIELD_PAGE_TITLE = "pageTitle";
    public final static String FIELD_PAGE_DESCRIPTION = "pageDescription";

    // interaction node
    public final static String FIELD_NODE_ID = "nodeId";
    public final static String FIELD_NODE_TARGET_REPOSITORY_ID = "nodeTargetRepositoryId";
    public final static String FIELD_NODE_TARGET_BRANCH_ID = "nodeTargetBranchId";
    public final static String FIELD_NODE_TARGET_ID = "nodeTargetId";
    public final static String FIELD_NODE_TITLE = "nodeTitle";
    public final static String FIELD_NODE_DESCRIPTION = "nodeDescription";

    // interaction user
    public final static String FIELD_USER_ID = "userId";
    public final static String FIELD_USER_TITLE = "userTitle";
    public final static String FIELD_USER_DESCRIPTION = "userDescription";
    public final static String FIELD_USER_FIRST_NAME = "userFirstName";
    public final static String FIELD_USER_LAST_NAME = "userLastName";
    public final static String FIELD_USER_EMAIL = "userEmail";
    public final static String FIELD_USER_NAME = "userName";


    public String getApplicationId();
    public void setApplicationId(String applicationId);

    public String getApplicationTitle();
    public void setApplicationTitle(String applicationTitle);

    public String getApplicationDescription();
    public void setApplicationDescription(String applicationDescription);

    public String getSessionId();
    public void setSessionId(String sessionId);

    public String getSessionTitle();
    public void setSessionTitle(String sessionTitle);

    public String getSessionDescription();
    public void setSessionDescription(String sessionDescription);

    public String getPageId();
    public void setPageId(String pageId);

    public String getPageUri();
    public void setPageUri(String pageUri);

    public String getPageTitle();
    public void setPageTitle(String pageTitle);

    public String getPageDescription();
    public void setPageDescription(String pageDescription);

    public String getNodeId();
    public void setNodeId(String nodeId);

    public String getNodeTargetRepositoryId();
    public void setNodeTargetRepositoryId(String nodeTargetRepositoryId);

    public String getNodeTargetBranchId();
    public void setNodeTargetBranchId(String nodeTargetBranchId);

    public String getNodeTargetId();
    public void setNodeTargetId(String nodeTargetId);

    public String getNodeTitle();
    public void setNodeTitle(String nodeTitle);

    public String getNodeDescription();
    public void setNodeDescription(String nodeDescription);

    public String getUserId();
    public void setUserId(String userId);

    public String getUserTitle();
    public void setUserTitle(String userTitle);

    public String getUserDescription();
    public void setUserDescription(String userDescription);

    public String getUserFirstName();
    public void setUserFirstName(String userFirstName);

    public String getUserLastName();
    public void setUserLastName(String userLastName);

    public String getUserEmail();
    public void setUserEmail(String userEmail);

    public String getUserName();
    public void setUserName(String userName);


    // general purpose stats object
    public final static String FIELD_STATISTICS = "statistics";
    public final static String FIELD_STATISTIC_SUM = "sum";
    public final static String FIELD_STATISTIC_VALUE = "value";
    public final static String FIELD_STATISTIC_COUNT = "count";

    public ObjectNode getStatistic(String name);
    public void setStatistic(String name, ObjectNode statistic);
}
