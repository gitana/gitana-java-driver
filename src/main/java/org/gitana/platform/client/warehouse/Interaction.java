/**
 * Copyright 2013 Gitana Software, Inc.
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

import org.gitana.platform.client.support.Selfable;

/**
 * @author uzi
 */
public interface Interaction extends WarehouseDocument, Selfable
{
    // these references get interpreted and set automatically
    public final static String FIELD_INTERACTION_APPLICATION_ID = "interactionApplicationId";
    public final static String FIELD_INTERACTION_SESSION_ID = "interactionSessionId";
    public final static String FIELD_INTERACTION_USER_ID = "interactionUserId";
    public final static String FIELD_INTERACTION_NODE_ID = "interactionNodeId";
    public final static String FIELD_INTERACTION_PAGE_ID = "interactionPageId";

    public String getInteractionApplicationId();
    public String getInteractionSessionId();
    public String getInteractionUserId();
    public String getInteractionNodeId();
    public String getInteractionPageId();

    //
    // stuff that is generated and filled in by the insight JS driver
    // it gets sent to us for us to resolve on the server side
    //
    public final static String FIELD_APPLICATION_KEY = "applicationKey";
    public final static String FIELD_SESSION_KEY = "sessionKey";
    public final static String FIELD_USER_KEY = "userKey";
    public final static String FIELD_NODE = "node";
    public final static String FIELD_NODE_REPOSITORY_ID = "repositoryId";
    public final static String FIELD_NODE_BRANCH_ID = "branchId";
    public final static String FIELD_NODE_ID = "id";
    public final static String FIELD_USER = "user";

    public String getNodeRepositoryId();
    public void setNodeRepositoryId(String nodeRepositoryId);

    public String getNodeBranchId();
    public void setNodeBranchId(String nodeBranchId);

    public String getNodeId();
    public void setNodeId(String nodeId);

    public ObjectNode getUser();
    public void setUser(ObjectNode userObject);



    //
    // EVENT DETAILS
    // TO BE PROVIDED BY BROWSER EVENT
    //

    public final static String FIELD_SOURCE = "source";
    public final static String FIELD_SOURCE_USER_AGENT = "user-agent";
    public final static String FIELD_SOURCE_HOST = "host";
    public final static String FIELD_SOURCE_IP = "ip";

    public final static String FIELD_EVENT = "event";
    public final static String FIELD_EVENT_TYPE = "type";
    public final static String FIELD_EVENT_X = "x";
    public final static String FIELD_EVENT_Y = "y";
    public final static String FIELD_EVENT_OFFSET_X = "offsetX";
    public final static String FIELD_EVENT_OFFSET_Y = "offsetY";

    public final static String FIELD_APPLICATION = "application";
    public final static String FIELD_APPLICATION_HOST = "host";
    public final static String FIELD_APPLICATION_PORT = "port";
    public final static String FIELD_APPLICATION_PROTOCOL = "protocol";
    public final static String FIELD_APPLICATION_URL = "url";

    public final static String FIELD_PAGE = "page";
    public final static String FIELD_PAGE_URI = "uri";
    public final static String FIELD_PAGE_HASH = "hash";
    public final static String FIELD_PAGE_FULL_URI = "fullUri";

    public final static String FIELD_TIMESTAMP = "timestamp";
    public final static String FIELD_TIMESTAMP_START = "start";
    public final static String FIELD_TIMESTAMP_END = "end";
    public final static String FIELD_TIMESTAMP_MS = "ms";

    public final static String FIELD_ELEMENT = "element";
    public final static String FIELD_ELEMENT_ID = "id";
    public final static String FIELD_ELEMENT_TYPE = "type";
    public final static String FIELD_ELEMENT_PATH = "path";

    public String getSourceUserAgent();
    public void setSourceUserAgent(String sourceUserAgent);

    public String getSourceHost();
    public void setSourceHost(String sourceHost);

    public String getSourceIP();
    public void setSourceIP(String sourceIP);

    public String getEventType();
    public void setEventType(String eventType);

    public int getEventX();
    public void setEventX(int eventX);

    public int getEventY();
    public void setEventY(int eventY);

    public int getEventOffsetX();
    public void setEventOffsetX(int eventOffsetX);

    public int getEventOffsetY();
    public void setEventOffsetY(int eventOffsetY);

    public String getApplicationHost();
    public void setApplicationHost(String applicationHost);

    public int getApplicationPort();
    public void setApplicationPort(int applicationPort);

    public String getApplicationProtocol();
    public void setApplicationProtocol(String applicationProtocol);

    public String getApplicationUrl();
    public void setApplicationUrl(String applicationUrl);

    public String getPageUri();
    public void setPageUri(String pageUri);

    public String getPageHash();
    public void setPageHash(String pageHash);

    public String getPageFullUri();
    public void setPageFullUri(String fullUri);

    public String getElementId();
    public void setElementId(String elementId);

    public String getElementType();
    public void setElementType(String elementType);

    public String getElementPath();
    public void setElementPath(String elementPath);

    public long getTimestampStart();
    public void setTimestampStart(long timestampStart);

    public long getTimestampEnd();
    public void setTimestampEnd(long timestampEnd);

    public long getTimestampMs();
    public void setTimestampMs(long timestampMs);
}
