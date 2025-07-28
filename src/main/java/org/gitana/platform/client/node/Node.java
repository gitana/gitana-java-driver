/**
 * Copyright 2025 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.beans.TraversalResults;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.Copyable;
import org.gitana.platform.client.support.Transferable;
import org.gitana.platform.services.association.Direction;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;

import java.util.List;
import java.util.Locale;

/**
 * @author uzi
 */
public interface Node extends BaseNode, AccessControllable, Transferable, Copyable
{
    /**
     * Lists all associations in ANY direction involving this node.
     *
     * @return map
     */
    public ResultMap<Association> associations();

    /**
     * Lists all associations in ANY direction involving this node.
     *
     * @param pagination
     *
     * @return map
     */
    public ResultMap<Association> associations(Pagination pagination);

    /**
     * Lists all association in the given direction involving this node.
     *
     * @param direction
     *
     * @return map
     */
    public ResultMap<Association> associations(Direction direction);

    /**
     * Lists all association in the given direction involving this node.
     *
     * @param direction
     * @param pagination
     *
     * @return map
     */
    public ResultMap<Association> associations(Direction direction, Pagination pagination);

    /**
     * Lists all associations of the given type in ANY direction involving this node.
     *
     * @param associationTypeQName
     * @return map
     */
    public ResultMap<Association> associations(QName associationTypeQName);

    /**
     * Lists all associations of the given type in ANY direction involving this node.
     *
     * @param associationTypeQName
     * @param pagination
     *
     * @return map
     */
    public ResultMap<Association> associations(QName associationTypeQName, Pagination pagination);

    /**
     * Lists all associations of the given type in the specified direction.
     *
     * @param associationTypeQName
     * @param direction
     * @return map
     */
    public ResultMap<Association> associations(QName associationTypeQName, Direction direction);

    /**
     * Lists all associations of the given type in the specified direction.
     *
     * @param associationTypeQName
     * @param direction
     * @param pagination
     *
     * @return map
     */
    public ResultMap<Association> associations(QName associationTypeQName, Direction direction, Pagination pagination);

    /**
     * Associates a target node to this source node.
     *
     * The direction is OUTGOING from this node to the specified node.
     *
     * @param targetNode
     * @param associationTypeQName
     * @return association
     */
    public Association associate(Node targetNode, QName associationTypeQName);

    /**
     * Associates a target node to this source node.
     *
     * The direction is OUTGOING from this node to the specified node.
     *
     * @param targetNode
     * @param associationTypeQName
     * @param object
     * @return association
     */
    public Association associate(Node targetNode, QName associationTypeQName, ObjectNode object);

    /**
     * Associates this node with another.
     *
     * @param otherNode
     * @param associationTypeQName
     * @param directionality
     * @return association
     */
    public Association associate(Node otherNode, QName associationTypeQName, Directionality directionality);

    /**
     * Associates this node with another.
     *
     * @param otherNode
     * @param associationTypeQName
     * @param directionality
     * @param object
     * @return association
     */
    public Association associate(Node otherNode, QName associationTypeQName, Directionality directionality, ObjectNode object);

    /**
     * Unassociates a target node from this node.
     *
     * @param targetNode
     * @param associationTypeQName
     */
    public void unassociate(Node targetNode, QName associationTypeQName);

    /**
     * Unassociates this node from another node.
     *
     * @param otherNode
     * @param associationTypeQName
     * @param directionality
     */
    public void unassociate(Node otherNode, QName associationTypeQName, Directionality directionality);

    /**
     * Runs a traversal around this node using the given configuration.
     *
     * @param traverse
     * @return
     */
    public TraversalResults traverse(ObjectNode traverse);

    /**
     * Moves the node to a configured target node
     * @param targetNodeId id of target folder, default is "root" for top folder
     * @param targetPath optional relative path to apply to targetNodeId for determining move target
     */
    public void move(String targetNodeId, String targetPath);
    public void move(String targetNodeId);

    /**
     * Resolves the path for the node relative to the root directory.
     * @return the path
     */
    public String resolvePath();

    /**
     * Resolves all of the paths for the node.
     * @return paths keyed by root identifier
     */
    public ObjectNode resolvePaths();

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MOUNTS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Mounts this node.
     *
     * @param mountKey
     */
    public void mount(String mountKey);

    /**
     * Unmounts this node.
     */
    public void unmount();


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TRANSLATIONS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a new translation.
     *
     * @param edition the edition
     * @param locale the locale
     * @param object the json object
     *
     * @return node
     */
    public Node createTranslation(String edition, Locale locale, ObjectNode object);

    /**
     * @return the editions available for this master node
     */
    public List<String> getTranslationEditions();

    /**
     * Hands back the locales available for a given edition of translations for this master node.
     *
     * @param edition
     * @return
     */
    public List<Locale> getTranslationLocales(String edition);

    /**
     * Reads a translation for the tip edition of the master node in the given locale.
     *
     * @param locale
     * @return
     */
    public Node readTranslation(Locale locale);

    /**
     * Reads a translation for the specified edition of the master node in the given locale.
     *
     * @param edition
     * @param locale
     * @return
     */
    public Node readTranslation(String edition, Locale locale);

    /**
     * Queries and searches for nodes in the defined traversal space around this node.
     *
     * @param query
     * @param searchTerm
     * @param traverse
     * @return
     */
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, ObjectNode traverse);

    /**
     * Queries and searches for nodes in the defined traversal space around this node.
     *
     * @param query
     * @param searchTerm
     * @param traverse
     * @param pagination
     *
     * @return
     */
    public ResultMap<BaseNode> findNodes(ObjectNode query, String searchTerm, ObjectNode traverse, Pagination pagination);

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TREE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ObjectNode fileFolderTree();
    public ObjectNode fileFolderTree(String basePath);
    public ObjectNode fileFolderTree(String basePath, String leafPath);
    public ObjectNode fileFolderTree(String basePath, int depth, List<String> leafPaths, boolean includeProperties, boolean containersOnly, ObjectNode query);

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CHILDREN
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves the children of the current node.
     *
     * @return a map of node objects keyed by node id
     */
    public ResultMap<BaseNode> listChildren();

    /**
     * Retrieves the children of the current node.
     *
     * @param pagination
     *
     * @return a map of node objects keyed by node id
     */
    public ResultMap<BaseNode> listChildren(Pagination pagination);

    /**
     * Retrieves the relatives of the current node.
     *
     * @param type
     * @param direction
     *
     * @return a map of node objects keyed by node id
     */
    public ResultMap<BaseNode> listRelatives(QName type, Direction direction);

    /**
     * Retrieves the relatives of the current node.
     *
     * @param type
     * @param direction
     * @param pagination
     *
     * @return a map of node objects keyed by node id
     */
    public ResultMap<BaseNode> listRelatives(QName type, Direction direction, Pagination pagination);

    /**
     * Queries the relatives of the current node.
     *
     * @param type
     * @param direction
     * @param query
     * @return map of nodes
     */
    public ResultMap<BaseNode> queryRelatives(QName type, Direction direction, ObjectNode query);

    /**
     * Queries the relatives of the current node.
     *
     * @param type
     * @param direction
     * @param query
     * @param pagination
     *
     * @return map of nodes
     */
    public ResultMap<BaseNode> queryRelatives(QName type, Direction direction, ObjectNode query, Pagination pagination);


}