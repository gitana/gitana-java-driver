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

package org.gitana.platform.client.node;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.TypedIDConstants;

/**
 * @author uzi
 */
public class AssociationImpl extends BaseNodeImpl implements Association
{
	private Node sourceNode;
	private Node targetNode;

    private Changeset sourceChangeset;
    private Changeset targetChangeset;
	
	private QName sourceNodeTypeQName;
	private QName targetNodeTypeQName;

    /**
     * Existing node constructor.
     *
     * @param branch
     * @param obj
     * @param isSaved
     */
    public AssociationImpl(Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(branch, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_ASSOCIATION;
    }

    @Override
    public boolean isOwned()
    {
        return false;
    }
	
	@Override
    public QName getSourceNodeTypeQName()
    {
		if (this.sourceNodeTypeQName == null)
		{
			String sourceType = getString(FIELD_SOURCE_TYPE);
			if (sourceType != null)
			{
				this.sourceNodeTypeQName = QName.create(sourceType);
			}
		}
		
		return this.sourceNodeTypeQName;
    }
    
	@Override
	public void setSourceNodeTypeQName(QName qname)
    {
		set(FIELD_SOURCE_TYPE, qname.toString());
		this.sourceNodeTypeQName = qname;
    }
    
	@Override
    public String getSourceNodeId()
    {
		return getString(FIELD_SOURCE);
    }

	@Override
    public void setSourceNodeId(String nodeId)
    {
        set(FIELD_SOURCE, nodeId);
    }

	@Override
    public String getSourceChangesetId()
    {
		return getString(FIELD_SOURCE_CHANGESET);
    }

	@Override
    public void setSourceChangesetId(String sourceChangesetId)
    {
        set(FIELD_SOURCE_CHANGESET, sourceChangesetId);
    }

	@Override
    public QName getTargetNodeTypeQName()
    {
		if (this.targetNodeTypeQName == null)
		{
			String targetType = getString(FIELD_TARGET_TYPE);
			if (targetType != null)
			{
				this.targetNodeTypeQName = QName.create(targetType);
			}
		}
		
		return this.targetNodeTypeQName;
    }

	@Override
    public void setTargetNodeTypeQName(QName qname)
    {
		set(FIELD_TARGET_TYPE, qname.toString());
		this.targetNodeTypeQName = qname;
    }

	@Override
    public String getTargetNodeId()
    {
        return getString(FIELD_TARGET);
    }

	@Override
    public void setTargetNodeId(String targetNodeId)
    {
        set(FIELD_TARGET, targetNodeId);
    }

	@Override
    public String getTargetChangesetId()
    {
		return getString(FIELD_TARGET_CHANGESET);
    }
    
	@Override
    public void setTargetChangesetId(String targetChangesetId)
    {
        set(FIELD_TARGET_CHANGESET, targetChangesetId);
    }

	@Override
    public boolean equals(Object obj)
    {
        boolean equals = false;

        if (obj instanceof Association)
        {
            Association _a = (Association) obj;

            equals = getId().equals(_a.getId()) &&
                     getChangesetId().equals(_a.getChangesetId()) &&
                     getBranchId().equals(_a.getBranchId());
        }
        else
        {
            equals = super.equals(obj);
        }

        return equals;
    }

	@Override
    public Node getSourceNode()
    {
    	if (this.sourceNode == null)
    	{
            this.sourceNode = (Node) this.getBranch().readNode(getSourceNodeId());
    	}
    	
    	return this.sourceNode;
    }

	@Override
    public Changeset getSourceChangeset()
    {
        // TODO: implement
        /*
        if (this.sourceChangeset == null)
        {
            this.sourceChangeset = this.getRepository().changesets().read(getSourceChangesetId());
        }
        */

        return this.sourceChangeset;
    }

	@Override
    public Node getTargetNode()
    {
    	if (this.targetNode == null)
    	{
            this.targetNode = (Node) this.getBranch().readNode(getTargetNodeId());
    	}
    	
    	return this.targetNode;
    }

	@Override
    public Changeset getTargetChangeset()
    {
        // TODO: implement
        /*
        if (this.targetChangeset == null)
        {
            this.targetChangeset = this.getRepository().changesets().read(getTargetChangesetId());
        }
        */

        return this.targetChangeset;
    }

	@Override
    public void setSourceNode(Node sourceNode)
    {
        if (sourceNode != null)
        {
            this.sourceNode = sourceNode;

            // adjust pointers
            this.setSourceChangesetId(sourceNode.getChangesetId());
            this.setSourceNodeId(sourceNode.getId());
            this.setSourceNodeTypeQName(sourceNode.getTypeQName());
        }
    }
    
	@Override
    public void setTargetNode(Node targetNode)
    {
        if (targetNode != null)
        {
            this.targetNode = targetNode;

            // adjust pointers
            this.setTargetChangesetId(targetNode.getChangesetId());
            this.setTargetNodeId(targetNode.getId());
            this.setTargetNodeTypeQName(targetNode.getTypeQName());
        }
    }

    @Override
    public Node getOtherNode(Node node)
    {
        Node other = null;

        if (getSourceNode().equals(node))
        {
            other = getTargetNode();
        }
        else
        {
            other = getSourceNode();
        }

        return other;
    }

    @Override
    public boolean isSourceLoaded()
    {
        return (this.sourceNode != null);
    }

    @Override
    public boolean isTargetLoaded()
    {
        return (this.targetNode != null);
    }

    @Override
    public ObjectNode getTimestamp()
    {
        return getObject(FIELD_TIMESTAMP);
    }

    @Override
    public Directionality getDirectionality()
    {
        Directionality directionality = null;

        if (getString(FIELD_DIRECTIONALITY) != null)
        {
            directionality = Directionality.valueOf(getString(FIELD_DIRECTIONALITY));
        }

        return directionality;
    }

    @Override
    public void setDirectionality(Directionality directionality)
    {
        set(FIELD_DIRECTIONALITY, directionality.toString());
    }

}
