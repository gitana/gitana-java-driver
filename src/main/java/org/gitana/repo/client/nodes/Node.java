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
import org.gitana.repo.association.Directionality;
import org.gitana.repo.client.AccessControllable;
import org.gitana.repo.client.Attachable;
import org.gitana.repo.client.beans.TraversalResults;
import org.gitana.repo.namespace.QName;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author uzi
 */
public interface Node extends BaseNode, AccessControllable, Attachable
{
    /**
     * Lists all associations (both directions) involving this node.
     *
     * @return map
     */
    public Map<String, Association> associations();

    /**
     * Lists all directed associations involving this node.
     *
     * @param direction
     *
     * @return map
     */
    public Map<String, Association> associations(Direction direction);

    /**
     * Lists all associations of the given type in both directions.
     *
     * @param associationTypeQName
     * @return map
     */
    public Map<String, Association> associations(QName associationTypeQName);

    /**
     * Lists all associations of the given type in both directions.
     *
     * @param associationTypeQName
     * @param direction
     * @return map
     */
    public Map<String, Association> associations(QName associationTypeQName, Direction direction);

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
     * Associates this node with another.
     *
     * @param otherNode
     * @param associationTypeQName
     * @param directionality
     * @return association
     */
    public Association associate(Node otherNode, QName associationTypeQName, Directionality directionality);

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
     * Mounts this node.
     *
     * @param mountKey
     */
    public void mount(String mountKey);

    /**
     * Unmounts this node.
     */
    public void unmount();


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

}