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
package org.gitana.platform.client.job;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.cluster.Cluster;

/**
 * @author uzi
 */
public class JobResponse<J extends Job, D extends JobData, R extends JobResult>
{
    private Cluster cluster;

    private String jobId;
    private Job job;
    private JobData jobData;
    private JobResult jobResult;

    public JobResponse(Cluster cluster, String jobId)
    {
        this.cluster = cluster;
        this.jobId = jobId;
    }

    public Class getJobClass()
    {
        return JobImpl.class;
    }

    public Class getJobDataClass()
    {
        return JobDataImpl.class;
    }

    public Class getJobResultClass()
    {
        return JobResultImpl.class;
    }

    public String getJobId()
    {
        return jobId;
    }

    public void setJob(Job job)
    {
        this.job = job;
    }

    public Job getJob()
    {
        return job;
    }

    public void setJobData(JobData jobData)
    {
        this.jobData = jobData;
    }

    public JobData getJobData()
    {
        return jobData;
    }

    public void setJobResult(JobResult jobResult)
    {
        this.jobResult = jobResult;
    }

    public JobResult getJobResult()
    {
        return this.jobResult;
    }

    public void pollForCompletion()
    {
        Job _job = cluster.pollForJobCompletion(jobId);

        // instantiate desired job type
        J job = null;
        try
        {
            job = (J) getJobClass().getConstructor(Cluster.class, ObjectNode.class, Boolean.class).newInstance(cluster, _job.getObject(), true);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        // instantiate desired job data type
        D jobData = null;
        try
        {
            ObjectNode _jobData = (ObjectNode) job.getObject().get("_data");
            if (_jobData != null)
            {
                jobData = (D) getJobDataClass().getConstructor(Cluster.class, ObjectNode.class, Boolean.class).newInstance(cluster, _jobData, true);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        // instantiate desired job result type
        R jobResult = null;
        try
        {
            ObjectNode _jobResult = (ObjectNode) job.getObject().get("_result");
            if (_jobResult != null)
            {
                jobResult = (R) getJobResultClass().getConstructor(Cluster.class, ObjectNode.class, Boolean.class).newInstance(cluster, _jobResult, true);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        ((JobImpl) job).setData(jobData);
        ((JobImpl) job).setResult(jobResult);

        setJob(job);
        setJobData(jobData);
        setJobResult(jobResult);
    }
}