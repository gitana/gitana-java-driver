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
 *   info@gitana.io
 */
package org.gitana.platform.client.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.cluster.AbstractClusterDocumentImpl;
import org.gitana.platform.client.cluster.Cluster;

/**
 * @author uzi
 */
public class JobResultImpl extends AbstractClusterDocumentImpl implements JobResult
{
    public JobResultImpl(Cluster cluster, ObjectNode obj, boolean isSaved)
    {
        super(cluster, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return "jobresult";
    }

    @Override
    protected String getResourceUri()
    {
        return "/jobs/" + getId() + "/result";
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof JobResult)
        {
            JobResult other = (JobResult) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    @Override
    public String getJobId()
    {
        return getString(FIELD_JOB_ID);
    }

    @Override
    public void setJobId(String jobId)
    {
        set(FIELD_JOB_ID, jobId);
    }

    @Override
    public String getJobType()
    {
        return getString(FIELD_JOB_TYPE);
    }

    @Override
    public void setJobType(String jobType)
    {
        set(FIELD_JOB_TYPE, jobType);
    }
}
