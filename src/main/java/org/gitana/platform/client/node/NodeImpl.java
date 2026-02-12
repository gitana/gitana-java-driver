/**
 * Copyright 2026 Gitana Software, Inc.
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
 *   info@gitana.io
 */
package org.gitana.platform.client.node;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.LookupOptions;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.beans.TraversalResults;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.association.Direction;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.support.*;
import org.gitana.util.JsonUtil;

import java.util.*;

/**
 * Default "n:node" implementation for a node.
 *
 * This class is the base class for all nodes in the Gitana repository.
 * 
 * @author uzi
 */
public class NodeImpl extends BaseNodeImpl implements Node
{
    /**
     * Existing node constructor.
     *
     * @param branch
     * @param obj
     * @param isSaved
     */
    public NodeImpl(Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(branch, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_NODE;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @return access control list
     */
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
    public boolean hasPermission(DomainPrincipal principal, String permissionId)
    {
        return hasPermission(principal.getDomainQualifiedId(), permissionId);
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ASSOCIATIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Association> associations()
    {
        return associations((Pagination) null);
    }

    @Override
    public ResultMap<Association> associations(Pagination pagination)
    {
        return associations(Direction.ANY, pagination);
    }

    @Override
    public ResultMap<Association> associations(Direction direction)
    {
        return associations(direction, (Pagination) null);
    }

    @Override
    public ResultMap<Association> associations(Direction direction, Pagination pagination)
    {
        return associations(null, direction, pagination);
    }

    @Override
    public ResultMap<Association> associations(QName associationTypeQName)
    {
        return associations(associationTypeQName, (Pagination) null);
    }

    @Override
    public ResultMap<Association> associations(QName associationTypeQName, Pagination pagination)
    {
        return associations(associationTypeQName, Direction.ANY, pagination);
    }

    @Override
    public ResultMap<Association> associations(QName associationTypeQName, Direction direction)
    {
        return associations(associationTypeQName, direction, (Pagination) null);
    }

    @Override
    public ResultMap<Association> associations(QName associationTypeQName, Direction direction, Pagination pagination)
    {
        String uri = getResourceUri() + "/associations";

        // build params (from pagination)
        Map<String, String> params = DriverUtil.params(pagination);
        if (direction != null)
        {
            params.put("direction", direction.toString());
        }
        if (associationTypeQName != null)
        {
            params.put("type", associationTypeQName.toString());
        }

        Response response = getRemote().get(uri, params);

        return getFactory().associations(getBranch(), response);
    }

    @Override
    public Association associate(Node targetNode, QName associationTypeQName)
    {
        return associate(targetNode, associationTypeQName, Directionality.DIRECTED);
    }

    @Override
    public Association associate(Node targetNode, QName associationTypeQName, ObjectNode object)
    {
        return associate(targetNode, associationTypeQName, Directionality.DIRECTED, object);
    }

    @Override
    public Association associate(Node otherNode, QName associationTypeQName, Directionality directionality)
    {
        return associate(otherNode, associationTypeQName, directionality, null);
    }

    @Override
    public Association associate(Node otherNode, QName associationTypeQName, Directionality directionality, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        String targetNodeId = otherNode.getId();

        String uri = getResourceUri() + "/associate?node=" + targetNodeId + "&type=" + associationTypeQName.toString();
        if (!Directionality.DIRECTED.equals(directionality))
        {
            uri += "&directionality=" + directionality.toString();
        }

        Response r1 = getRemote().post(uri, object);

        String associationId = r1.getId();

        // read it back
        Response r2 = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + associationId);
        return getFactory().association(getBranch(), r2);
    }

    @Override
    public void unassociate(Node targetNode, QName associationTypeQName)
    {
        unassociate(targetNode, associationTypeQName, Directionality.DIRECTED);
    }

    @Override
    public void unassociate(Node otherNode, QName associationTypeQName, Directionality directionality)
    {
        String targetNodeId = otherNode.getId();

        String uri = getResourceUri() + "/unassociate?node=" + targetNodeId + "&type=" + associationTypeQName.toString();
        if (!Directionality.DIRECTED.equals(directionality))
        {
            uri += "&directionality=" + directionality.toString();
        }

        getRemote().post(uri);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TRAVERSE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public TraversalResults traverse(ObjectNode traverse)
    {
        ObjectNode config = JsonUtil.createObject();
        config.put("traverse", traverse);

        String uri = getResourceUri() + "/traverse";
        Response r = getRemote().post(uri, config);

        TraversalResults results = new TraversalResults();
        results.parse(getFactory(), getBranch(), r);

        return results;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MOUNT
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void mount(String mountKey)
    {
        String uri = getResourceUri() + "/mount/" + mountKey;
        getRemote().post(uri);
    }

    @Override
    public void unmount()
    {
        String uri = getResourceUri() + "/unmount";
        getRemote().post(uri);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TRANSLATIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Node createTranslation(String edition, Locale locale, ObjectNode object)
    {
        String uri = getResourceUri() + "/i18n?edition=" + edition + "&locale=" + locale.toString();
        Response r1 = getRemote().post(uri, object);
        String nodeId = r1.getId();

        Response r2 = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + nodeId);
        return (Node) getFactory().node(getBranch(), r2);
    }

    @Override
    public List<String> getTranslationEditions()
    {
        Response response = getRemote().get(getResourceUri() + "/i18n/editions");

        ArrayNode array = (ArrayNode) response.getObjectNode().get("editions");

        List<String> editions = new ArrayList<String>();
        for (int i = 0; i < array.size(); i++)
        {
            editions.add(array.get(i).textValue());
        }

        return editions;
    }

    @Override
    public List<Locale> getTranslationLocales(String edition)
    {
        Response response = getRemote().get(getResourceUri() + "/i18n/locales?edition=" + edition);

        ArrayNode array = (ArrayNode) response.getObjectNode().get("locales");

        List<Locale> locales = new ArrayList<Locale>();
        for (int i = 0; i < array.size(); i++)
        {
            String localeString = array.get(i).textValue();
            Locale locale = org.gitana.util.I18NUtil.parseLocale(localeString);

            locales.add(locale);

        }

        return locales;
    }

    @Override
    public Node readTranslation(Locale locale)
    {
        return readTranslation(null, locale);
    }

    @Override
    public Node readTranslation(String edition, Locale locale)
    {
        String uri = getResourceUri() + "/i18n?locale=" + locale.toString();
        if (edition != null)
        {
            uri += "&edition=" + edition;
        }

        Response response = getRemote().get(uri);
        return (Node) getFactory().node(getBranch(), response);
    }

    @Override
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, ObjectNode traverse)
    {
        return findNodes(query, searchTerm, traverse, (Pagination) null);
    }

    @Override
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, ObjectNode traverse, Pagination pagination)
    {
        return findNodes(query, searchTerm, traverse, LookupOptions.fromPagination(pagination));
    }

    @Override
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, ObjectNode traverse, LookupOptions options)
    {
        String uri = getResourceUri() + "/find";

        ObjectNode payload = JsonUtil.createObject();
        if (query != null)
        {
            payload.put("query", query);
        }
        if (searchTerm != null)
        {
            payload.put("search", searchTerm);
        }
        if (traverse != null)
        {
            payload.put("traverse", traverse);
        }

        Map<String, String> params = DriverUtil.params(options);

        Response response = getRemote().post(uri, params, payload);

        return getFactory().nodes(getBranch(), response);
    }

    @Override
    public ObjectNode fileFolderTree()
    {
        return fileFolderTree((String) null);
    }

    @Override
    public ObjectNode fileFolderTree(String basePath)
    {
        return fileFolderTree(basePath, null);
    }

    @Override
    public ObjectNode fileFolderTree(String basePath, String leafPath)
    {
        List<String> leafPaths = new ArrayList<String>();
        if (leafPath != null)
        {
            leafPaths.add(leafPath);
        }

        return fileFolderTree(basePath, -1, leafPaths, true, false, null);
    }

    @Override
    public ObjectNode fileFolderTree(String basePath, int depth, List<String> leafPaths, boolean includeProperties, boolean containersOnly, ObjectNode query)
    {
        TreeLookupOptions options = new TreeLookupOptions();
        if (basePath != null)
        {
            options.setBase(basePath);
        }
        if (leafPaths != null && leafPaths.size() > 0)
        {
            int lpSize = leafPaths.size();

            // construct a string of all paths
            String leafPathsParamString = "";
            for (int i = 0; i < lpSize; ++i)
            {
                leafPathsParamString += leafPaths.get(i);
                if (i < lpSize - 1)
                {
                   leafPathsParamString += ",";
                }
            }

            options.setLeafPathString(leafPathsParamString);
        }
        if (depth > -1)
        {
            options.setDepth(depth);
        }
        if (includeProperties)
        {
            options.setProperties(includeProperties);
        }
        if (containersOnly)
        {
            options.setContainers(containersOnly);
        }

        if (query != null)
        {
            options.setQuery(query);
        }

        return fileFolderTree(options);
    }

    @Override
    public ObjectNode fileFolderTree(TreeLookupOptions options)
    {
        String uri = getResourceUri() + "/tree";

        Map<String, String> params = DriverUtil.params(options);
        ObjectNode payload = options.getPayload();
        Response response = getRemote().post(uri, params, payload);

        return response.getObjectNode();
    }

    @Override
    public void move(String targetNodeId)
    {
        move(targetNodeId, null);
    }

    @Override
    public void move(String targetNodeId, String targetPath)
    {
        if (targetNodeId == null)
        {
            targetNodeId = "root";
        }

        Map<String, String> params = DriverUtil.params();
        params.put("targetNodeId", targetNodeId);

        if (targetPath != null)
        {
            params.put("targetPath", targetPath);
        }

        getRemote().post(getResourceUri() + "/move", params, JsonUtil.createObject());
    }


    @Override
    public String resolvePath()
    {
        Response response = getRemote().get(getResourceUri() + "/path");
        return JsonUtil.objectGetString(response.getObjectNode(), "path");
    }

    @Override
    public ObjectNode resolvePaths()
    {
        Response response = getRemote().get(getResourceUri() + "/paths");
        return JsonUtil.objectGetObject(response.getObjectNode(), "paths");
    }

    @Override
    public ResultMap<BaseNode> listChildren()
    {
        return listChildren(null);
    }

    @Override
    public ResultMap<BaseNode> listChildren(Pagination pagination)
    {
        String uri = getResourceUri() + "/children";

        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(uri, params);

        return getFactory().nodes(getBranch(), response);
    }

    @Override
    public ResultMap<BaseNode> listRelatives(QName type, Direction direction)
    {
        return listRelatives(type, direction, null);
    }

    @Override
    public ResultMap<BaseNode> listRelatives(QName type, Direction direction, Pagination pagination)
    {
        String uri = getResourceUri() + "/relatives";

        Map<String, String> params = DriverUtil.params(pagination);
        params.put("type", type.toString());

        if (direction != null)
        {
            params.put("direction", direction.toString());
        }

        Response response = getRemote().get(uri, params);

        return getFactory().nodes(getBranch(), response);
    }

    @Override
    public ResultMap<BaseNode> queryRelatives(QName type, Direction direction, ObjectNode query)
    {
        return queryRelatives(type, direction, query, null);
    }

    @Override
    public ResultMap<BaseNode> queryRelatives(QName type, Direction direction, ObjectNode query, Pagination pagination)
    {
        String uri = getResourceUri() + "/relatives/query";

        Map<String, String> params = DriverUtil.params(pagination);
        params.put("type", type.toString());

        if (direction != null)
        {
            params.put("direction", direction.toString());
        }

        Response response = getRemote().post(uri, params, query);

        return getFactory().nodes(getBranch(), response);
    }
}
