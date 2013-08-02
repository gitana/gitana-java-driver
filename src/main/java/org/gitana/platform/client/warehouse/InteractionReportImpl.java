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

import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.client.webhost.AbstractWarehouseDocumentImpl;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;

import java.util.Map;

/**
 * @author uzi
 */
public class InteractionReportImpl extends AbstractWarehouseDocumentImpl implements InteractionReport
{
    public InteractionReportImpl(Warehouse warehouse, ObjectNode obj, boolean isSaved)
    {
        super(warehouse, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_INTERACTION_REPORT;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getWarehouseId() + "/reports/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof InteractionReport)
        {
            InteractionReport other = (InteractionReport) object;

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
        InteractionReport interactionReport = getWarehouse().readInteractionReport(getId());

        this.reload(interactionReport.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getObjectTypeId()
    {
        return getString(FIELD_OBJECT_TYPE_ID);
    }

    @Override
    public void setObjectTypeId(String objectTypeId)
    {
        set(FIELD_OBJECT_TYPE_ID, objectTypeId);
    }

    @Override
    public String getObjectId()
    {
        return getString(FIELD_OBJECT_ID);
    }

    @Override
    public void setObjectId(String objectId)
    {
        set(FIELD_OBJECT_ID, objectId);
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION REPORT ENTRIES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION REPORT
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<InteractionReportEntry> listInteractionReportEntries()
    {
        return listInteractionReportEntries(null);
    }

    @Override
    public ResultMap<InteractionReportEntry> listInteractionReportEntries(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/entries", params);
        return getFactory().interactionReportEntries(this.getWarehouse(), response);
    }

    @Override
    public InteractionReportEntry readInteractionReportEntry(String interactionReportEntryId)
    {
        InteractionReportEntry interactionReportEntry = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/entries/" + interactionReportEntryId);
            interactionReportEntry = getFactory().interactionReportEntry(this.getWarehouse(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return interactionReportEntry;
    }

    @Override
    public InteractionReportEntry createInteractionReportEntry(ObjectNode object)
    {
        Response response = getRemote().post(getResourceUri() + "/entries", object);

        String interactionReportEntryId = response.getId();
        return readInteractionReportEntry(interactionReportEntryId);
    }

    @Override
    public ResultMap<InteractionReportEntry> queryInteractionReportEntries(ObjectNode query)
    {
        return queryInteractionReportEntries(query, null);
    }

    @Override
    public ResultMap<InteractionReportEntry> queryInteractionReportEntries(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/entries/query", params, query);
        return getFactory().interactionReportEntries(this.getWarehouse(), response);
    }

    @Override
    public void updateInteractionReportEntry(InteractionReportEntry interactionReportEntry)
    {
        getRemote().put(getResourceUri() + "/entries/" + interactionReportEntry.getId(), interactionReportEntry.getObject());
    }

    @Override
    public void deleteInteractionReportEntry(InteractionReportEntry interactionReportEntry)
    {
        deleteInteractionReportEntry(interactionReportEntry.getId());
    }

    @Override
    public void deleteInteractionReportEntry(String interactionReportEntryId)
    {
        getRemote().delete(getResourceUri() + "/entries/" + interactionReportEntryId);
    }
}
