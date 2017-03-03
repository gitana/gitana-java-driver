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

package org.gitana.platform.client.warehouse;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.webhost.AbstractWarehouseDocumentImpl;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

/**
 * @author uzi
 */
public class InteractionReportEntryImpl extends AbstractWarehouseDocumentImpl implements InteractionReportEntry
{
    public InteractionReportEntryImpl(Warehouse warehouse, ObjectNode obj, boolean isSaved)
    {
        super(warehouse, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_INTERACTION_REPORT_ENTRY;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getWarehouseId() + "/reports/" + getReportId() + "/entries/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof InteractionReportEntry)
        {
            InteractionReportEntry other = (InteractionReportEntry) object;

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
        InteractionReportEntry entry = getWarehouse().readInteractionReport(getReportId()).readInteractionReportEntry(getId());

        this.reload(entry.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getReportId()
    {
        return getString(FIELD_REPORT_ID);
    }

    @Override
    public void setReportId(String reportId)
    {
        set(FIELD_REPORT_ID, reportId);
    }

    @Override
    public String getKey()
    {
        return getString(FIELD_KEY);
    }

    @Override
    public void setKey(String key)
    {
        set(FIELD_KEY, key);
    }

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
    public String getApplicationTitle()
    {
        return getString(FIELD_APPLICATION_ID);
    }

    @Override
    public void setApplicationTitle(String applicationTitle)
    {
        set(FIELD_APPLICATION_TITLE, applicationTitle);
    }

    @Override
    public String getApplicationDescription()
    {
        return getString(FIELD_APPLICATION_DESCRIPTION);
    }

    @Override
    public void setApplicationDescription(String applicationDescription)
    {
        set(FIELD_APPLICATION_DESCRIPTION, applicationDescription);
    }

    @Override
    public String getSessionId()
    {
        return getString(FIELD_SESSION_ID);
    }

    @Override
    public void setSessionId(String sessionId)
    {
        set(FIELD_SESSION_ID, sessionId);
    }

    @Override
    public String getSessionTitle()
    {
        return getString(FIELD_SESSION_TITLE);
    }

    @Override
    public void setSessionTitle(String sessionTitle)
    {
        set(FIELD_SESSION_TITLE, sessionTitle);
    }

    @Override
    public String getSessionDescription()
    {
        return getString(FIELD_SESSION_DESCRIPTION);
    }

    @Override
    public void setSessionDescription(String sessionDescription)
    {
        set(FIELD_SESSION_DESCRIPTION, sessionDescription);
    }

    @Override
    public String getPageId()
    {
        return getString(FIELD_PAGE_ID);
    }

    @Override
    public void setPageId(String pageId)
    {
        set(FIELD_PAGE_ID, pageId);
    }

    @Override
    public String getPageUri()
    {
        return getString(FIELD_PAGE_URI);
    }

    @Override
    public void setPageUri(String pageUri)
    {
        set(FIELD_PAGE_URI, pageUri);
    }

    @Override
    public String getPageTitle()
    {
        return getString(FIELD_PAGE_TITLE);
    }

    @Override
    public void setPageTitle(String pageTitle)
    {
        set(FIELD_PAGE_TITLE, pageTitle);
    }

    @Override
    public String getPageDescription()
    {
        return getString(FIELD_PAGE_DESCRIPTION);
    }

    @Override
    public void setPageDescription(String pageDescription)
    {
        set(FIELD_PAGE_DESCRIPTION, pageDescription);
    }

    @Override
    public String getNodeId()
    {
        return getString(FIELD_NODE_ID);
    }

    @Override
    public void setNodeId(String nodeId)
    {
        set(FIELD_NODE_ID, nodeId);
    }

    @Override
    public String getNodeTargetRepositoryId()
    {
        return getString(FIELD_NODE_TARGET_REPOSITORY_ID);
    }

    @Override
    public void setNodeTargetRepositoryId(String nodeTargetRepositoryId)
    {
        set(FIELD_NODE_TARGET_REPOSITORY_ID, nodeTargetRepositoryId);
    }

    @Override
    public String getNodeTargetBranchId()
    {
        return getString(FIELD_NODE_TARGET_BRANCH_ID);
    }

    @Override
    public void setNodeTargetBranchId(String nodeBranchId)
    {
        set(FIELD_NODE_TARGET_BRANCH_ID, nodeBranchId);
    }

    @Override
    public String getNodeTargetId()
    {
        return getString(FIELD_NODE_TARGET_ID);
    }

    @Override
    public void setNodeTargetId(String nodeTargetId)
    {
        set(FIELD_NODE_TARGET_ID, nodeTargetId);
    }

    @Override
    public String getNodeTitle()
    {
        return getString(FIELD_NODE_TITLE);
    }

    @Override
    public void setNodeTitle(String nodeTitle)
    {
        set(FIELD_NODE_TITLE, nodeTitle);
    }

    @Override
    public String getNodeDescription()
    {
        return getString(FIELD_NODE_DESCRIPTION);
    }

    @Override
    public void setNodeDescription(String nodeDescription)
    {
        set(FIELD_NODE_DESCRIPTION, nodeDescription);
    }

    @Override
    public String getUserId()
    {
        return getString(FIELD_USER_ID);
    }

    @Override
    public void setUserId(String userId)
    {
        set(FIELD_USER_ID, userId);
    }

    @Override
    public String getUserTitle()
    {
        return getString(FIELD_USER_TITLE);
    }

    @Override
    public void setUserTitle(String userTitle)
    {
        set(FIELD_USER_TITLE, userTitle);
    }

    @Override
    public String getUserDescription()
    {
        return getString(FIELD_USER_DESCRIPTION);
    }

    @Override
    public void setUserDescription(String userDescription)
    {
        set(FIELD_USER_DESCRIPTION, userDescription);
    }

    @Override
    public String getUserFirstName()
    {
        return getString(FIELD_USER_FIRST_NAME);
    }

    @Override
    public void setUserFirstName(String userFirstName)
    {
        set(FIELD_USER_FIRST_NAME, userFirstName);

    }

    @Override
    public String getUserLastName()
    {
        return getString(FIELD_USER_LAST_NAME);
    }

    @Override
    public void setUserLastName(String userLastName)
    {
        set(FIELD_USER_LAST_NAME, userLastName);
    }

    @Override
    public String getUserEmail()
    {
        return getString(FIELD_USER_EMAIL);
    }

    @Override
    public void setUserEmail(String userEmail)
    {
        set(FIELD_USER_EMAIL, userEmail);
    }

    @Override
    public String getUserName()
    {
        return getString(FIELD_USER_NAME);
    }

    @Override
    public void setUserName(String userName)
    {
        set(FIELD_USER_NAME, userName);
    }

    @Override
    public ObjectNode getStatistic(String name)
    {
        ObjectNode statistic = null;

        if (has(FIELD_STATISTICS))
        {
            ObjectNode statistics = getObject(FIELD_STATISTICS);

            statistic = JsonUtil.objectGetObject(statistics, name);
        }

        return statistic;
    }

    @Override
    public void setStatistic(String name, ObjectNode statistic)
    {
        ObjectNode statistics = getObject(FIELD_STATISTICS);
        if (statistics == null)
        {
            statistics = JsonUtil.createObject();
            set(FIELD_STATISTICS, statistics);
        }

        statistics.put(name, statistic);
    }}
