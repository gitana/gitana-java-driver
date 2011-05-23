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

package org.gitana.repo.client;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.binary.BinaryObject;
import org.gitana.repo.branch.BranchType;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.types.AssociationDefinition;
import org.gitana.repo.client.types.TypeDefinition;
import org.gitana.repo.namespace.QName;

import java.io.InputStream;
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
    public Map<String, Node> fetchNodes();

    /**
     * Retrieves the mount nodes for this branch.
     *
     * @return list of repositories
     */
    public List<Node> listNodes();

    /**
     * Reads a single node from the branch.
     *
     * @param nodeId
     *
     * @return node
     */
    public Node readNode(String nodeId);

    /**
     * Creates an empty node on the branch.
     *
     * @return branch
     */
    public Node createNode();

    /**
     * Creates an empty node on the branch.
     *
     * @param typeQName
     *
     * @return branch
     */
    public Node createNode(QName typeQName);

    /**
     * Creates a node on the branch.
     *
     * @param object
     *
     * @return node
     */
    public Node createNode(ObjectNode object);

    /**
     * Performs a query for nodes.
     *
     * @param query
     * @return map of nodes
     */
    public Map<String, Node> queryNodes(ObjectNode query);

    /**
     * Full-text search
     *
     * @param text
     * @return map of nodes
     */
    public Map<String, Node> searchNodes(String text);

    /**
     * Reads the person object for a security user.
     *
     * @param userId  userId
     * @param createIfNotFound whether to create the person object if it isn't found
     * @return Person node
     */
    public Node readPerson (String userId, boolean createIfNotFound);

    /**
     *  Reads the group object for a security group.
     *
     * @param groupId groupd ID
     * @param createIfNotFound whether to create the group object if it isn't found
     * @return Group node
     */
    public Node readGroup (String groupId, boolean createIfNotFound);


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
    public Map<QName, Node> fetchDefinitions();

    /**
     * Retrieves definitions for the branch.
     *
     * @return list of nodes
     */
    public List<Node> listDefinitions();

    /**
     * Reads a definition
     *
     * @param qname
     *
     * @return node
     */
    public Node readDefinition(QName qname);

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
    // IMPORT/EXPORT
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Export the HEAD of this branch as a publication archive.
     *
     * @return publication zip file
     *
     * @throws Exception
     */
    public BinaryObject exportPublication();

    /**
     * Imports a publication archive into the branch.
     *
     * @parma binary
     * @return
     * @throws Exception
     */
    public void importPublication(BinaryObject binary);

}
