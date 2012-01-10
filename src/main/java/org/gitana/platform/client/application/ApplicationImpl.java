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

package org.gitana.platform.client.application;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;

/**
 * @author uzi
 */
public class ApplicationImpl extends AbstractPlatformDataStoreImpl implements Application
{
    public ApplicationImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getType()
    {
        return "application";
    }

    @Override
    public String getResourceUri()
    {
        return "/applications/" + getId();
    }

    @Override
    public void reload()
    {
        Application application = getPlatform().readApplication(getId());
        this.reload(application.getObject());
    }
}
