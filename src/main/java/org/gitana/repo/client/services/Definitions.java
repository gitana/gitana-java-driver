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

package org.gitana.repo.client.services;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.types.AssociationDefinition;
import org.gitana.repo.client.types.TypeDefinition;
import org.gitana.repo.namespace.QName;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class Definitions extends AbstractService
{
    private Branch branch;

    public Definitions(Gitana gitana, Branch branch)
    {
        super(gitana);

        this.branch = branch;
    }

    public Repository getRepository()
    {
        return getBranch().getRepository();
    }

    public String getRepositoryId()
    {
        return getRepository().getId();
    }

    public Branch getBranch()
    {
        return this.branch;
    }

    public String getBranchId()
    {
        return getBranch().getId();
    }

    /**
     * Retrieves definitions for the branch
     *
     * @return a map of node objects keyed by node id
     */
    public Map<QName, Node> map()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/definitions");

        Map<String, Node> nodes = getFactory().nodes(getBranch(), response);

        Map<QName, Node> definitions = new HashMap<QName, Node>();
        for (Node node: nodes.values())
        {
            definitions.put(node.getQName(), node);
        }

        return definitions;
    }

    /**
     * Retrieves definitions for the branch.
     *
     * @return list of nodes
     */
    public List<Node> list()
    {
        Map<QName, Node> map = map();

        List<Node> list = new ArrayList<Node>();
        for (Node node : map.values())
        {
            list.add(node);
        }

        return list;
    }


    /**
     * Reads a definition
     *
     * @param qname
     *
     * @return node
     */
    public Node read(QName qname)
    {
        Node node = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/definitions/" + qname.toString());
            node = getFactory().node(getBranch(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return node;
    }

    /**
     * Creates a type definition.
     *
     * @return node
     */
    public TypeDefinition defineType(QName definitionQName)
    {
        return defineType(definitionQName, JsonUtil.createObject());
    }

    /**
     * Creates a type definition.
     *
     * @param definitionQName
     * @param object
     * @return
     */
    public TypeDefinition defineType(QName definitionQName, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put(Node.FIELD_QNAME, definitionQName.toString());
        object.put(Node.FIELD_TYPE_QNAME, "d:type");
        object.put("type", "object");
        object.put("description", definitionQName.toString());

        return (TypeDefinition) getBranch().nodes().create(object);
    }

    /**
     * Creates an association type definition.
     *
     * @param definitionQName
     * @return
     */
    public AssociationDefinition defineAssociationType(QName definitionQName)
    {
        return defineAssociationType(definitionQName, JsonUtil.createObject());
    }

    /**
     * Creates an association type definition.
     *
     * @param definitionQName
     * @param object
     * @return
     */
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

        return (AssociationDefinition) getBranch().nodes().create(object);

    }
}
