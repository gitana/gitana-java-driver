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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.webhost.AbstractWarehouseDocumentImpl;
import org.gitana.platform.support.TypedIDConstants;

/**
 * @author uzi
 */
public class InteractionImpl extends AbstractWarehouseDocumentImpl implements Interaction
{
    public InteractionImpl(Warehouse warehouse, ObjectNode obj, boolean isSaved)
    {
        super(warehouse, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_INTERACTION;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getWarehouseId() + "/interactions/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Interaction)
        {
            Interaction other = (Interaction) object;

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
        Interaction interaction = getWarehouse().readInteraction(getId());

        this.reload(interaction.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getInteractionApplicationId()
    {
        return getString(FIELD_INTERACTION_APPLICATION_ID);
    }

    @Override
    public String getInteractionSessionId()
    {
        return getString(FIELD_INTERACTION_SESSION_ID);
    }

    @Override
    public String getInteractionUserId()
    {
        return getString(FIELD_INTERACTION_USER_ID);
    }

    @Override
    public String getInteractionNodeId()
    {
        return getString(FIELD_INTERACTION_NODE_ID);
    }

    @Override
    public String getInteractionPageId()
    {
        return getString(FIELD_INTERACTION_PAGE_ID);
    }

    @Override
    public String getSourceUserAgent()
    {
        return _objectGetString(FIELD_SOURCE, FIELD_SOURCE_USER_AGENT);
    }

    @Override
    public void setSourceUserAgent(String sourceUserAgent)
    {
        _objectSet(FIELD_SOURCE, FIELD_SOURCE_USER_AGENT, sourceUserAgent);
    }

    @Override
    public String getSourceHost()
    {
        return _objectGetString(FIELD_SOURCE, FIELD_SOURCE_HOST);
    }

    @Override
    public void setSourceHost(String sourceHost)
    {
        _objectSet(FIELD_SOURCE, FIELD_SOURCE_HOST, sourceHost);
    }

    @Override
    public String getSourceIP()
    {
        return _objectGetString(FIELD_SOURCE, FIELD_SOURCE_IP);
    }

    @Override
    public void setSourceIP(String sourceIP)
    {
        _objectSet(FIELD_SOURCE, FIELD_SOURCE_IP, sourceIP);
    }

    @Override
    public String getEventType()
    {
        return _objectGetString(FIELD_EVENT, FIELD_EVENT_TYPE);
    }

    @Override
    public void setEventType(String eventType)
    {
        _objectSet(FIELD_EVENT, FIELD_EVENT_TYPE, eventType);
    }

    @Override
    public int getEventX()
    {
        return _objectGetInt(FIELD_EVENT, FIELD_EVENT_X);
    }

    @Override
    public void setEventX(int eventX)
    {
        _objectSet(FIELD_EVENT, FIELD_EVENT_X, eventX);
    }

    @Override
    public int getEventY()
    {
        return _objectGetInt(FIELD_EVENT, FIELD_EVENT_Y);
    }

    @Override
    public void setEventY(int eventY)
    {
        _objectSet(FIELD_EVENT, FIELD_EVENT_Y, eventY);
    }

    @Override
    public int getEventOffsetX()
    {
        return _objectGetInt(FIELD_EVENT, FIELD_EVENT_OFFSET_X);
    }

    @Override
    public void setEventOffsetX(int eventOffsetX)
    {
        _objectSet(FIELD_EVENT, FIELD_EVENT_OFFSET_X, eventOffsetX);
    }

    @Override
    public int getEventOffsetY()
    {
        return _objectGetInt(FIELD_EVENT, FIELD_EVENT_OFFSET_Y);
    }

    @Override
    public void setEventOffsetY(int eventOffsetY)
    {
        _objectSet(FIELD_EVENT, FIELD_EVENT_OFFSET_Y, eventOffsetY);
    }

    @Override
    public String getApplicationHost()
    {
        return _objectGetString(FIELD_APPLICATION, FIELD_APPLICATION_HOST);
    }

    @Override
    public void setApplicationHost(String applicationHost)
    {
        _objectSet(FIELD_APPLICATION, FIELD_APPLICATION_HOST, applicationHost);
    }

    @Override
    public int getApplicationPort()
    {
        return _objectGetInt(FIELD_APPLICATION, FIELD_APPLICATION_PORT);
    }

    @Override
    public void setApplicationPort(int applicationPort)
    {
        _objectSet(FIELD_APPLICATION, FIELD_APPLICATION_PORT, applicationPort);
    }

    @Override
    public String getApplicationProtocol()
    {
        return _objectGetString(FIELD_APPLICATION, FIELD_APPLICATION_PROTOCOL);
    }

    @Override
    public void setApplicationProtocol(String applicationProtocol)
    {
        _objectSet(FIELD_APPLICATION, FIELD_APPLICATION_PROTOCOL, applicationProtocol);
    }

    @Override
    public String getApplicationUrl()
    {
        return _objectGetString(FIELD_APPLICATION, FIELD_APPLICATION_URL);
    }

    @Override
    public void setApplicationUrl(String applicationUrl)
    {
        _objectSet(FIELD_APPLICATION, FIELD_APPLICATION_URL, applicationUrl);
    }

    @Override
    public String getPageFullUri()
    {
        return _objectGetString(FIELD_PAGE, FIELD_PAGE_FULL_URI);
    }

    @Override
    public void setPageFullUri(String pageFullUri)
    {
        _objectSet(FIELD_PAGE, FIELD_PAGE_FULL_URI, pageFullUri);
    }

    @Override
    public String getPageUri()
    {
        return _objectGetString(FIELD_PAGE, FIELD_PAGE_URI);
    }

    @Override
    public void setPageUri(String applicationUri)
    {
        _objectSet(FIELD_PAGE, FIELD_PAGE_URI, applicationUri);
    }

    @Override
    public String getPageHash()
    {
        return _objectGetString(FIELD_PAGE, FIELD_PAGE_HASH);
    }

    @Override
    public void setPageHash(String applicationHash)
    {
        _objectSet(FIELD_PAGE, FIELD_PAGE_HASH, applicationHash);
    }

    @Override
    public String getElementId()
    {
        return _objectGetString(FIELD_ELEMENT, FIELD_ELEMENT_ID);
    }

    @Override
    public void setElementId(String elementId)
    {
        _objectSet(FIELD_ELEMENT, FIELD_ELEMENT_ID, elementId);
    }

    @Override
    public String getElementType()
    {
        return _objectGetString(FIELD_ELEMENT, FIELD_ELEMENT_TYPE);
    }

    @Override
    public void setElementType(String elementType)
    {
        _objectSet(FIELD_ELEMENT, FIELD_ELEMENT_TYPE, elementType);
    }

    @Override
    public String getElementPath()
    {
        return _objectGetString(FIELD_ELEMENT, FIELD_ELEMENT_PATH);
    }

    @Override
    public void setElementPath(String elementPath)
    {
        _objectSet(FIELD_ELEMENT, FIELD_ELEMENT_PATH, elementPath);
    }

    @Override
    public String getNodeRepositoryId()
    {
        return _objectGetString(FIELD_NODE, FIELD_NODE_REPOSITORY_ID);
    }

    @Override
    public void setNodeRepositoryId(String nodeRepositoryId)
    {
        _objectSet(FIELD_NODE, FIELD_NODE_REPOSITORY_ID, nodeRepositoryId);
    }

    @Override
    public String getNodeBranchId()
    {
        return _objectGetString(FIELD_NODE, FIELD_NODE_BRANCH_ID);
    }

    @Override
    public void setNodeBranchId(String nodeBranchId)
    {
        _objectSet(FIELD_NODE, FIELD_NODE_BRANCH_ID, nodeBranchId);
    }

    @Override
    public String getNodeId()
    {
        return _objectGetString(FIELD_NODE, FIELD_NODE_ID);
    }

    @Override
    public void setNodeId(String nodeId)
    {
        _objectSet(FIELD_NODE, FIELD_NODE_ID, nodeId);
    }

    @Override
    public long getTimestampStart()
    {
        return _objectGetLong(FIELD_TIMESTAMP, FIELD_TIMESTAMP_START);
    }

    @Override
    public void setTimestampStart(long timestampStart)
    {
        _objectSet(FIELD_TIMESTAMP, FIELD_TIMESTAMP_START, timestampStart);
    }

    @Override
    public long getTimestampEnd()
    {
        return _objectGetLong(FIELD_TIMESTAMP, FIELD_TIMESTAMP_END);
    }

    @Override
    public void setTimestampEnd(long timestampEnd)
    {
        _objectSet(FIELD_TIMESTAMP, FIELD_TIMESTAMP_END, timestampEnd);
    }

    @Override
    public long getTimestampMs()
    {
        return _objectGetLong(FIELD_TIMESTAMP, FIELD_TIMESTAMP_MS);
    }

    @Override
    public void setTimestampMs(long timestampMs)
    {
        _objectSet(FIELD_TIMESTAMP, FIELD_TIMESTAMP_MS, timestampMs);
    }

    @Override
    public ObjectNode getUser()
    {
        return getObject(FIELD_USER);
    }

    @Override
    public void setUser(ObjectNode userObject)
    {
        set(FIELD_USER, userObject);
    }
}
