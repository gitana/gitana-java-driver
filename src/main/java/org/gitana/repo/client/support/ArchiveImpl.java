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

package org.gitana.repo.client.support;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Archive;
import org.gitana.repo.client.Driver;
import org.gitana.repo.client.Server;

/**
 * @author uzi
 */
public class ArchiveImpl extends DocumentImpl implements Archive
{
    private Driver driver;
    private Server server;

    protected ArchiveImpl(Driver driver, Server server, ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);

        this.driver = driver;
        this.server = server;
    }

    protected Remote getRemote()
    {
        return driver.getRemote();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Archive)
        {
            Archive other = (Archive) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    @Override
    public void setGroupId(String groupId)
    {
        set(FIELD_GROUP_ID, groupId);
    }

    @Override
    public String getGroupId()
    {
        return getString(FIELD_GROUP_ID);
    }

    @Override
    public void setArtifactId(String artifactId)
    {
        set(FIELD_ARTIFACT_ID, artifactId);
    }

    @Override
    public String getArtifactId()
    {
        return getString(FIELD_ARTIFACT_ID);
    }

    @Override
    public void setVersionId(String versionId)
    {
        set(FIELD_VERSION_ID, versionId);
    }

    @Override
    public String getVersionId()
    {
        return getString(FIELD_VERSION_ID);
    }
}
