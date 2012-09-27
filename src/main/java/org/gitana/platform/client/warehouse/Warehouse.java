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
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Warehouse extends PlatformDataStore
{
    /**
     * @return platform
     */
    public Platform getPlatform();



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Interaction> listInteractions();
    public ResultMap<Interaction> listInteractions(Pagination pagination);

    public Interaction readInteraction(String interactionId);

    public Interaction createInteraction(ObjectNode object);

    public ResultMap<Interaction> queryInteractions(ObjectNode query);
    public ResultMap<Interaction> queryInteractions(ObjectNode query, Pagination pagination);

    public void updateInteraction(Interaction interaction);

    public void deleteInteraction(Interaction interaction);
    public void deleteInteraction(String interactionId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION APPLICATION
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<InteractionApplication> listInteractionApplications();
    public ResultMap<InteractionApplication> listInteractionApplications(Pagination pagination);

    public InteractionApplication readInteractionApplication(String interactionApplicationId);

    public InteractionApplication createInteractionApplication(ObjectNode object);

    public ResultMap<InteractionApplication> queryInteractionApplications(ObjectNode query);
    public ResultMap<InteractionApplication> queryInteractionApplications(ObjectNode query, Pagination pagination);

    public void updateInteractionApplication(InteractionApplication interactionApplication);

    public void deleteInteractionApplication(InteractionApplication interactionApplication);
    public void deleteInteractionApplication(String interactionApplicationId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION NODE
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<InteractionNode> listInteractionNodes();
    public ResultMap<InteractionNode> listInteractionNodes(Pagination pagination);

    public InteractionNode readInteractionNode(String autoClientMappingId);

    public InteractionNode createInteractionNode(ObjectNode object);

    public ResultMap<InteractionNode> queryInteractionNodes(ObjectNode query);
    public ResultMap<InteractionNode> queryInteractionNodes(ObjectNode query, Pagination pagination);

    public void updateInteractionNode(InteractionNode interactionNode);

    public void deleteInteractionNode(InteractionNode interactionNode);
    public void deleteInteractionNode(String interactionNodeId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION PAGE
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<InteractionPage> listInteractionPages();
    public ResultMap<InteractionPage> listInteractionPages(Pagination pagination);

    public InteractionPage readInteractionPage(String interactionPageId);

    public InteractionPage createInteractionPage(ObjectNode object);

    public ResultMap<InteractionPage> queryInteractionPages(ObjectNode query);
    public ResultMap<InteractionPage> queryInteractionPages(ObjectNode query, Pagination pagination);

    public void updateInteractionPage(InteractionPage interactionPage);

    public void deleteInteractionPage(InteractionPage interactionPage);
    public void deleteInteractionPage(String interactionPageId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION REPORT
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<InteractionReport> listInteractionReports();
    public ResultMap<InteractionReport> listInteractionReports(Pagination pagination);

    public InteractionReport readInteractionReport(String interactionReportId);

    public InteractionReport createInteractionReport(ObjectNode object);

    public ResultMap<InteractionReport> queryInteractionReports(ObjectNode query);
    public ResultMap<InteractionReport> queryInteractionReports(ObjectNode query, Pagination pagination);

    public void updateInteractionReport(InteractionReport interactionReport);

    public void deleteInteractionReport(InteractionReport interactionReport);
    public void deleteInteractionReport(String interactionReportId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION SESSION
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<InteractionSession> listInteractionSessions();
    public ResultMap<InteractionSession> listInteractionSessions(Pagination pagination);

    public InteractionSession readInteractionSession(String interactionSessionId);

    public InteractionSession createInteractionSession(ObjectNode object);

    public ResultMap<InteractionSession> queryInteractionSessions(ObjectNode query);
    public ResultMap<InteractionSession> queryInteractionSessions(ObjectNode query, Pagination pagination);

    public void updateInteractionSession(InteractionSession interactionSession);

    public void deleteInteractionSession(InteractionSession interactionSession);
    public void deleteInteractionSession(String interactionSessionId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION USER
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<InteractionUser> listInteractionUsers();
    public ResultMap<InteractionUser> listInteractionUsers(Pagination pagination);

    public InteractionUser readInteractionUser(String interactionUserId);

    public InteractionUser createInteractionUser(ObjectNode object);

    public ResultMap<InteractionUser> queryInteractionUsers(ObjectNode query);
    public ResultMap<InteractionUser> queryInteractionUsers(ObjectNode query, Pagination pagination);

    public void updateInteractionUser(InteractionUser interactionUser);

    public void deleteInteractionUser(InteractionUser interactionUser);
    public void deleteInteractionUser(String interactionUserId);

}