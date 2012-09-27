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
import org.gitana.platform.client.webhost.AbstractWarehouseDocumentImpl;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.Iterator;
import java.util.List;

/**
 * @author uzi
 */
public class InteractionPageImpl extends AbstractWarehouseDocumentImpl implements InteractionPage
{
    public InteractionPageImpl(Warehouse warehouse, ObjectNode obj, boolean isSaved)
    {
        super(warehouse, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_INTERACTION_PAGE;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getWarehouseId() + "/pages/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof InteractionPage)
        {
            InteractionPage other = (InteractionPage) object;

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
        InteractionPage interactionPage = getWarehouse().readInteractionPage(getId());

        this.reload(interactionPage.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    // ACCESSORS

    @Override
    public String getUri()
    {
        return getString(FIELD_URI);
    }

    @Override
    public void setUri(String uri)
    {
        set(FIELD_URI, uri);
    }

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
    public int getSnapshotWidth()
    {
        return _objectGetInt(FIELD_SNAPSHOT, FIELD_SNAPSHOT_WIDTH);
    }

    @Override
    public void setSnapshotWidth(int snapshotWidth)
    {
        _objectSet(FIELD_SNAPSHOT, FIELD_SNAPSHOT_WIDTH, snapshotWidth);
    }

    @Override
    public int getSnapshotHeight()
    {
        return _objectGetInt(FIELD_SNAPSHOT, FIELD_SNAPSHOT_HEIGHT);
    }

    @Override
    public void setSnapshotHeight(int snapshotHeight)
    {
        _objectSet(FIELD_SNAPSHOT, FIELD_SNAPSHOT_HEIGHT, snapshotHeight);
    }

    @Override
    public List<String> getElementIIDs()
    {
        List<String> iids = null;

        ObjectNode elements = getObject(FIELD_ELEMENTS);
        if (elements != null)
        {
            Iterator<String> fieldNames = elements.getFieldNames();
            while (fieldNames.hasNext())
            {
                iids.add(fieldNames.next());
            }
        }

        return iids;
    }

    @Override
    public void addElement(String iid, String type, int x, int y, int width, int height)
    {
        ObjectNode element = JsonUtil.createObject();

        element.put(FIELD_ELEMENT_TYPE, type);
        element.put(FIELD_ELEMENT_X, x);
        element.put(FIELD_ELEMENT_Y, y);
        element.put(FIELD_ELEMENT_WIDTH, width);
        element.put(FIELD_ELEMENT_HEIGHT, height);

        ObjectNode elements = getObject(FIELD_ELEMENTS);
        if (elements == null)
        {
            elements = JsonUtil.createObject();
            set(FIELD_ELEMENTS, elements);
        }

        elements.put(iid, element);
    }
}
