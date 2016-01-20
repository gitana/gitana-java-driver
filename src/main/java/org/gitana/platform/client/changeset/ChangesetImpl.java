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

package org.gitana.platform.client.changeset;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.repository.AbstractRepositoryDocumentImpl;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public class ChangesetImpl extends AbstractRepositoryDocumentImpl implements Changeset
{
    public ChangesetImpl(Repository repository, ObjectNode obj, boolean isSaved)
    {
        super(repository, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_CHANGESET;
    }

    @Override
    public String getResourceUri()
    {
        return "/repositories/" + getRepositoryId() + "/changesets/" + getId();
    }

    @Override
    public int getRev()
    {
    	return getInt(FIELD_REV);
    }

    @Override
    public Branch getBranch()
    {
        return getRepository().readBranch(getBranchId());
    }

    @Override
    public String[] getTags()
    {
        String[] array = null;
        
        if (has(FIELD_TAGS))
        {
        	array = JsonUtil.toStringArray(getArray(FIELD_TAGS));
        }
        
        return array;
    }

    @Override
    public String getSummary()
    {
    	return getString(FIELD_SUMMARY);
    }

    @Override
    public void setSummary(String summary)
    {
        set(FIELD_SUMMARY, summary);
    }

    @Override
    public String getBranchId()
    {
        return getString(FIELD_BRANCH);
    }

    @Override
    public boolean isActive()
    {
    	return getBoolean(FIELD_ACTIVE);
    }
                
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) 
    {
        boolean equals = false;
        
        if (obj instanceof Changeset)
        {
            equals = getId().equals(((Changeset)obj).getId());
        }
        else
        {
            equals = super.equals(obj);
        }
        
        return equals;
    }    

    @Override
    public List<String> getParentChangesetIds()
    {
    	List<String> parentChangesetIds = new ArrayList<String>();
    	
        if (has(FIELD_PARENTS))
        {
        	ArrayNode parents = getArray(FIELD_PARENTS);
            if (parents.size() > 0)
            {
                for (int i = 0; i < parents.size(); i++)
                {
                	parentChangesetIds.add(parents.get(i).textValue());
                }                
            }
        }
        
        return parentChangesetIds;
    }
    
    @Override
    public void setParentChangesetIds(List<String> parentChangesetIds)
    {
    	ArrayNode array = JsonUtil.createArray(parentChangesetIds);
        set(FIELD_PARENTS, array);
    }

    @Override
    public boolean hasTag(String tag)
    {
        boolean has = false;
        
        if (has(FIELD_TAGS))
        {
        	ArrayNode arrayNode = getArray(FIELD_TAGS);
            has = JsonUtil.arrayContains(arrayNode, tag);
        }
        
        return has;
    }

    @Override
    public void addTag(String tag)
    {
    	ArrayNode array = null;
        
        if (!has(FIELD_TAGS))
        {
        	array = JsonUtil.createArray();
            set(FIELD_TAGS, array);
        }
        else
        {
        	array = getArray(FIELD_TAGS);
        }

        if (!JsonUtil.arrayContains(array, tag))
        {
            array.add(tag);
            
            set(FIELD_TAGS, array);
        }
    }
    
    @Override
    public void removeTag(String tag)
    {
    	ArrayNode array = null;
        
        if (!has(FIELD_TAGS))
        {
            array = JsonUtil.createArray();
            set(FIELD_TAGS, array);
        }
        else
        {
            array = getArray(FIELD_TAGS);            
        }
        
        JsonUtil.arrayRemove(array, tag);
    }

    @Override
    public void setActive(boolean active)
    {
        set(FIELD_ACTIVE, active);
    }

    @Override
    public ResultMap<BaseNode> listNodes()
    {
        Response response = getRemote().get(getResourceUri() + "/nodes");
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Branch branch = this.getRepository().readBranch(getBranchId());

        ResultMap<BaseNode> map = new ResultMapImpl<BaseNode>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            BaseNode node = getFactory().produce(branch, object, true);
            map.put(node.getId(), node);
        }

        return map;

    }
}
