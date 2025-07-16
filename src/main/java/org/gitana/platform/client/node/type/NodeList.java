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
package org.gitana.platform.client.node.type;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.client.node.Node;

/**
 * @author uzi
 */
public interface NodeList extends Node
{
    public final static QName QNAME = QName.create("n:list");

    public final static String FIELD_LIST_KEY = "listKey";
    public final static String FIELD_LIST_ITEM_TYPE = "listItemType";

    public String getListKey();

    public void setListKey(String listKey);

    public QName getListItemType();

    public void setListItemType(QName listItemType);

    /**
     * Adds an item
     *
     * @param object
     * @return
     */
    public Node addItem(ObjectNode object);

    /**
     * Lists items
     *
     * @return
     */
    public ResultMap<Node> listItems();

    /**
     * Lists items
     *
     * @param pagination
     * @return
     */
    public ResultMap<Node> listItems(Pagination pagination);

    /**
     * Queries for items
     *
     * @param query
     * @return
     */
    public ResultMap<Node> queryItems(ObjectNode query);

    /**
     * Queries for items.
     *
     * @param query
     * @param pagination
     * @return
     */
    public ResultMap<Node> queryItems(ObjectNode query, Pagination pagination);

}