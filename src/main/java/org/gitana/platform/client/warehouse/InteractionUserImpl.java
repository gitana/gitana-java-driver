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

/**
 * @author uzi
 */
public class InteractionUserImpl extends AbstractWarehouseDocumentImpl implements InteractionUser
{
    public InteractionUserImpl(Warehouse warehouse, ObjectNode obj, boolean isSaved)
    {
        super(warehouse, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_INTERACTION_USER;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getWarehouseId() + "/users/" + getId();
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
        InteractionUser interactionUser = getWarehouse().readInteractionUser(getId());

        this.reload(interactionUser.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    public String getName()
    {
        return getString(FIELD_NAME);
    }

    @Override
    public void setName(String name)
    {
        set(FIELD_NAME, name);
    }
}
