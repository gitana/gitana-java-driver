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
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.beans.TraversalResults;
import org.gitana.repo.client.services.Translations;
import org.gitana.repo.namespace.QName;

import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public interface Node extends BaseNode
{
    /**
     * @return translations service for this node
     */
    public Translations translations();

    /**
     * @return access control list
     */
    public ACL getACL();

    /**
     * Retrieve the authorities that a principal has.
     *
     * @param principalId
     * @return list
     */
    public List<String> getAuthorities(String principalId);

    /**
     * Grants an authority to a principal.
     *
     * @param principalId
     * @param authorityId
     */
    public void grant(String principalId, String authorityId);

    /**
     * Revokes an authority for a principal.
     *
     * @param principalId
     * @param authorityId
     */
    public void revoke(String principalId, String authorityId);

    /**
     * Revoke all authorities for a principal.
     *
     * @param principalId
     */
    public void revokeAll(String principalId);

    /**
     * Uploads the default attachment.
     *
     * @param bytes
     * @param contentType
     */
    public void uploadAttachment(byte[] bytes, String contentType);

    /**
     * Uploads an attachment.
     *
     * @param attachmentId
     * @param bytes
     * @param contentType
     */
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType);

    /**
     * Downloads the default attachment.
     *
     * @return attachment
     */
    public byte[] downloadAttachment();

    /**
     * Downloads an attachment.
     *
     * @param attachmentId
     *
     * @return attachment
     */
    public byte[] downloadAttachment(String attachmentId);

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
     * @param direction
     * @param associationTypeQName
     * @return map
     */
    public Map<String, Association> associations(Direction direction, QName associationTypeQName);

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
     * @param direction
     * @param associationTypeQName
     * @return association
     */
    public Association associate(Node otherNode, Direction direction, QName associationTypeQName);

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

}