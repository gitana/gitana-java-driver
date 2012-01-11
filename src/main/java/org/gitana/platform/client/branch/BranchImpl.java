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

package org.gitana.platform.client.branch;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.http.HttpPayload;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.nodes.BaseNode;
import org.gitana.platform.client.nodes.Node;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.repository.AbstractRepositoryDocumentImpl;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.types.*;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.services.branch.BranchType;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class BranchImpl extends AbstractRepositoryDocumentImpl implements Branch
{
    private boolean dirty;

    public BranchImpl(Repository repository, ObjectNode obj, boolean isSaved)
    {
        super(repository, obj, isSaved);
    }

    @Override
    protected String getResourceUri()
    {
        return "/repositories/" + getRepositoryId() + "/branches/" + getId();
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
		return getBoolean(FIELD_FROZEN);
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
        getRemote().put(getResourceUri(), getObject());
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
        Response response = getRemote().get(getResourceUri() + "/acl/list");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getACL(String principalId)
    {
        Response response = getRemote().get(getResourceUri() + "/acl?id=" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/grant?id=" + principalId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/revoke?id=" + principalId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
    }

    @Override
    public Map<String, Map<String, AuthorityGrant>> getAuthorityGrants(List<String> principalIds)
    {
        ObjectNode object = JsonUtil.createObject();
        JsonUtil.objectPut(object, "principals", principalIds);

        Response response = getRemote().post(getResourceUri() + "/authorities", object);
        return getFactory().principalAuthorityGrants(response);
    }

    @Override
    public boolean hasPermission(String principalId, String permissionId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/permissions/" + permissionId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // NODES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Node> listNodes()
    {
        return listNodes(null);
    }

    @Override
    public ResultMap<Node> listNodes(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/nodes", params);
        ResultMap<BaseNode> baseNodes = getFactory().nodes(this, response);

        return DriverUtil.toNodes(baseNodes);
    }

    @Override
    public BaseNode readNode(String nodeId)
    {
        BaseNode baseNode = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/nodes/" + nodeId);
            baseNode = getFactory().node(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return baseNode;
    }

    @Override
    public BaseNode createNode()
    {
        return createNode(JsonUtil.createObject());
    }

    @Override
    public BaseNode createNode(QName typeQName)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put("_type", typeQName.toString());

        return createNode(object);
    }

    @Override
    public BaseNode createNode(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/nodes", object);

        String nodeId = response.getId();
        BaseNode node = readNode(nodeId);

        // mark the branch as being dirty (since the tip will have moved)
        this.markDirty();

        return node;
    }

    @Override
    public Map<String, BaseNode> createNodes(Map<String, String> params, HttpPayload... payloads)
    {
        if (params == null)
        {
            params = new HashMap<String, String>();
        }

        // build the uri
        String uri = getResourceUri() + "/nodes";

        Response response = null;
        try
        {
            response = getRemote().upload(uri, params, payloads);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return getFactory().nodes(this, response);
    }

    @Override
    public ResultMap<BaseNode> queryNodes(ObjectNode query)
    {
        return queryNodes(query, null);
    }

    @Override
    public ResultMap<BaseNode> queryNodes(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/nodes/query", params, query);
        return getFactory().nodes(this, response);
    }

    @Override
    public ResultMap<BaseNode> searchNodes(String text)
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

        Response response = getRemote().get(getResourceUri() + "/nodes/search?text=" + text);

        return getFactory().nodes(this, response);
    }

    @Override
    public PermissionCheckResults checkNodePermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }

    @Override
    public Person readPerson (String userId, boolean createIfNotFound)
    {
        // invoke
        Response response = getRemote().get(getResourceUri() + "/person/" + userId + "?createIfNotFound=" + createIfNotFound);
        return (Person) getFactory().node(this, response);
    }

    @Override
    public Group readGroup (String groupId, boolean createIfNotFound)
    {
        // invoke
        Response response = getRemote().get(getResourceUri() + "/group/" + groupId + "?createIfNotFound=" + createIfNotFound);
        return (Group) getFactory().node(this, response);
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DEFINITIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Map<QName, Definition> listDefinitions()
    {
        Response response = getRemote().get(getResourceUri() + "/definitions");

        Map<String, BaseNode> nodes = getFactory().nodes(this, response);

        Map<QName, Definition> definitions = new LinkedHashMap<QName, Definition>();
        for (BaseNode node: nodes.values())
        {
            definitions.put(node.getQName(), (Definition) node);
        }

        return definitions;
    }

    @Override
    public Definition readDefinition(QName qname)
    {
        Definition definition = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/definitions/" + qname.toString());
            definition = (Definition) getFactory().node(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return definition;
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // IMPORT/EXPORT
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Archive exportPublicationArchive(Vault vault, String groupId, String artifactId, String versionId)
    {
        //
        // start the export
        //
        Response response1 = getRemote().post(getResourceUri() + "/export?vault=" + vault.getId() + "&group=" + groupId + "&artifact=" + artifactId + "&version=" + versionId);
        String jobId = response1.getId();


        //
        // query job service until job completes or fails
        //
        Job job = null;
        try
        {
            boolean complete = false;
            while (!complete)
            {
                job = getCluster().readJob(jobId);
                if (job == null)
                {
                    throw new Exception("No job found: " + jobId);
                }
                if (job.isError())
                {
                    throw new Exception("Job: " + job.getId() + " failed with: " + job.getLogEntries());
                }
                if (job.isFinished())
                {
                    complete = true;
                }

                if (!complete)
                {
                    Thread.sleep(200);
                }
            }
        }
        catch (Exception ie)
        {
            throw new RuntimeException(ie);
        }


        //
        // read archive
        //
        return vault.lookupArchive(groupId, artifactId, versionId);
    }

    @Override
    public void importPublicationArchive(Vault vault, String groupId, String artifactId, String versionId)
    {
        // post binary to server and get back job id
        Response response = getRemote().post(getResourceUri() + "/import?vault=" + vault.getId() + "&group=" + groupId + "&artifact=" + artifactId + "&version=" + versionId);
        String jobId = response.getId();


        //
        // query job service until job completes or fails
        //
        Job job = null;
        try
        {
            boolean complete = false;
            while (!complete)
            {
                job = getCluster().readJob(jobId);
                if (job.isError())
                {
                    complete = true;
                }
                if (job.isFinished())
                {
                    complete = true;
                }

                if (!complete)
                {
                    Thread.sleep(200);
                }
            }
        }
        catch (InterruptedException ie)
        {
            throw new RuntimeException(ie);
        }
        if (job.isError())
        {
            throw new RuntimeException("Job: " + job.getId() + " failed with: " + job.getLogEntries());
        }


        // if we made it this far, the job completed successfully
    }

    @Override
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm)
    {
        return findNodes(query, searchTerm, null);
    }

    @Override
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, Pagination pagination)
    {
        String uri = "/repositories/" + this.getRepositoryId() + "/branches/" + this.getId() + "/nodes/find";

        ObjectNode payload = JsonUtil.createObject();
        if (query != null)
        {
            payload.put("query", query);
        }
        if (searchTerm != null)
        {
            payload.put("search", searchTerm);
        }

        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(uri, params, payload);

        return getFactory().nodes(this, response);
    }

    @Override
    public NodeList createList(String listKey, QName itemTypeQName)
    {
        Response response = getRemote().post(getResourceUri() + "/lists/" + listKey + "?type=" + itemTypeQName.toString());

        String nodeId = response.getId();
        NodeList nodeList = (NodeList) readNode(nodeId);

        // mark the branch as being dirty (since the tip will have moved)
        this.markDirty();

        return nodeList;
    }

    @Override
    public NodeList readList(String listKey)
    {
        NodeList nodeList = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/lists/" + listKey);
            nodeList = (NodeList) getFactory().node(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return nodeList;
    }


}
