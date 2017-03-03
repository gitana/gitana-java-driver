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

/**
 * @author uzi
 */
public class InteractionSessionImpl extends AbstractWarehouseDocumentImpl implements InteractionSession
{
    public InteractionSessionImpl(Warehouse warehouse, ObjectNode obj, boolean isSaved)
    {
        super(warehouse, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_INTERACTION_SESSION;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getWarehouseId() + "/sessions/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof InteractionSession)
        {
            InteractionSession other = (InteractionSession) object;

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
        InteractionSession interactionSession = getWarehouse().readInteractionSession(getId());

        this.reload(interactionSession.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORIES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getInteractionApplicationId()
    {
        return getString(FIELD_INTERACTION_APPLICATION_ID);
    }

    @Override
    public void setInteractionApplicationId(String interactionApplicationId)
    {
        set(FIELD_INTERACTION_APPLICATION_ID, interactionApplicationId);
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
}
