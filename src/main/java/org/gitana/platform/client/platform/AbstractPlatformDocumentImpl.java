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

package org.gitana.platform.client.platform;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.document.DocumentImpl;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.services.job.JobState;
import org.gitana.platform.services.transfer.TransferExportConfiguration;
import org.gitana.platform.services.transfer.TransferImportConfiguration;
import org.gitana.platform.services.transfer.TransferSchedule;

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
    public Job exportArchive(Vault vault, String groupId, String artifactId, String versionId, TransferExportConfiguration configuration, TransferSchedule schedule)
    {
        if (schedule == null)
        {
            schedule = TransferSchedule.SYNCHRONOUS;
        }

        if (configuration == null)
        {
            configuration = new TransferExportConfiguration();
        }

        // start the export
        ObjectNode configObject = configuration.toJSON();
        Response response1 = getRemote().post(getResourceUri() + "/export?vault=" + vault.getId() + "&group=" + groupId + "&artifact=" + artifactId + "&version=" + versionId + "&schedule=" + schedule.toString(), configObject);
        String jobId = response1.getId();

        // if we were set to "synchronous", then wait a bit to make sure it is marked as complete
        boolean completed = false;
        Job job = null;
        do
        {
            job = getCluster().readJob(jobId);
            if (job != null)
            {
                if (JobState.FINISHED.equals(job.getState()) || JobState.ERROR.equals(job.getState()))
                {
                    completed = true;
                }
            }

            if (!completed)
            {
                try { Thread.sleep(500); } catch (Exception ex) { completed = true; }
            }
        }
        while (!completed);

        return job;
    }

    @Override
    public Job importArchive(Archive archive)
    {
        return importArchive(archive, null);
    }

    @Override
    public Job importArchive(Archive archive, TransferImportConfiguration configuration)
    {
        return importArchive(archive, configuration, TransferSchedule.SYNCHRONOUS);
    }

    @Override
    public Job importArchive(Archive archive, TransferImportConfiguration configuration, TransferSchedule schedule)
    {
        String vaultId = archive.getVaultId();
        String groupId = archive.getGroupId();
        String artifactId = archive.getArtifactId();
        String versionId = archive.getVersionId();

        if (schedule == null)
        {
            schedule = TransferSchedule.SYNCHRONOUS;
        }

        if (configuration == null)
        {
            configuration = new TransferImportConfiguration();
        }

        // post binary to server and get back job id
        // we always do this asynchronously
        ObjectNode configObject = configuration.toJSON();
        Response response = getRemote().post(getResourceUri() + "/import?vault=" + vaultId + "&group=" + groupId + "&artifact=" + artifactId + "&version=" + versionId + "&schedule=" + TransferSchedule.ASYNCHRONOUS.toString(), configObject);
        String jobId = response.getId();

        // if we were set to "synchronous", then wait a bit to make sure it is marked as complete
        boolean completed = false;
        Job job = null;
        do
        {
            job = getCluster().readJob(jobId);
            if (job != null)
            {
                if (JobState.FINISHED.equals(job.getState()) || JobState.ERROR.equals(job.getState()))
                {
                    completed = true;
                }
            }

            if (!completed)
            {
                try { Thread.sleep(500); } catch (Exception ex) { completed = true; }
            }
        }
        while (!completed);

        return job;
    }

}
