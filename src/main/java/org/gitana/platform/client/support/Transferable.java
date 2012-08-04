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

package org.gitana.platform.client.support;

import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.transfer.TransferExportJob;
import org.gitana.platform.client.transfer.TransferImportJob;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.services.transfer.TransferExportConfiguration;
import org.gitana.platform.services.transfer.TransferImportConfiguration;
import org.gitana.platform.services.transfer.TransferSchedule;

/**
 * @author uzi
 */
public interface Transferable
{
    /**
     * Exports synchronously and hands back the binary directly.
     *
     * @param vault
     * @param groupId
     * @param artifactId
     * @param versionId
     * @return
     */
    public Archive exportArchive(Vault vault, String groupId, String artifactId, String versionId);

    /**
     * Exports synchronously and hands back the binary directly.
     *
     * @param vault
     * @param groupId
     * @param artifactId
     * @param versionId
     * @param configuration
     * @return
     */
    public Archive exportArchive(Vault vault, String groupId, String artifactId, String versionId, TransferExportConfiguration configuration);

    /**
     * Exports either synchronously or asynchronously, returning the job.
     *
     * @param vault
     * @param groupId
     * @param artifactId
     * @param versionId
     * @param configuration
     * @param schedule
     * @return
     */
    public TransferExportJob exportArchive(Vault vault, String groupId, String artifactId, String versionId, TransferExportConfiguration configuration, TransferSchedule schedule);

    /**
     * Imports an archive synchronously, returning the job.
     *
     * @param archive
     * @return
     */
    public TransferImportJob importArchive(Archive archive);

    /**
     * Imports an archive synchronously, returning the job.
     *
     * @param archive
     * @param configuration
     * @return
     */
    public TransferImportJob importArchive(Archive archive, TransferImportConfiguration configuration);

    /**
     * Imports either synchronously or asynchronously, returning the job.
     *
     * @param archive
     * @param configuration
     * @param schedule
     * @return
     */
    public TransferImportJob importArchive(Archive archive, TransferImportConfiguration configuration, TransferSchedule schedule);
}
