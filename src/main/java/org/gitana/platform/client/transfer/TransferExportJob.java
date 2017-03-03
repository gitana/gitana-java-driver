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

package org.gitana.platform.client.transfer;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.job.JobImpl;
import org.gitana.platform.services.transfer.TransferExportConfiguration;

/**
 * @author uzi
 */
public class TransferExportJob extends JobImpl
{
    // manifest properties
    public final static String FIELD_ARCHIVE_GROUP = "archiveGroup";
    public final static String FIELD_ARCHIVE_ARTIFACT = "archiveArtifact";
    public final static String FIELD_ARCHIVE_VERSION = "archiveVersion";

    // vault
    public final static String FIELD_VAULT_ID = "vaultId";

    // configuration
    public final static String FIELD_CONFIGURATION = "configuration";

    public TransferExportJob(Cluster cluster, ObjectNode obj, boolean isSaved)
    {
        super(cluster, obj, isSaved);
    }

    public String getArchiveGroup()
    {
        return getString(FIELD_ARCHIVE_GROUP);
    }

    public String getArchiveArtifact()
    {
        return getString(FIELD_ARCHIVE_ARTIFACT);
    }

    public String getArchiveVersion()
    {
        return getString(FIELD_ARCHIVE_VERSION);
    }

    public String getVaultId()
    {
        return getString(FIELD_VAULT_ID);
    }

    public TransferExportConfiguration getConfiguration()
    {
        return new TransferExportConfiguration(getObject(FIELD_CONFIGURATION));
    }
}
