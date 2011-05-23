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

package org.gitana.repo.client.support;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.branch.BranchType;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Driver;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.types.AssociationDefinition;
import org.gitana.repo.client.types.TypeDefinition;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.namespace.QName;
import org.gitana.util.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class BranchImpl extends AbstractRepositoryDocumentImpl implements Branch
{
    private boolean dirty;

    protected BranchImpl(Driver driver, Repository repository, ObjectNode obj, boolean isSaved)
    {
        super(driver, repository, obj, isSaved);
    }

    private void ensureNotDirty()
    {
        if (this.dirty)
        {
            this.reload();
            this.dirty = false;
        }
    }

    @Override
    public void markDirty()
    {
        this.dirty = true;
    }

    @Override
    public void setTipChangesetId(String changesetId)
    {
        set(FIELD_TIP, changesetId);
    }
        
    @Override
    public String getTipChangesetId()
    {
        ensureNotDirty();
    	return getString(FIELD_TIP);
    }
    
    @Override
	public void setRootChangesetId(String rootChangesetId) 
	{
    	set(FIELD_ROOT, rootChangesetId);
	}
	
    @Override
    public String getRootChangesetId() 
    {
        ensureNotDirty();
        return getString(FIELD_ROOT);
    }
    
    @Override
    public boolean isReadOnly()
    {
        ensureNotDirty();
    	return getBoolean(FIELD_READONLY);
    }
    
    @Override
    public boolean isSnapshot()
    {
        ensureNotDirty();
    	return getBoolean(FIELD_SNAPSHOT);
    }

	@Override
	public boolean isFrozen() 
	{
        ensureNotDirty();
		return getBoolean(Branch.FIELD_FROZEN);
	}

    @Override
    public String getJoinBranchId()
    {
        ensureNotDirty();
        return getString(FIELD_JOIN_BRANCH);
    }

    @Override
    public String getRootBranchId()
    {
        ensureNotDirty();
        return getString(FIELD_ROOT_BRANCH);
    }

    @Override
    public boolean isMaster()
    {
        ensureNotDirty();
        return BranchType.MASTER.equals(getType());
    }

    @Override
    public BranchType getType()
    {
        ensureNotDirty();

        BranchType type = null;

        String typeId = getString(FIELD_BRANCH_TYPE);
        if (typeId != null)
        {
            type = BranchType.valueOf(typeId);
        }

        return type;
    }


    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Branch)
        {
            Branch other = (Branch) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELF
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void reload()
    {
        Branch b = getRepository().readBranch(this.getId());
        this.reload(b.getObject());
    }

    @Override
    public void update()
    {
        getRemote().put("/repositories/" + getRepositoryId() + "/branches/" + getId(), getObject());
    }

    @Override
    public void delete()
    {
        // TODO: not implemented
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/acl");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/acl/" + principalId + "/grant/" + authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/acl/" + principalId + "/revoke/" + authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // NODES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Map<String, Node> fetchNodes()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/nodes");

        return getFactory().nodes(this, response);
    }

    @Override
    public List<Node> listNodes()
    {
        Map<String, Node> map = fetchNodes();

        List<Node> list = new ArrayList<Node>();
        for (Node node : map.values())
        {
            list.add(node);
        }

        return list;
    }

    @Override
    public Node readNode(String nodeId)
    {
        Node node = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/nodes/" + nodeId);
            node = getFactory().node(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return node;
    }

    @Override
    public Node createNode()
    {
        return createNode(JsonUtil.createObject());
    }

    @Override
    public Node createNode(QName typeQName)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put("_type", typeQName.toString());

        return createNode(object);
    }

    @Override
    public Node createNode(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/nodes", object);

        String nodeId = response.getId();
        Node node = readNode(nodeId);

        // mark the branch as being dirty (since the tip will have moved)
        this.markDirty();

        return node;
    }

    @Override
    public Map<String, Node> queryNodes(ObjectNode query)
    {
        Response response = getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/nodes/query", query);

        return getFactory().nodes(this, response);
    }

    @Override
    public Map<String, Node> searchNodes(String text)
    {
        // url encode the text
        try
        {
            text = URLEncoder.encode(text, "utf-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new RuntimeException(uee);
        }

        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/nodes/search?text=" + text);

        return getFactory().nodes(this, response);
    }

    @Override
    public Node readPerson (String userId, boolean createIfNotFound)
    {
        // invoke
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/person/" + userId + "?createIfNotFound=" + createIfNotFound);
        return getFactory().node(this, response);
    }

    @Override
    public Node readGroup (String groupId, boolean createIfNotFound)
    {
        // invoke
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/group/" + groupId + "?createIfNotFound=" + createIfNotFound);
        return getFactory().node(this, response);
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DEFINITIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Map<QName, Node> fetchDefinitions()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/definitions");

        Map<String, Node> nodes = getFactory().nodes(this, response);

        Map<QName, Node> definitions = new HashMap<QName, Node>();
        for (Node node: nodes.values())
        {
            definitions.put(node.getQName(), node);
        }

        return definitions;
    }

    @Override
    public List<Node> listDefinitions()
    {
        Map<QName, Node> map = fetchDefinitions();

        List<Node> list = new ArrayList<Node>();
        for (Node node : map.values())
        {
            list.add(node);
        }

        return list;
    }

    @Override
    public Node readDefinition(QName qname)
    {
        Node node = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getId() + "/definitions/" + qname.toString());
            node = getFactory().node(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return node;
    }

    @Override
    public TypeDefinition defineType(QName definitionQName)
    {
        return defineType(definitionQName, JsonUtil.createObject());
    }

    @Override
    public TypeDefinition defineType(QName definitionQName, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put(Node.FIELD_QNAME, definitionQName.toString());
        object.put(Node.FIELD_TYPE_QNAME, "d:type");
        object.put("type", "object");
        //object.put("description", definitionQName.toString());

        return (TypeDefinition) createNode(object);
    }

    @Override
    public AssociationDefinition defineAssociationType(QName definitionQName)
    {
        return defineAssociationType(definitionQName, JsonUtil.createObject());
    }

    @Override
    public AssociationDefinition defineAssociationType(QName definitionQName, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put(Node.FIELD_QNAME, definitionQName.toString());
        object.put(Node.FIELD_TYPE_QNAME, "d:association");
        object.put("type", "object");
        object.put("description", definitionQName.toString());

        return (AssociationDefinition) createNode(object);
    }

}
