/**
 * Copyright 2025 Gitana Software, Inc.
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
 *   info@gitana.io
 */
package org.gitana.platform.client.transfer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.job.JobImpl;
import org.gitana.platform.services.transfer.TransferExportConfiguration;

/**
 * @author uzi
 */
public class TransferExportJob extends JobImpl<TransferExportJobData, TransferExportJobResult>
{
    // configuration
    public final static String FIELD_CONFIGURATION = "configuration";

    public TransferExportJob(Cluster cluster, ObjectNode obj, boolean isSaved)
    {
        super(cluster, obj, isSaved);
    }

    public TransferExportConfiguration getConfiguration()
    {
        return new TransferExportConfiguration(getObject(FIELD_CONFIGURATION));
    }
}