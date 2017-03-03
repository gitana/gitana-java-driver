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

import java.util.Iterator;
import java.util.List;

/**
 * @author uzi
 */
public class InteractionNodeImpl extends AbstractWarehouseDocumentImpl implements InteractionNode
{
    public InteractionNodeImpl(Warehouse warehouse, ObjectNode obj, boolean isSaved)
    {
        super(warehouse, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_INTERACTION_NODE;
    }

    @Override
    public String getResourceUri()
    {
        return "/warehouses/" + getWarehouseId() + "/nodes/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof InteractionNode)
        {
            InteractionNode other = (InteractionNode) object;

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
        InteractionNode interactionNode = getWarehouse().readInteractionNode(getId());

        this.reload(interactionNode.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getRepositoryId()
    {
        return getString(FIELD_REPOSITORY_ID);
    }

    @Override
    public void setRepositoryId(String repositoryId)
    {
        set(FIELD_REPOSITORY_ID, repositoryId);
    }

    @Override
    public String getBranchId()
    {
        return getString(FIELD_BRANCH_ID);
    }

    @Override
    public void setBranchId(String branchId)
    {
        set(FIELD_BRANCH_ID, branchId);
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
    public List<String> getTags()
    {
        List<String> list = null;

        ObjectNode tags = getObject(FIELD_TAGS);
        if (tags != null)
        {
            Iterator<String> fieldNames = tags.fieldNames();
            while (fieldNames.hasNext())
            {
                list.add(fieldNames.next());
            }
        }

        return list;
    }

    @Override
    public String getTagTitle(String tag)
    {
        String tagTitle = null;

        ObjectNode tags = getObject(FIELD_TAGS);
        if (tags != null)
        {
            ObjectNode tagObject = JsonUtil.objectGetObject(tags, tag);
            if (tagObject != null)
            {
                tagTitle = JsonUtil.objectGetString(tagObject, FIELD_TAG_TITLE);
            }
        }

        return tagTitle;
    }

    @Override
    public void addTag(String tag, String title)
    {
        ObjectNode tagObject = JsonUtil.createObject();
        tagObject.put(FIELD_TAG_TITLE, title);

        ObjectNode tags = getObject(FIELD_TAGS);
        if (tags == null)
        {
            tags = JsonUtil.createObject();
            set(FIELD_TAGS, tags);
        }

        tags.put(tag, tagObject);
    }

}
