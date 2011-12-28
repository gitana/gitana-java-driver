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

package org.gitana.platform.client.vault;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.datastore.DataStore;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author uzi
 */
public interface Vault extends DataStore
{
    /**
     * @return platform
     */
    public Platform getPlatform();


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ARCHIVES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Archive> queryArchives(ObjectNode query);

    public ResultMap<Archive> queryArchives(ObjectNode query, Pagination pagination);

    public Archive lookupArchive(String groupId, String artifactId, String versionId);

    public Archive readArchive(String archiveId);

    public void deleteArchive(String archiveId);

    public void uploadArchive(InputStream in, long length)
        throws IOException;

    public InputStream downloadArchive(String archiveId)
        throws IOException;
}