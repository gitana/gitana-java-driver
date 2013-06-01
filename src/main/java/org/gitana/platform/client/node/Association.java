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

package org.gitana.platform.client.node;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.support.QName;
import org.gitana.platform.client.changeset.Changeset;

/**
 * Association
 * 
 * @author uzi
 */
public interface Association extends BaseNode
{
    // field properties
	public final static String FIELD_SOURCE = "source";
    public final static String FIELD_SOURCE_TYPE = "source_type";    
    public final static String FIELD_SOURCE_CHANGESET = "source_changeset";
    public final static String FIELD_TARGET = "target";
    public final static String FIELD_TARGET_TYPE = "target_type";    
    public final static String FIELD_TARGET_CHANGESET = "target_changeset";
    public final static String FIELD_TIMESTAMP = "timestamp";
    public final static String FIELD_DIRECTIONALITY = "directionality";

    // source accessor
    public QName getSourceNodeTypeQName();
    public void setSourceNodeTypeQName(QName sourceNodeTypeQName);
    public String getSourceNodeId();
    public void setSourceNodeId(String sourceNodeId);
    public String getSourceChangesetId();
    public void setSourceChangesetId(String sourceChangesetId);
    
    // target accessor
    public QName getTargetNodeTypeQName();
    public void setTargetNodeTypeQName(QName targetNodeType);
    public String getTargetNodeId();
    public void setTargetNodeId(String targetNodeId);
    public String getTargetChangesetId();
    public void setTargetChangesetId(String targetChangesetId);
    
    // methods
    public Node getSourceNode();
    public Changeset getSourceChangeset();
    public Node getTargetNode();
    public Changeset getTargetChangeset();

    public void setSourceNode(Node sourceNode);
    public void setTargetNode(Node targetNode);

    public Node getOtherNode(Node node);

    public boolean isOwned();

    public boolean isSourceLoaded();
    public boolean isTargetLoaded();

    /**
     * @return timestamp
     */
    public ObjectNode getTimestamp();

    // orientation
    public Directionality getDirectionality();
    public void setDirectionality(Directionality directionality);

    
}