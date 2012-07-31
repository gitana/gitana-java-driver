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

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.cluster.AbstractClusterDataStoreImpl;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.support.TypedID;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.transfer.TransferSchedule;
import org.gitana.util.JsonUtil;

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
    public String copy(TypedID targetContainer)
    {
        Job job = DriverUtil.copy(getCluster(), getRemote(), this, targetContainer, TransferSchedule.SYNCHRONOUS);

        ArrayNode imports = job.getArray("imports");
        ObjectNode lastImport = (ObjectNode) imports.get(imports.size() - 1);

        return JsonUtil.objectGetString(lastImport, "id");
    }

    @Override
    public Job copyAsync(TypedID targetContainer)
    {
        return DriverUtil.copy(getCluster(), getRemote(), this, targetContainer, TransferSchedule.ASYNCHRONOUS);
    }

}

