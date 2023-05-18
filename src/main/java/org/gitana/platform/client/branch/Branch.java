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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.http.HttpPayload;
import org.gitana.platform.client.deletion.Deletion;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.node.type.*;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.repository.RepositoryDocument;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.services.branch.BranchType;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;

import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public interface Branch extends RepositoryDocument, AccessControllable, Selfable
{
    // properties
    public final static String FIELD_ROOT = "root";
    public final static String FIELD_TIP = "tip";
    public final static String FIELD_READONLY = "readonly";
    public final static String FIELD_SNAPSHOT = "snapshot";
    public final static String FIELD_FROZEN = "frozen";
    public final static String FIELD_JOIN_BRANCH = "join-branch";
    public final static String FIELD_ROOT_BRANCH = "root-branch";
    public final static String FIELD_BRANCH_TYPE = "type";

    // root
    public void setRootChangesetId(String rootChangesetId);
    public String getRootChangesetId();

    // current
    public void setTipChangesetId(String tipChangesetId);
    public String getTipChangesetId();

    public boolean isReadOnly();
    public boolean isSnapshot();

    public boolean isFrozen();

    // helpers
    public String getJoinBranchId();
    public String getRootBranchId();

    public boolean isMaster();
    public BranchType getType();

    public void markDirty();



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // NODES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves the mount nodes for this branch.
     *
     * @return a map of node objects keyed by node id
     */
    public ResultMap<Node> listNodes();

    /**
     * Retrieves the mount nodes for this branch.
     *
     * @param pagination
     *
     * @return a map of node objects keyed by node id
     */
    public ResultMap<Node> listNodes(Pagination pagination);

    /**
     * Reads a single node from the branch.
     *
     * @param nodeId
     *
     * @return node
     */
    public BaseNode readNode(String nodeId);

    /**
     * Reads a single node from the brancnh.
     *
     * @param nodeId
     * @param path
     *
     * @return node
     */
    public BaseNode readNode(String nodeId, String path);

    /**
     * Creates an empty node on the branch.
     *
     * @return branch
     */
    public BaseNode createNode();

    /**
     * Creates an empty node on the branch.
     *
     * @param typeQName
     *
     * @return branch
     */
    public BaseNode createNode(QName typeQName);

    /**
     * Creates a node on the branch.
     *
     * @param object
     *
     * @return node
     */
    public BaseNode createNode(ObjectNode object);

    /**
     * Creates multiple nodes from binary payloads (single attachment each).
     *
     * @param params
     * @param payloads
     * @return
     */
    public Map<String, BaseNode> createNodes(Map<String, String> params, HttpPayload... payloads);

    /**
     * Performs a query for nodes.
     *
     * @param query
     * @return map of nodes
     */
    public ResultMap<BaseNode> queryNodes(ObjectNode query);

    /**
     * Performs a query for nodes.
     *
     * @param query
     * @param pagination
     *
     * @return map of nodes
     */
    public ResultMap<BaseNode> queryNodes(ObjectNode query, Pagination pagination);

    /**
     * Full-text search
     *
     * @param text
     * @return map of nodes
     */
    public ResultMap<BaseNode> searchNodes(String text);

    /**
     * Full-text search
     *
     * @param text
     * @param pagination
     * @return map of nodes
     */
    public ResultMap<BaseNode> searchNodes(String text, Pagination pagination);

    /**
     * Search
     *
     * @param searchObject
     * @return map of nodes
     */
    public ResultMap<BaseNode> searchNodes(ObjectNode searchObject);

    /**
     * Search
     *
     * @param searchObject
     * @param pagination
     * @return map of nodes
     */
    public ResultMap<BaseNode> searchNodes(ObjectNode searchObject, Pagination pagination);

    /**
     * Delete multiple nodes
     * @param nodeIds
     * @return list of node ids deleted
     */
    public List<String> deleteNodes(List<String> nodeIds);


    /**
     * Moves a list of nodes to a configured target node
     * @param sourceNodeIds list of nodes to move
     * @param targetNodeId id of target folder, default is "root" for top folder
     * @param targetPath optional relative path to apply to targetNodeId for determining move target
     */
    public void moveNodes(List<String> sourceNodeIds, String targetNodeId, String targetPath);
    public void moveNodes(List<String> sourceNodeIds, String targetNodeId);

    public Node rootNode();

    /**
     * Check node permissions.
     *
     * @param list
     * @return
     */
    public PermissionCheckResults checkNodePermissions(List<PermissionCheck> list);

    /**
     * Reads the person object for a security user.
     *
     * @param userId  userId
     * @param createIfNotFound whether to create the person object if it isn't found
     * @return Person node
     */
    public Person readPerson(String userId, boolean createIfNotFound);

    /**
     *  Reads the group object for a security group.
     *
     * @param groupId groupd ID
     * @param createIfNotFound whether to create the group object if it isn't found
     * @return Group node
     */
    public Group readGroup(String groupId, boolean createIfNotFound);



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DEFINITIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves definitions for the branch
     *
     * @return a map of node objects keyed by node id
     */
    public Map<QName, Definition> listDefinitions();

    public Map<QName, Definition> listDefinitions(DefinitionFilterType filter, boolean custom, boolean system, Pagination pagination);

    /**
     * Reads a definition
     *
     * @param qname
     *
     * @return node
     */
    public Definition readDefinition(QName qname);

    /**
     * Creates a type definition.
     *
     * @return node
     */
    public TypeDefinition defineType(QName definitionQName);

    /**
     * Creates a type definition.
     *
     * @param definitionQName
     * @param object
     * @return
     */
    public TypeDefinition defineType(QName definitionQName, ObjectNode object);

    /**
     * Creates an association type definition.
     *
     * @param definitionQName
     * @return
     */
    public AssociationDefinition defineAssociationType(QName definitionQName);

    /**
     * Creates an association type definition.
     *
     * @param definitionQName
     * @param object
     * @return
     */
    public AssociationDefinition defineAssociationType(QName definitionQName, ObjectNode object);



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // FIND
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Queries and searches for nodes.
     *
     * @param query
     * @param searchTerm
     *
     * @return
     */
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm);

    /**
     * Queries and searches for nodes.
     *
     * @param query
     * @param searchTerm
     * @param pagination
     *
     * @return
     */
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, Pagination pagination);



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // NODE LISTS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new node list.
     *
     * @param listKey
     * @param itemTypeQName
     * @return node list
     */
    public NodeList createList(String listKey, QName itemTypeQName);

    /**
     * Reads a node list.
     *
     * @param listKey
     * @return node list
     */
    public NodeList readList(String listKey);


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DELETIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reads a deletion by node ID.
     *
     * @param nodeId
     * @return deletion
     */
    public Deletion readDeletion(String nodeId);

    /**
     * Queries for deletions.
     *
     * @param query
     * @return result map of deletions
     */
    public ResultMap<Deletion> queryDeletions(ObjectNode query);

    /**
     * Queries for deletions.
     *
     * @param query
     * @param pagination
     * @return result map of deletions
     */
    public ResultMap<Deletion> queryDeletions(ObjectNode query, Pagination pagination);

    /**
     * Purges all deletions for the current branch.
     */
    public void purgeAllDeletions();

    /**
     * Performs a graphql query
     * @param query
     * @return
     */
    public ObjectNode graphqlQuery(String query);

    /**
     * Performs a graphql query
     * @param query
     * @param operationName
     * @param variables
     * @return query results json
     */
    public ObjectNode graphqlQuery(String query, String operationName, Map<String, Object> variables);

    /**
     * Finds the graphql schema for types on this branch
     * @return schema string
     */
    public String graphqlSchema();

    public Job startCopyFrom(String sourceRepositoryId, String sourceBranchId, List<String> nodeIds, ObjectNode config);
}
