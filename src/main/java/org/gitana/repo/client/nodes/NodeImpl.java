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

package org.gitana.repo.client.nodes;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.association.Direction;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.namespace.QName;

import java.util.List;
import java.util.Map;

/**
 * Default "n:node" implementation for a node.
 *
 * This class is the base class for all nodes in the Gitana repository.
 * 
 * @author uzi
 */
public class NodeImpl extends BaseNodeImpl implements Node
{
	private QName qname;
	private QName typeQName;

    private Gitana gitana;
    private Branch branch;
	
    /**
     * New node constructor.
     *
     * @param branch
     * @param obj
     */
    public NodeImpl(Gitana gitana, Branch branch, ObjectNode obj)
    {
        this(gitana, branch, obj, false);
    }
    
    /**
     * Existing node constructor.
     *
     * @param branch
     * @param obj
     * @param isSaved
     */
    public NodeImpl(Gitana gitana, Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(gitana, branch, obj, isSaved);

        this.init();
    }

    private void init()
    {
    }

    /**
     * @return access control list
     */
    public ACL getACL()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/acl");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/acl/" + principalId + "/grant/" + authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/acl/" + principalId + "/revoke/" + authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public void uploadAttachment(byte[] bytes, String contentType)
    {
        uploadAttachment(null, bytes, contentType);
    }

    @Override
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType)
    {
        // build the uri
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachment";
        if (attachmentId != null)
        {
            uri += "/" + attachmentId;
        }

        try
        {
            getRemote().upload(uri, bytes, contentType);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public byte[] downloadAttachment()
    {
        return downloadAttachment(null);
    }

    @Override
    public byte[] downloadAttachment(String attachmentId)
    {
        // build the uri
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachment";
        if (attachmentId != null)
        {
            uri += "/" + attachmentId;
        }

        byte[] bytes = null;
        try
        {
            bytes = getRemote().download(uri);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }

    @Override
    public Map<String, Association> associations()
    {
        return associations(Direction.BOTH);
    }

    @Override
    public Map<String, Association> associations(Direction direction)
    {
        return associations(direction, null);
    }

    @Override
    public Map<String, Association> associations(QName associationTypeQName)
    {
        return associations(Direction.BOTH, associationTypeQName);
    }

    @Override
    public Map<String, Association> associations(Direction direction, QName associationTypeQName)
    {
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/associations";

        boolean first = true;

        // direction
        if (direction != null)
        {
            uri += (first ? "?" : "&");
            uri = uri + "direction=" + direction.toString();
            first = false;
        }

        // associationTypeQName type qname
        if (associationTypeQName != null)
        {
            uri += (first ? "?" : "&");
            uri = uri + "type=" + associationTypeQName.toString();
            first = false;
        }

        Response response = getRemote().get(uri);

        return getFactory().associations(getBranch(), response);

    }

    @Override
    public Association associate(Node targetNode, QName associationTypeQName)
    {
        return associate(targetNode, Direction.OUTGOING, associationTypeQName);
    }

    @Override
    public Association associate(Node otherNode, Direction direction, QName associationTypeQName)
    {
        String sourceNodeId = null;
        String targetNodeId = null;

        if (Direction.INCOMING.equals(direction))
        {
            sourceNodeId = otherNode.getId();
            targetNodeId = getId();
        }
        else if (Direction.OUTGOING.equals(direction))
        {
            sourceNodeId = getId();
            targetNodeId = otherNode.getId();
        }
        else
        {
            throw new RuntimeException("Invalid direction: " + direction.toString());
        }

        Response r1 = getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + sourceNodeId + "/associate?node=" + targetNodeId + "&type=" + associationTypeQName.toString());

        String associationId = r1.getId();

        // read it back
        Response r2 = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + associationId);
        return getFactory().association(getBranch(), r2);
    }


}
