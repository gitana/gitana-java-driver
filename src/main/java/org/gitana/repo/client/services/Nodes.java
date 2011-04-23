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
import org.gitana.util.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class Nodes extends AbstractService
{
    private Branch branch;

    public Nodes(Gitana gitana, Branch branch)
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
     * Retrieves nodes for the branch
     *
     * @return a map of node objects keyed by node id
     */
    public Map<String, Node> map()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes");

        return getFactory().nodes(getBranch(), response);
    }

    /**
     * Retrieves nodes for the branch.
     *
     * @return list of repositories
     */
    public List<Node> list()
    {
        Map<String, Node> map = map();

        List<Node> list = new ArrayList<Node>();
        for (Node node : map.values())
        {
            list.add(node);
        }

        return list;
    }


    /**
     * Reads a single node from the branch.
     *
     * @param nodeId
     *
     * @return node
     */
    public Node read(String nodeId)
    {
        Node node = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + nodeId);
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
     * Creates an empty node on the branch.
     *
     * @return branch
     */
    public Node create()
    {
        return create(JsonUtil.createObject());
    }

    /**
     * Creates a node on the branch.
     *
     * @param object
     *
     * @return node
     */
    public Node create(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes", object);

        String nodeId = response.getId();
        Node node = read(nodeId);

        // mark the branch as being dirty (since the tip will have moved)
        this.getBranch().markDirty();

        return node;
    }

    /**
     * Performs a query for nodes.
     *
     * @param query
     * @return map of nodes
     */
    public Map<String, Node> query(ObjectNode query)
    {
        Response response = getRemote().post("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/query", query);

        return getFactory().nodes(getBranch(), response);
    }

    /**
     * Full-text search
     *
     * @param text
     * @return map of nodes
     */
    public Map<String, Node> search(String text)
    {
        // url encode the text
        try
        {
            text = URLEncoder.encode(text, "utf-8");
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new RuntimeException(uee);
        }

        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/search?text=" + text);

        return getFactory().nodes(getBranch(), response);
    }


}
