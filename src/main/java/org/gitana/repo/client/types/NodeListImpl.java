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

package org.gitana.repo.client.types;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Driver;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.nodes.BaseNode;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.nodes.NodeImpl;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.namespace.QName;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.ResultMap;
import org.gitana.repo.support.ResultMapImpl;
import org.gitana.util.JsonUtil;

import java.util.Map;

/**
 * @author uzi
 */
public class NodeListImpl extends NodeImpl implements NodeList
{
    public NodeListImpl(Driver driver, Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(driver, branch, obj, isSaved);
    }

    @Override
    public String getListKey()
    {
        return getString(FIELD_LIST_KEY);
    }

    @Override
    public void setListKey(String listKey)
    {
        set(FIELD_LIST_KEY, listKey);
    }

    @Override
    public QName getListItemType()
    {
        QName qname = null;

        try
        {
            qname = QName.create(getString(FIELD_LIST_ITEM_TYPE));
        }
        catch (Exception ex) { }

        return qname;
    }

    @Override
    public void setListItemType(QName listItemType)
    {
        set(FIELD_LIST_ITEM_TYPE, listItemType.toString());
    }

    @Override
    public Node addItem(ObjectNode object)
    {
        String uri = "/repositories/" + this.getRepositoryId() + "/branches/" + getBranchId() + "/lists/" + getListKey() + "/items";

        Response r1 = getRemote().post(uri, object);

        String nodeId = r1.getId();

        // read it back
        Response r2 = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + nodeId);

        return (Node) getFactory().node(getBranch(), r2);
    }

    @Override
    public ResultMap<Node> listItems()
    {
        return listItems(null);
    }

    @Override
    public ResultMap<Node> listItems(Pagination pagination)
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/lists/" + getListKey() + "/items");

        ResultMap<BaseNode> baseNodes = getFactory().nodes(this.getBranch(), response);
        ResultMap<Node> nodes = new ResultMapImpl<Node>(baseNodes.offset(), baseNodes.totalRows());
        for (BaseNode baseNode: baseNodes.values())
        {
            nodes.put(baseNode.getId(), (Node) baseNode);
        }

        return nodes;
    }

    @Override
    public ResultMap<Node> queryItems(ObjectNode query)
    {
        return queryItems(query, null);
    }

    @Override
    public ResultMap<Node> queryItems(ObjectNode query, Pagination pagination)
    {
        String uri = "/repositories/" + this.getRepositoryId() + "/branches/" + getBranchId() + "/lists/" + getListKey() + "/items/query";

        if (query == null)
        {
            query = JsonUtil.createObject();
        }

        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(uri, params, query);

        ResultMap<BaseNode> baseNodes = getFactory().nodes(this.getBranch(), response);
        ResultMap<Node> nodes = new ResultMapImpl<Node>(baseNodes.offset(), baseNodes.totalRows());
        for (BaseNode baseNode: baseNodes.values())
        {
            nodes.put(baseNode.getId(), (Node) baseNode);
        }

        return nodes;
    }

}
