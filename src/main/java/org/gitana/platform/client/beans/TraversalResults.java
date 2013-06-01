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

package org.gitana.platform.client.beans;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.node.Association;
import org.gitana.platform.client.node.Node;

import java.util.Iterator;
import java.util.Map;

/**
 * @author uzi
 */
public class TraversalResults
{
    private ResultMap<Node> nodes;
    private ResultMap<Association> associations;

    public TraversalResults()
    {
        this.nodes = new ResultMapImpl<Node>();
        this.associations = new ResultMapImpl<Association>();
    }

    public void parse(ObjectFactory factory, Branch branch, Response response)
    {
        // NODES
        ObjectNode nodes = (ObjectNode) response.getObjectNode().get("nodes");
        Iterator<String> it1 = nodes.fieldNames();
        while (it1.hasNext())
        {
            String fieldName = it1.next();

            ObjectNode obj = (ObjectNode) nodes.get(fieldName);

            Node node = (Node) factory.produce(branch, obj, true);
            this.nodes.put(node.getId(), node);
        }

        // ASSOCIATIONS
        ObjectNode associations = (ObjectNode) response.getObjectNode().get("associations");
        Iterator<String> it2 = associations.fieldNames();
        while (it2.hasNext())
        {
            String fieldName = it2.next();

            ObjectNode obj = (ObjectNode) associations.get(fieldName);

            Association association = (Association) factory.produce(branch, obj, true);
            this.associations.put(association.getId(), association);
        }
    }

    public void setNodes(Map<String, Node> nodes)
    {
        this.nodes.clear();
        this.nodes.putAll(nodes);
    }

    public ResultMap<Node> getNodes()
    {
        return nodes;
    }

    public void setAssociations(Map<String, Association> associations)
    {
        this.associations.clear();
        this.associations.putAll(associations);
    }

    public ResultMap<Association> getAssociations()
    {
        return associations;
    }
}