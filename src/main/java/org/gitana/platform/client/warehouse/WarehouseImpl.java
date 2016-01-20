/**
 * Copyright 2016 Gitana Software, Inc.
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

import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;

import java.util.Map;

/**
 * @author uzi
 */
public class WarehouseImpl extends AbstractPlatformDataStoreImpl implements Warehouse
{
    public WarehouseImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_WAREHOUSE;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getId();
    }

    @Override
    public void reload()
    {
        Warehouse warehouse = getPlatform().readWarehouse(getId());
        this.reload(warehouse.getObject());
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTIONS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Interaction> listInteractions()
    {
        return listInteractions(null);
    }

    @Override
    public ResultMap<Interaction> listInteractions(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/interactions", params);
        return getFactory().interactions(this, response);
    }

    @Override
    public Interaction readInteraction(String interactionId)
    {
        Interaction interaction = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/interactions/" + interactionId);
            interaction = getFactory().interaction(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return interaction;
    }

    @Override
    public Interaction createInteraction(ObjectNode object)
    {
        Response response = getRemote().post(getResourceUri() + "/interactions", object);

        String interactionId = response.getId();
        return readInteraction(interactionId);
    }

    @Override
    public ResultMap<Interaction> queryInteractions(ObjectNode query)
    {
        return queryInteractions(query, null);
    }

    @Override
    public ResultMap<Interaction> queryInteractions(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/interactions/query", params, query);
        return getFactory().interactions(this, response);
    }

    @Override
    public void updateInteraction(Interaction interaction)
    {
        getRemote().put(getResourceUri() + "/interactions/" + interaction.getId(), interaction.getObject());
    }

    @Override
    public void deleteInteraction(Interaction interaction)
    {
        deleteInteraction(interaction.getId());
    }

    @Override
    public void deleteInteraction(String interactionId)
    {
        getRemote().delete(getResourceUri() + "/interactions/" + interactionId);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION APPLICATIONS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<InteractionApplication> listInteractionApplications()
    {
        return listInteractionApplications(null);
    }

    @Override
    public ResultMap<InteractionApplication> listInteractionApplications(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/applications", params);
        return getFactory().interactionApplications(this, response);
    }

    @Override
    public InteractionApplication readInteractionApplication(String interactionApplicationId)
    {
        InteractionApplication interactionApplication = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/applications/" + interactionApplicationId);
            interactionApplication = getFactory().interactionApplication(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return interactionApplication;
    }

    @Override
    public InteractionApplication createInteractionApplication(ObjectNode object)
    {
        Response response = getRemote().post(getResourceUri() + "/applications", object);

        String interactionApplicationId = response.getId();
        return readInteractionApplication(interactionApplicationId);
    }

    @Override
    public ResultMap<InteractionApplication> queryInteractionApplications(ObjectNode query)
    {
        return queryInteractionApplications(query, null);
    }

    @Override
    public ResultMap<InteractionApplication> queryInteractionApplications(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/applications/query", params, query);
        return getFactory().interactionApplications(this, response);
    }

    @Override
    public void updateInteractionApplication(InteractionApplication interactionApplication)
    {
        getRemote().put(getResourceUri() + "/applications/" + interactionApplication.getId(), interactionApplication.getObject());
    }

    @Override
    public void deleteInteractionApplication(InteractionApplication interactionApplication)
    {
        deleteInteractionApplication(interactionApplication.getId());
    }

    @Override
    public void deleteInteractionApplication(String interactionApplicationId)
    {
        getRemote().delete(getResourceUri() + "/applications/" + interactionApplicationId);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION NODE
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<InteractionNode> listInteractionNodes()
    {
        return listInteractionNodes(null);
    }

    @Override
    public ResultMap<InteractionNode> listInteractionNodes(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/nodes", params);
        return getFactory().interactionNodes(this, response);
    }

    @Override
    public InteractionNode readInteractionNode(String interactionNodeId)
    {
        InteractionNode interactionNode = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/nodes/" + interactionNodeId);
            interactionNode = getFactory().interactionNode(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return interactionNode;
    }

    @Override
    public InteractionNode createInteractionNode(ObjectNode object)
    {
        Response response = getRemote().post(getResourceUri() + "/nodes", object);

        String interactionNodeId = response.getId();
        return readInteractionNode(interactionNodeId);
    }

    @Override
    public ResultMap<InteractionNode> queryInteractionNodes(ObjectNode query)
    {
        return queryInteractionNodes(query, null);
    }

    @Override
    public ResultMap<InteractionNode> queryInteractionNodes(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/nodes/query", params, query);
        return getFactory().interactionNodes(this, response);
    }

    @Override
    public void updateInteractionNode(InteractionNode interactionNode)
    {
        getRemote().put(getResourceUri() + "/nodes/" + interactionNode.getId(), interactionNode.getObject());
    }

    @Override
    public void deleteInteractionNode(InteractionNode interactionNode)
    {
        deleteInteractionNode(interactionNode.getId());
    }

    @Override
    public void deleteInteractionNode(String interactionNodeId)
    {
        getRemote().delete(getResourceUri() + "/nodes/" + interactionNodeId);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION PAGE
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<InteractionPage> listInteractionPages()
    {
        return listInteractionPages(null);
    }

    @Override
    public ResultMap<InteractionPage> listInteractionPages(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/pages", params);
        return getFactory().interactionPages(this, response);
    }

    @Override
    public InteractionPage readInteractionPage(String interactionReportId)
    {
        InteractionPage interactionPage = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/pages/" + interactionReportId);
            interactionPage = getFactory().interactionPage(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return interactionPage;
    }

    @Override
    public InteractionPage createInteractionPage(ObjectNode object)
    {
        Response response = getRemote().post(getResourceUri() + "/pages", object);

        String interactionPageId = response.getId();
        return readInteractionPage(interactionPageId);
    }

    @Override
    public ResultMap<InteractionPage> queryInteractionPages(ObjectNode query)
    {
        return queryInteractionPages(query, null);
    }

    @Override
    public ResultMap<InteractionPage> queryInteractionPages(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/pages/query", params, query);
        return getFactory().interactionPages(this, response);
    }

    @Override
    public void updateInteractionPage(InteractionPage interactionPage)
    {
        getRemote().put(getResourceUri() + "/pages/" + interactionPage.getId(), interactionPage.getObject());
    }

    @Override
    public void deleteInteractionPage(InteractionPage interactionPage)
    {
        deleteInteractionPage(interactionPage.getId());
    }

    @Override
    public void deleteInteractionPage(String interactionPageId)
    {
        getRemote().delete(getResourceUri() + "/pages/" + interactionPageId);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION REPORT
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<InteractionReport> listInteractionReports()
    {
        return listInteractionReports(null);
    }

    @Override
    public ResultMap<InteractionReport> listInteractionReports(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/reports", params);
        return getFactory().interactionReports(this, response);
    }

    @Override
    public InteractionReport readInteractionReport(String interactionReportId)
    {
        InteractionReport interactionReport = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/reports/" + interactionReportId);
            interactionReport = getFactory().interactionReport(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return interactionReport;
    }

    @Override
    public InteractionReport createInteractionReport(ObjectNode object)
    {
        Response response = getRemote().post(getResourceUri() + "/reports", object);

        String interactionReportId = response.getId();
        return readInteractionReport(interactionReportId);
    }

    @Override
    public ResultMap<InteractionReport> queryInteractionReports(ObjectNode query)
    {
        return queryInteractionReports(query, null);
    }

    @Override
    public ResultMap<InteractionReport> queryInteractionReports(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/reports/query", params, query);
        return getFactory().interactionReports(this, response);
    }

    @Override
    public void updateInteractionReport(InteractionReport interactionReport)
    {
        getRemote().put(getResourceUri() + "/reports/" + interactionReport.getId(), interactionReport.getObject());
    }

    @Override
    public void deleteInteractionReport(InteractionReport interactionReport)
    {
        deleteInteractionPage(interactionReport.getId());
    }

    @Override
    public void deleteInteractionReport(String interactionReportId)
    {
        getRemote().delete(getResourceUri() + "/reports/" + interactionReportId);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION SESSION
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<InteractionSession> listInteractionSessions()
    {
        return listInteractionSessions(null);
    }

    @Override
    public ResultMap<InteractionSession> listInteractionSessions(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/sessions", params);
        return getFactory().interactionSessions(this, response);
    }

    @Override
    public InteractionSession readInteractionSession(String interactionSessionId)
    {
        InteractionSession interactionSession = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/sessions/" + interactionSessionId);
            interactionSession = getFactory().interactionSession(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return interactionSession;
    }

    @Override
    public InteractionSession createInteractionSession(ObjectNode object)
    {
        Response response = getRemote().post(getResourceUri() + "/sessions", object);

        String interactionSessionId = response.getId();
        return readInteractionSession(interactionSessionId);
    }

    @Override
    public ResultMap<InteractionSession> queryInteractionSessions(ObjectNode query)
    {
        return queryInteractionSessions(query, null);
    }

    @Override
    public ResultMap<InteractionSession> queryInteractionSessions(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/sessions/query", params, query);
        return getFactory().interactionSessions(this, response);
    }

    @Override
    public void updateInteractionSession(InteractionSession interactionSession)
    {
        getRemote().put(getResourceUri() + "/sessions/" + interactionSession.getId(), interactionSession.getObject());
    }

    @Override
    public void deleteInteractionSession(InteractionSession interactionSession)
    {
        deleteInteractionSession(interactionSession.getId());
    }

    @Override
    public void deleteInteractionSession(String interactionSessionId)
    {
        getRemote().delete(getResourceUri() + "/sessions/" + interactionSessionId);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION USER
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<InteractionUser> listInteractionUsers()
    {
        return listInteractionUsers(null);
    }

    @Override
    public ResultMap<InteractionUser> listInteractionUsers(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/users", params);
        return getFactory().interactionUsers(this, response);
    }

    @Override
    public InteractionUser readInteractionUser(String interactionUserId)
    {
        InteractionUser interactionUser = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/users/" + interactionUserId);
            interactionUser = getFactory().interactionUser(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return interactionUser;
    }

    @Override
    public InteractionUser createInteractionUser(ObjectNode object)
    {
        Response response = getRemote().post(getResourceUri() + "/users", object);

        String interactionUserId = response.getId();
        return readInteractionUser(interactionUserId);
    }

    @Override
    public ResultMap<InteractionUser> queryInteractionUsers(ObjectNode query)
    {
        return queryInteractionUsers(query, null);
    }

    @Override
    public ResultMap<InteractionUser> queryInteractionUsers(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/users/query", params, query);
        return getFactory().interactionUsers(this, response);
    }

    @Override
    public void updateInteractionUser(InteractionUser interactionUser)
    {
        getRemote().put(getResourceUri() + "/users/" + interactionUser.getId(), interactionUser.getObject());
    }

    @Override
    public void deleteInteractionUser(InteractionUser interactionUser)
    {
        deleteInteractionUser(interactionUser.getId());
    }

    @Override
    public void deleteInteractionUser(String interactionUserId)
    {
        getRemote().delete(getResourceUri() + "/users/" + interactionUserId);
    }
}
