/**
 * Copyright 2026 Gitana Software, Inc.
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
import org.gitana.platform.client.LookupOptions;
import org.gitana.util.JsonUtil;

import java.util.Map;

public class TreeLookupOptions extends LookupOptions
{
    private String base = null;
    private String leafPathString = null;
    private Integer depth = null;
    private Boolean properties = null;
    private Boolean options = null;
    private Boolean containers = null;

    private ObjectNode query = null;

    public String getBase()
    {
        return base;
    }

    public void setBase(String base)
    {
        this.base = base;
    }

    public String getLeafPathString()
    {
        return leafPathString;
    }

    public void setLeafPathString(String leafPathString)
    {
        this.leafPathString = leafPathString;
    }

    public Integer getDepth()
    {
        return depth;
    }

    public void setDepth(Integer depth)
    {
        this.depth = depth;
    }

    public Boolean getProperties()
    {
        return properties;
    }

    public void setProperties(Boolean properties)
    {
        this.properties = properties;
    }

    public Boolean getOptions()
    {
        return options;
    }

    public void setOptions(Boolean options)
    {
        this.options = options;
    }

    public Boolean getContainers()
    {
        return containers;
    }

    public void setContainers(Boolean containers)
    {
        this.containers = containers;
    }

    public ObjectNode getQuery()
    {
        return query;
    }

    public void setQuery(ObjectNode query)
    {
        this.query = query;
    }

    @Override
    public Map<String, String> params()
    {
        Map<String, String> params = super.params();

        if (base != null)
        {
            params.put("base", base);
        }

        if (leafPathString != null)
        {
            params.put("leaf", leafPathString);
        }

        if (depth != null)
        {
            params.put("depth", String.valueOf(depth));
        }

        if (properties != null)
        {
            params.put("properties", String.valueOf(properties));
        }

        if (options != null)
        {
            params.put("options", String.valueOf(options));
        }

        if (containers != null)
        {
            params.put("containers", String.valueOf(containers));
        }

        return params;
    }

    public ObjectNode getPayload()
    {
        ObjectNode payload = JsonUtil.createObject();
        if (query != null)
        {
            payload.set("query", query);
        }

        return payload;
    }
}
