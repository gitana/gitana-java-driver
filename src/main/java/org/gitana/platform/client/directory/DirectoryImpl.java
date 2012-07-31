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

package org.gitana.platform.client.directory;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.identity.Identity;
import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.support.TypedIDConstants;

/**
 * @author uzi
 */
public class DirectoryImpl extends AbstractPlatformDataStoreImpl implements Directory
{
    public DirectoryImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_DIRECTORY;
    }

    @Override
    public String getResourceUri()
    {
        return "/directories/" + getId();
    }

    @Override
    public void reload()
    {
        Directory directory = getPlatform().readDirectory(getId());
        this.reload(directory.getObject());
    }

    @Override
    public Identity readIdentity(String identityId)
    {
        Identity identity = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/identities/" + identityId);
            identity = getFactory().identity(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return identity;
    }
}
