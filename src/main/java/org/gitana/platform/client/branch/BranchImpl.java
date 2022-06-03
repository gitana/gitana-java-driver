/**
 * Copyright 2022 Gitana Software, Inc.
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
package org.gitana.platform.client.branch;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.http.HttpPayload;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.deletion.Deletion;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.repository.AbstractRepositoryDocumentImpl;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.node.type.*;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.services.branch.BranchType;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.*;

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
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_BRANCH;
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
    public void grant(DomainPrincipal principal, String authorityId)
    {
        grant(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/revoke?id=" + principalId);
    }

    @Override
    public void revoke(DomainPrincipal principal, String authorityId)
    {
        revoke(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public void revokeAll(DomainPrincipal principal)
    {
        revokeAll(principal.getDomainQualifiedId());
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").booleanValue();
        }

        return has;
    }

    @Override
    public boolean hasAuthority(DomainPrincipal principal, String authorityId)
    {
        return hasAuthority(principal.getDomainQualifiedId(), authorityId);
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
            has = response.getObjectNode().get("check").booleanValue();
        }

        return has;
    }

    @Override
    public boolean hasPermission(DomainPrincipal principal, String authorityId)
    {
        return hasPermission(principal.getDomainQualifiedId(), authorityId);
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
        return readNode(nodeId, null);
    }

    @Override
    public BaseNode readNode(String nodeId, String path)
    {
        BaseNode baseNode = null;

        try
        {
            Map<String, String> params = new HashMap<String, String>();

            if (path != null)
            {
                params.put("path", path);
            }

            Response response = getRemote().get(getResourceUri() + "/nodes/" + nodeId, params);
            baseNode = getFactory().node(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
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
        return searchNodes(text, null);
    }

    @Override
    public ResultMap<BaseNode> searchNodes(String text, Pagination pagination)
    {
        /*
        // url encode the text
        try
        {
            text = URLEncoder.encode(text, "utf-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new RuntimeException(uee);
        }
        */

        // Put pagination and text in params
        Map<String, String> params = DriverUtil.params(pagination);
        params.put("text", text);

        Response response = getRemote().get(getResourceUri() + "/nodes/search", params);

        return getFactory().nodes(this, response);
    }

    @Override
    public ResultMap<BaseNode> searchNodes(ObjectNode searchObject)
    {
        return searchNodes(searchObject, null);
    }

    @Override
    public ResultMap<BaseNode> searchNodes(ObjectNode searchObject, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        ObjectNode payload = null;

        if (searchObject.has("search"))
        {
            payload = searchObject;
        }
        else
        {
            payload = JsonUtil.createObject();
            payload.put("search", searchObject);
        }

        Response response = getRemote().post(getResourceUri() + "/nodes/search", params, payload);

        return getFactory().nodes(this, response);
    }

    @Override
    public List<String> deleteNodes(List<String> nodeIds)
    {
        ObjectNode payload = JsonUtil.createObject();
        ArrayNode docIds = JsonUtil.createArray(nodeIds);
        payload.set("_docs", docIds);

        Response response = getRemote().post(getResourceUri() + "/nodes/delete", null, payload);
        ObjectNode responseObj = response.getObjectNode();
        ArrayNode docs = JsonUtil.objectGetArray(responseObj, "_docs");

        return Arrays.asList(JsonUtil.toStringArray(docs));
    }


    @Override
    public Node rootNode()
    {
        return (Node) this.readNode("root");
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
    public Map<QName, Definition> listDefinitions(DefinitionFilterType filter, boolean custom, boolean system, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        if (filter != null)
        {
            params.put("filter", filter.toString());
        }

        if (custom)
        {
            params.put("custom", Boolean.toString(custom));
        }

        if (system)
        {
            params.put("system", Boolean.toString(system));
        }

        Response response = getRemote().get(getResourceUri() + "/definitions", params);

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
            // TODO: the remote layer needs to hand back more interesting
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
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return nodeList;
    }

    @Override
    public Deletion readDeletion(String nodeId)
    {
        Deletion deletion = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/deletions/" + nodeId);
            deletion = (Deletion) getFactory().deletion(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return deletion;
    }

    @Override
    public ResultMap<Deletion> queryDeletions(ObjectNode query)
    {
        return queryDeletions(query, null);
    }

    @Override
    public ResultMap<Deletion> queryDeletions(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/deletions/query", params, query);
        return getFactory().deletions(this, response);
    }

    @Override
    public void purgeAllDeletions()
    {
        getRemote().post(getResourceUri() + "/deletions/purgeall");
    }

    @Override
    public ObjectNode graphqlQuery(String query)
    {
        return graphqlQuery(query, null, null);
    }

    @Override
    public ObjectNode graphqlQuery(String query, String operationName, Map<String, Object> variables)
    {
        Map<String, String> params = DriverUtil.params();
        params.put("query", query);

        // convert variables to a json string
        if (variables != null)
        {
            ObjectNode variablesObj = JsonUtil.createObject(variables);
            params.put("variables", variablesObj.toString());
        }

        if (operationName != null)
        {
            params.put("operationName", operationName);
        }

        Response response = getRemote().get(getResourceUri() + "/graphql", params);
        return response.getObjectNode();
    }

    @Override
    public String graphqlSchema()
    {
        String response = getRemote().getString(getResourceUri() + "/graphql/schema", null);
        return response;
    }

    @Override
    public Job startCopyFrom(String sourceRepositoryId, String sourceBranchId, List<String> nodeIds, ObjectNode config)
    {
        Map<String, String> params = DriverUtil.params();
        params.put("id", sourceBranchId);

        if (config == null)
        {
            config = JsonUtil.createObject();
        }

        config.set("nodeIds", JsonUtil.createArray(nodeIds));
        config.put("branchId", sourceBranchId);
        config.put("targetBranchId", this.getId());
        config.put("repositoryId", sourceRepositoryId);
        config.put("sourceRepositoryId", sourceRepositoryId);
        config.put("targetRepositoryId", this.getRepositoryId());

        Response response = getRemote().post(getResourceUri() + "/copyfrom/start", params, config);
        String jobId = response.getId();

        return getCluster().readJob(jobId);
    }
}
