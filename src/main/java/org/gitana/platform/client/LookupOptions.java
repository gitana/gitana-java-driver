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
 *   info@cloudcms.com
 */
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.support.Pagination;
import org.gitana.util.JsonUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LookupOptions
{
    private Pagination pagination = null;
    private Locale locale = null;
    private Boolean paths = null;
    private String path = null;

    public LookupOptions()
    {

    }

    public void setPagination(Pagination pagination)
    {
        this.pagination = pagination;
    }

    public Pagination getPagination()
    {
        return pagination;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public Boolean getPaths()
    {
        return paths;
    }

    public void setPaths(Boolean paths)
    {
        this.paths = paths;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public Map<String, String> params()
    {
        Map<String, String> params = new HashMap<>();

        Pagination pagination = getPagination();
        if (pagination != null)
        {
            // sorting
            if (pagination.getSorting().size() > 0)
            {
                String sort = JsonUtil.stringify((ObjectNode) pagination.getSorting().toJSON(), false);
                //sort = sort.replace("\"", "");
                params.put("sort", sort);
            }

            // skip
            if (pagination.hasSkip())
            {
                params.put("skip", String.valueOf(pagination.getSkip()));
            }

            // limit
            if (pagination.hasLimit())
            {
                params.put("limit", String.valueOf(pagination.getLimit()));
            }
            else if (pagination.getLimit() == -1)
            {
                params.put("limit", String.valueOf(-1));
            }

            // options
            if (pagination.getOptions() != null)
            {
                ObjectNode json = pagination.getOptions().toJSON();
                if (json.size() > 0)
                {
                    params.put("options", JsonUtil.stringify(json, false));
                }
            }
        }

        Locale locale = getLocale();
        if (locale != null)
        {
            params.put("locale", locale.toString());
        }

        Boolean paths = getPaths();
        if (paths != null)
        {
            params.put("paths", Boolean.toString(paths));
        }

        return params;
    }

    public static LookupOptions fromPagination(Pagination pagination)
    {
        LookupOptions options = new LookupOptions();
        options.setPagination(pagination);
        return options;
    }

    public static LookupOptions fromPath(String path)
    {
        LookupOptions options = new LookupOptions();
        options.setPath(path);
        return options;
    }
}
