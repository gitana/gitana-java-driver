/**
 * Copyright 2016 Gitana Software, Inc.
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

package org.gitana.platform.client.cluster;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.datastore.AbstractDataStoreImpl;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.job.JobState;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.Map;

/**
 * @author uzi
 */
public class ClusterImpl extends AbstractDataStoreImpl implements Cluster
{
    public ClusterImpl(ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);
    }

    public ClusterImpl(String clusterId)
    {
        this(JsonUtil.createObject(), true);

        setId(clusterId);
    }

    @Override
    public Cluster getCluster()
    {
        return this;
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_CLUSTER;
    }

    @Override
    public String getResourceUri()
    {
        return "";
    }

    @Override
    public void reload()
    {
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // JOBS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Job> queryJobs(ObjectNode query)
    {
        return queryJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/query", params, query);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listUnstartedJobs()
    {
        return listUnstartedJobs(null);
    }

    @Override
    public ResultMap<Job> listUnstartedJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/unstarted", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryUnstartedJobs(ObjectNode query)
    {
        return queryUnstartedJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryUnstartedJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/unstarted/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listRunningJobs()
    {
        return listRunningJobs(null);
    }

    @Override
    public ResultMap<Job> listRunningJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/running", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryRunningJobs(ObjectNode query)
    {
        return queryRunningJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryRunningJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/running/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listFailedJobs()
    {
        return listFailedJobs(null);
    }

    @Override
    public ResultMap<Job> listFailedJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/failed", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryFailedJobs(ObjectNode query)
    {
        return queryFailedJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryFailedJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/failed/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listWaitingJobs()
    {
        return listWaitingJobs(null);
    }

    @Override
    public ResultMap<Job> listWaitingJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/waiting", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryWaitingJobs(ObjectNode query)
    {
        return queryWaitingJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryWaitingJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/waiting/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listFinishedJobs()
    {
        return listFinishedJobs(null);
    }

    @Override
    public ResultMap<Job> listFinishedJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/finished", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryFinishedJobs(ObjectNode query)
    {
        return queryFinishedJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryFinishedJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/finished/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public Job readJob(String jobId)
    {
        Job job = null;

        try
        {
            Response response = getRemote().get("/jobs/" + jobId);
            job = getFactory().job(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return job;
    }

    @Override
    public void killJob(String jobId)
    {
        getRemote().post("/jobs/" + jobId);
    }

    @Override
    public Job waitForJobCompletion(String jobId)
    {
        Job completedJob = null;

        do
        {
            Job job = readJob(jobId);
            if (JobState.FINISHED.equals(job.getState()))
            {
                completedJob = job;
            }
            else if (JobState.ERROR.equals(job.getState()))
            {
                completedJob = job;
            }
            else
            {
                // otherwise, try again
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException ie)
                {
                    throw new RuntimeException(ie);
                }
            }
        }
        while (completedJob == null);

        return completedJob;
    }
}
