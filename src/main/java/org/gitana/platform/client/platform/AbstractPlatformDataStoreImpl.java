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

package org.gitana.platform.client.platform;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.cluster.AbstractClusterDataStoreImpl;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.support.TypedID;
import org.gitana.platform.client.transfer.CopyJob;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.reference.Reference;
import org.gitana.platform.services.transfer.TransferImportStrategy;
import org.gitana.platform.services.transfer.TransferSchedule;

import java.util.Map;

/**
 * @author uzi
 */
public abstract class AbstractPlatformDataStoreImpl extends AbstractClusterDataStoreImpl implements PlatformDataStore
{
    private Platform platform;

    protected AbstractPlatformDataStoreImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform.getCluster(), obj, isSaved);

        this.platform = platform;
    }

    @Override
    public Reference ref()
    {
        return Reference.create(getTypeId(), getPlatform().getId(), getId());
    }

    @Override
    public Platform getPlatform()
    {
        return this.platform;
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
        Job job = DriverUtil.copy(getCluster(), getRemote(), this, targetContainer, strategy, additionalConfiguration, TransferSchedule.ASYNCHRONOUS);

        return new CopyJob(job.getCluster(), job.getObject(), job.isSaved());
    }

}

