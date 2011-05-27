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

package org.gitana.repo.client.beans;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.ObjectFactory;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.nodes.Association;
import org.gitana.repo.client.nodes.Node;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class TraversalResults
{
    private Map<String, Node> nodes;
    private Map<String, Association> associations;

    public TraversalResults()
    {
        this.nodes = new LinkedHashMap<String, Node>();
        this.associations = new LinkedHashMap<String, Association>();
    }

    public void parse(ObjectFactory factory, Branch branch, Response response)
    {
        // NODES
        ObjectNode nodes = (ObjectNode) response.getObjectNode().get("nodes");
        Iterator<String> it1 = nodes.getFieldNames();
        while (it1.hasNext())
        {
            String fieldName = it1.next();

            ObjectNode obj = (ObjectNode) nodes.get(fieldName);

            Node node = (Node) factory.produce(branch, obj, true);
            this.nodes.put(node.getId(), node);
        }

        // ASSOCIATIONS
        ObjectNode associations = (ObjectNode) response.getObjectNode().get("associations");
        Iterator<String> it2 = associations.getFieldNames();
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
        this.nodes.clear();;
        this.nodes.putAll(nodes);
    }

    public Map<String, Node> getNodes()
    {
        return Collections.unmodifiableMap(this.nodes);
    }

    public void setAssociations(Map<String, Association> associations)
    {
        this.associations.clear();
        this.associations.putAll(associations);
    }

    public Map<String, Association> getAssociations()
    {
        return Collections.unmodifiableMap(this.associations);
    }
}