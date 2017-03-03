/**
 * Copyright 2017 Gitana Software, Inc.
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

package org.gitana.platform.client.webhost;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.TypedIDConstants;

/**
 * @author uzi
 */
public class AutoClientMappingImpl extends AbstractWebHostDocumentImpl implements AutoClientMapping
{
    public AutoClientMappingImpl(WebHost webhost, ObjectNode obj, boolean isSaved)
    {
        super(webhost, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_AUTO_CLIENT_MAPPING;
    }

    @Override
    public String getResourceUri()
    {
        return "/webhosts/" + getWebHostId() + "/autoclientmappings/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof AutoClientMapping)
        {
            AutoClientMapping other = (AutoClientMapping) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELFABLE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put(getResourceUri(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete(getResourceUri());
    }

    @Override
    public void reload()
    {
        AutoClientMapping settings = getWebHost().readAutoClientMapping(getId());

        this.reload(settings.getObject());
    }

    @Override
    public String getApplicationId()
    {
        return getString(FIELD_APPLICATION_ID);
    }

    @Override
    public void setApplicationId(String applicationId)
    {
        set(FIELD_APPLICATION_ID, applicationId);
    }

    @Override
    public String getUri()
    {
        return getString(FIELD_URI);
    }

    @Override
    public void setUri(String uri)
    {
        set(FIELD_URI, uri);
    }

    @Override
    public String getClientKey()
    {
        return getString(FIELD_CLIENT_KEY);
    }

    @Override
    public void setClientKey(String clientKey)
    {
        set(FIELD_CLIENT_KEY, clientKey);
    }

    @Override
    public String getAuthGrantKey()
    {
        return getString(FIELD_AUTH_GRANT_KEY);
    }

    @Override
    public void setAuthGrantKey(String authGrantKey)
    {
        set(FIELD_AUTH_GRANT_KEY, authGrantKey);
    }
}
