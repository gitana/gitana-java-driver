/**
 * Copyright 2022 Gitana Software, Inc.
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
package org.gitana.platform.client.support;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps a JSON response from the Gitana server.
 * 
 * @author uzi
 */
public class Response
{
    public final static String FIELD_OK = "ok";
    public final static String FIELD_ERROR = "error";
    public final static String FIELD_TOTAL_ROWS = "total_rows";
    public final static String FIELD_ROWS = "rows";
    public final static String FIELD_OFFSET = "offset";
    public final static String FIELD_MESSAGE = "message";
    public final static String FIELD_STACKTRACE = "stacktrace";

    private ObjectNode object;
    private Map<String, String> headers = new HashMap<String, String>();

    public Response(ObjectNode object, Map<String, String> headers)
    {
        this.object = object;
        this.headers = headers;
    }

    public ObjectNode getObjectNode()
    {
        return this.object;
    }

    public Map<String, String> getHeaders()
    {
        return headers;
    }

    public String getHeader(String name)
    {
        return getHeaders().get(name);
    }

    public boolean isStatusDocument()
    {
        return ((getObjectNode().has(FIELD_OK)) || (getObjectNode().has(FIELD_ERROR)));
    }

    public boolean isListDocument()
    {
        return ((getObjectNode().has(FIELD_TOTAL_ROWS)) && (getObjectNode().has(FIELD_ROWS)) && getObjectNode().has(FIELD_OFFSET));
    }

    public boolean isDataDocument()
    {
        return (!isStatusDocument() && !isListDocument());
    }

    public String getId()
    {
        return this.object.get("_doc").textValue();
    }



    //////////////////////////////////////////////////
    //
    // DATA DOCUMENT ACCESSORS
    //
    //////////////////////////////////////////////////

    /*
    public GitanaObject getObjectNode()
    {

    }
    */




    //////////////////////////////////////////////////
    //
    // LIST DOCUMENT ACCESSORS
    //
    //////////////////////////////////////////////////

    public int getListTotalRows()
    {
        int totalRows = -1;

        if (getObjectNode().has(FIELD_TOTAL_ROWS))
        {
            totalRows = getObjectNode().get(FIELD_TOTAL_ROWS).intValue();
        }

        return totalRows;
    }

    public int getListOffset()
    {
        int offset = -1;

        if (getObjectNode().has(FIELD_OFFSET))
        {
            offset = getObjectNode().get(FIELD_OFFSET).intValue();
        }

        return offset;
    }

    public List<ObjectNode> getObjectNodes()
    {
        List<ObjectNode> objectNodes = new ArrayList<ObjectNode>();

        ArrayNode arrayNode = (ArrayNode) getObjectNode().get(FIELD_ROWS);
        if (arrayNode != null)
        {
            for (int i = 0; i < arrayNode.size(); i++)
            {
                ObjectNode objectNode = (ObjectNode) arrayNode.get(i);
                objectNodes.add(objectNode);
            }
        }

        return objectNodes;
    }

    /*
    public List<GitanaObject> getObjects()
    {

    }
    */




    //////////////////////////////////////////////////
    //
    // STATUS DOCUMENT ACCESSORS
    //
    //////////////////////////////////////////////////

    public boolean isOk()
    {
        // assume things are ok
        boolean ok = true;

        // status documents report their ok in an "ok" field
        if (isStatusDocument())
        {
            if (getObjectNode().has(FIELD_OK))
            {
                ok = getObjectNode().get(FIELD_OK).booleanValue();
            }
        }

        // any document type can specify an error
        if (getObjectNode().has(FIELD_ERROR))
        {
            ok = false;
        }

        return ok;
    }

    public boolean isError()
    {
        return !isOk();
    }

    public String getMessage()
    {
        String message = null;

        if (getObjectNode().has(FIELD_MESSAGE))
        {
            message = getObjectNode().get(FIELD_MESSAGE).textValue();
        }

        return message;
    }

    public String getStackTrace()
    {
        String stacktrace = null;

        if (getObjectNode().has(FIELD_STACKTRACE))
        {
            stacktrace = getObjectNode().get(FIELD_STACKTRACE).textValue();
        }

        return stacktrace;        
    }

}
