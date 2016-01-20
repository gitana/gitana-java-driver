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

import org.gitana.platform.client.webhost.AbstractWarehouseDocumentImpl;
import org.gitana.platform.support.TypedIDConstants;

/**
 * @author uzi
 */
public class InteractionApplicationImpl extends AbstractWarehouseDocumentImpl implements InteractionApplication
{
    public InteractionApplicationImpl(Warehouse warehouse, ObjectNode obj, boolean isSaved)
    {
        super(warehouse, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_INTERACTION_APPLICATION;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getWarehouseId() + "/applications/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof InteractionApplication)
        {
            InteractionApplication other = (InteractionApplication) object;

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
        InteractionApplication interactionApplication = getWarehouse().readInteractionApplication(getId());

        this.reload(interactionApplication.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    public String getKey()
    {
        return getString(FIELD_KEY);
    }

    @Override
    public void setKey(String applicationKey)
    {
        set(FIELD_KEY, applicationKey);
    }

    @Override
    public String getUrl()
    {
        return getString(FIELD_URL);
    }

    @Override
    public void setUrl(String url)
    {
        set(FIELD_URL, url);
    }

    @Override
    public String getHost()
    {
        return getString(FIELD_HOST);
    }

    @Override
    public void setHost(String applicationHost)
    {
        set(FIELD_HOST, applicationHost);
    }

    @Override
    public int getPort()
    {
        return getInt(FIELD_PORT);
    }

    @Override
    public void setPort(int port)
    {
        set(FIELD_PORT, port);
    }

    @Override
    public String getProtocol()
    {
        return getString(FIELD_PROTOCOL);
    }

    @Override
    public void setProtocol(String protocol)
    {
        set(FIELD_PROTOCOL, protocol);
    }
}
