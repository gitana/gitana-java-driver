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
import org.gitana.repo.client.beans.TraversalResults;
import org.gitana.repo.client.services.Translations;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.namespace.QName;
import org.gitana.util.JsonUtil;

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
     * Existing node constructor.
     *
     * @param branch
     * @param obj
     * @param isSaved
     */
    public NodeImpl(Gitana gitana, Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(gitana, branch, obj, isSaved);

        this.gitana = gitana;

        this.init();
    }

    private void init()
    {
    }

    @Override
    public Translations translations()
    {
        return new Translations(gitana, this);
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
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachments/" + attachmentId;

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
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/attachments/" + attachmentId;

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
        return associations(null, direction);
    }

    @Override
    public Map<String, Association> associations(QName associationTypeQName)
    {
        return associations(associationTypeQName, Direction.BOTH);
    }

    @Override
    public Map<String, Association> associations(QName associationTypeQName, Direction direction)
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
        return associate(targetNode, associationTypeQName, Direction.OUTGOING);
    }

    @Override
    public Association associate(Node otherNode, QName associationTypeQName, Direction direction)
    {
        String sourceNodeId = getId();
        String targetNodeId = otherNode.getId();

        Response r1 = getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + sourceNodeId + "/associate?node=" + targetNodeId + "&type=" + associationTypeQName.toString() + "&direction=" + direction.toString());

        String associationId = r1.getId();

        // read it back
        Response r2 = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + associationId);
        return getFactory().association(getBranch(), r2);
    }

    @Override
    public void unassociate(Node targetNode, QName associationTypeQName)
    {
        unassociate(targetNode, associationTypeQName, Direction.OUTGOING);
    }

    @Override
    public void unassociate(Node otherNode, QName associationTypeQName, Direction direction)
    {
        String sourceNodeId = getId();
        String targetNodeId = otherNode.getId();

        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + sourceNodeId + "/unassociate?node=" + targetNodeId + "&type=" + associationTypeQName.toString() + "&direction=" + direction.toString());
    }

    @Override
    public TraversalResults traverse(ObjectNode traverse)
    {
        ObjectNode config = JsonUtil.createObject();
        config.put("traverse", traverse);

        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId() + "/traverse";
        Response r = getRemote().post(uri, config);

        TraversalResults results = new TraversalResults();
        results.parse(getFactory(), getBranch(), r);

        return results;
    }

    @Override
    public void mount(String mountKey)
    {
        String uri = "/repositories/" + this.getRepository().getId() + "/branches/" + this.getBranch().getId() + "/nodes/" + this.getId() + "/mount/" + mountKey;
        getRemote().post(uri);
    }

    @Override
    public void unmount()
    {
        String uri = "/repositories/" + this.getRepository().getId() + "/branches/" + this.getBranch().getId() + "/nodes/" + this.getId() + "/unmount";
        getRemote().post(uri);
    }

}
