/**
 * Copyright 2016 Gitana Software, Inc.
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

package org.gitana.platform.client.vault;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class VaultImpl extends AbstractPlatformDataStoreImpl implements Vault
{
    public VaultImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_VAULT;
    }

    @Override
    public String getResourceUri()
    {
        return "/vaults/" + getId();
    }

    @Override
    public void reload()
    {
        Vault vault = getPlatform().readVault(getId());
        this.reload(vault.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ARCHIVES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Archive> queryArchives(ObjectNode query)
    {
        return queryArchives(query, null);
    }

    @Override
    public ResultMap<Archive> queryArchives(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/archives/query", params, query);
        return getFactory().archives(this, response);
    }

    @Override
    public Archive lookupArchive(String groupId, String artifactId, String versionId)
    {
        Archive archive = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("group", groupId);
        params.put("artifact", artifactId);
        params.put("version", versionId);

        try
        {
            Response response = getRemote().get(getResourceUri() + "/archives/lookup", params);
            archive = getFactory().archive(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return archive;
    }

    @Override
    public Archive readArchive(String archiveId)
    {
        Archive archive = null;

        Map<String, String> params = new HashMap<String, String>();

        try
        {
            Response response = getRemote().get(getResourceUri() + "/archives/" + archiveId, params);
            archive = getFactory().archive(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return archive;
    }

    @Override
    public void deleteArchive(String archiveId)
    {
        try
        {
            Response response = getRemote().delete(getResourceUri() + "/archives/" + archiveId);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }
    }

    @Override
    public void uploadArchive(InputStream in, long length)
        throws IOException
    {
        String contentType = MimeTypeMap.APPLICATION_ZIP;
        try
        {
            getRemote().upload(getResourceUri() + "/archives", in, length, contentType);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public InputStream downloadArchive(String archiveId)
        throws IOException
    {
        InputStream in = null;

        HttpResponse response = null;
        try
        {
            response = getRemote().download(getResourceUri() + "/archives/" + archiveId + "/download");

            in = response.getEntity().getContent();
        }
        catch (Exception ex)
        {
            try { EntityUtils.consume(response.getEntity()); } catch (Exception ex2) { }

            throw new RuntimeException(ex);
        }

        return in;
    }
}
