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
package org.gitana.platform.client.platform;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.document.DocumentImpl;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.support.*;
import org.gitana.platform.client.transfer.CopyJob;
import org.gitana.platform.client.transfer.TransferExportJob;
import org.gitana.platform.client.transfer.TransferImportJob;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.services.reference.Reference;
import org.gitana.platform.services.transfer.TransferExportConfiguration;
import org.gitana.platform.services.transfer.TransferImportConfiguration;
import org.gitana.platform.services.transfer.TransferImportStrategy;
import org.gitana.platform.services.transfer.TransferSchedule;

import java.util.Map;

/**
 * Abstract implementation of a platform document.
 *
 * @author uzi
 */
public abstract class AbstractPlatformDocumentImpl extends DocumentImpl implements PlatformDocument
{
    private Platform platform;

    public AbstractPlatformDocumentImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
    	super(obj, isSaved);

        this.platform = platform;
    }

    @Override
    public Reference ref()
    {
        return Reference.create(getTypeId(), getPlatformId(), getId());
    }

    protected abstract String getResourceUri();

    protected ObjectFactory getFactory()
    {
        return getDriver().getFactory();
    }

    protected Driver getDriver()
    {
        return DriverContext.getDriver();
    }

    protected Remote getRemote()
    {
        return getDriver().getRemote();
    }

    @Override
    public Platform getPlatform()
    {
        return this.platform;
    }

    @Override
    public String getPlatformId()
    {
        return getPlatform().getId();
    }

    protected Cluster getCluster()
    {
        return getPlatform().getCluster();
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TRANSFER
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Archive exportArchive(Vault vault, String groupId, String artifactId, String versionId)
    {
        return exportArchive(vault, groupId, artifactId, versionId, null);
    }

    @Override
    public Archive exportArchive(Vault vault, String groupId, String artifactId, String versionId, TransferExportConfiguration configuration)
    {
        // run the job synchronously
        exportArchive(vault, groupId, artifactId, versionId, configuration, TransferSchedule.SYNCHRONOUS);

        // read the archive
        Archive archive = vault.lookupArchive(groupId, artifactId, versionId);
        if (archive == null)
        {
            throw new RuntimeException("Unable to find archive");
        }

        return archive;
    }

    @Override
    public TransferExportJob exportArchive(Vault vault, String groupId, String artifactId, String versionId, TransferExportConfiguration configuration, TransferSchedule schedule)
    {
        boolean synchronous = TransferSchedule.SYNCHRONOUS.equals(schedule);

        if (configuration == null)
        {
            configuration = new TransferExportConfiguration();
        }

        // start the export
        ObjectNode configObject = configuration.toJSON();
        Response response1 = getRemote().post(getResourceUri() + "/export?vault=" + vault.getId() + "&group=" + groupId + "&artifact=" + artifactId + "&version=" + versionId + "&schedule=" + TransferSchedule.ASYNCHRONOUS.toString(), configObject);
        String jobId = response1.getId();

        Job job = DriverUtil.retrieveOrPollJob(getCluster(), jobId, synchronous);

        return new TransferExportJob(job.getCluster(), job.getObject(), job.isSaved());
    }

    @Override
    public TransferImportJob importArchive(Archive archive)
    {
        return importArchive(archive, null);
    }

    @Override
    public TransferImportJob importArchive(Archive archive, TransferImportConfiguration configuration)
    {
        return importArchive(archive, configuration, TransferSchedule.SYNCHRONOUS);
    }

    @Override
    public TransferImportJob importArchive(Archive archive, TransferImportConfiguration configuration, TransferSchedule schedule)
    {
        boolean synchronous = TransferSchedule.SYNCHRONOUS.equals(schedule);

        String vaultId = archive.getVaultId();
        String groupId = archive.getGroupId();
        String artifactId = archive.getArtifactId();
        String versionId = archive.getVersionId();

        if (configuration == null)
        {
            configuration = new TransferImportConfiguration();
        }

        // post binary to server and get back job id
        // we always do this asynchronously
        ObjectNode configObject = configuration.toJSON();
        Response response = getRemote().post(getResourceUri() + "/import?vault=" + vaultId + "&group=" + groupId + "&artifact=" + artifactId + "&version=" + versionId + "&schedule=" + TransferSchedule.ASYNCHRONOUS.toString(), configObject);
        String jobId = response.getId();

        Job job = DriverUtil.retrieveOrPollJob(getCluster(), jobId, synchronous);

        return new TransferImportJob(job.getCluster(), job.getObject(), job.isSaved());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // COPYABLE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CopyJob copy(TypedID targetContainer)
    {
        return copy(targetContainer, null, null);
    }

    @Override
    public CopyJob copy(TypedID targetContainer, TransferImportStrategy strategy, Map<String, Object> additionalConfiguration)
    {
        return DriverUtil.copy(getCluster(), getRemote(), this, targetContainer, strategy, additionalConfiguration, TransferSchedule.SYNCHRONOUS);
    }

    @Override
    public CopyJob copyAsync(TypedID targetContainer)
    {
        return copyAsync(targetContainer, null, null);
    }

    @Override
    public CopyJob copyAsync(TypedID targetContainer, TransferImportStrategy strategy, Map<String, Object> additionalConfiguration)
    {
        return DriverUtil.copy(getCluster(), getRemote(), this, targetContainer, strategy, additionalConfiguration, TransferSchedule.ASYNCHRONOUS);
    }
}
