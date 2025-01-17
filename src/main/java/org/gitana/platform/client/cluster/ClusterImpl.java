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
package org.gitana.platform.client.cluster;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.datastore.AbstractDataStoreImpl;
import org.gitana.platform.client.job.*;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.job.JobState;
import org.gitana.platform.services.reference.Reference;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.Map;
import java.util.Set;

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
    public Reference ref()
    {
        return Reference.create(getTypeId(), getId());
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

//    @Override
//    public ResultMap<Job> listUnstartedJobs()
//    {
//        return listUnstartedJobs(null);
//    }
//
//    @Override
//    public ResultMap<Job> listUnstartedJobs(Pagination pagination)
//    {
//        ObjectNode notTrue = JsonUtil.createObject();
//        notTrue.put("$ne", true);
//
//        ObjectNode query = JsonUtil.createObject();
//        query.put(Job.FIELD_STARTED, notTrue);
//
//        return queryJobs(query, pagination);
//    }
//
//    @Override
//    public ResultMap<Job> queryUnstartedJobs(ObjectNode query)
//    {
//        return queryUnstartedJobs(query, null);
//    }
//
//    @Override
//    public ResultMap<Job> queryUnstartedJobs(ObjectNode query, Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().post("/jobs/unstarted/query", params);
//        return getFactory().jobs(this, response);
//    }
//
//    @Override
//    public ResultMap<Job> listRunningJobs()
//    {
//        return listRunningJobs(null);
//    }
//
//    @Override
//    public ResultMap<Job> listRunningJobs(Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().get("/jobs/running", params);
//        return getFactory().jobs(this, response);
//    }
//
//    @Override
//    public ResultMap<Job> queryRunningJobs(ObjectNode query)
//    {
//        return queryRunningJobs(query, null);
//    }
//
//    @Override
//    public ResultMap<Job> queryRunningJobs(ObjectNode query, Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().post("/jobs/running/query", params);
//        return getFactory().jobs(this, response);
//    }
//
//    @Override
//    public ResultMap<Job> listFailedJobs()
//    {
//        return listFailedJobs(null);
//    }
//
//    @Override
//    public ResultMap<Job> listFailedJobs(Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().get("/jobs/failed", params);
//        return getFactory().jobs(this, response);
//    }
//
//    @Override
//    public ResultMap<Job> queryFailedJobs(ObjectNode query)
//    {
//        return queryFailedJobs(query, null);
//    }
//
//    @Override
//    public ResultMap<Job> queryFailedJobs(ObjectNode query, Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().post("/jobs/failed/query", params);
//        return getFactory().jobs(this, response);
//    }
//
//    @Override
//    public ResultMap<Job> listWaitingJobs()
//    {
//        return listWaitingJobs(null);
//    }
//
//    @Override
//    public ResultMap<Job> listWaitingJobs(Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().get("/jobs/waiting", params);
//        return getFactory().jobs(this, response);
//    }
//
//    @Override
//    public ResultMap<Job> queryWaitingJobs(ObjectNode query)
//    {
//        return queryWaitingJobs(query, null);
//    }
//
//    @Override
//    public ResultMap<Job> queryWaitingJobs(ObjectNode query, Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().post("/jobs/waiting/query", params);
//        return getFactory().jobs(this, response);
//    }
//
//    @Override
//    public ResultMap<Job> listFinishedJobs()
//    {
//        return listFinishedJobs(null);
//    }
//
//    @Override
//    public ResultMap<Job> listFinishedJobs(Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().get("/jobs/finished", params);
//        return getFactory().jobs(this, response);
//    }
//
//    @Override
//    public ResultMap<Job> queryFinishedJobs(ObjectNode query)
//    {
//        return queryFinishedJobs(query, null);
//    }
//
//    @Override
//    public ResultMap<Job> queryFinishedJobs(ObjectNode query, Pagination pagination)
//    {
//        Map<String, String> params = DriverUtil.params(pagination);
//
//        Response response = getRemote().post("/jobs/finished/query", params);
//        return getFactory().jobs(this, response);
//    }

    public int countJobs(ObjectNode query)
    {
        Pagination p = Pagination.limit(1);
        p.getOptions().setCountTotal(true);

        return queryJobs(query, p).totalRows();
    }

    @Override
    public int countUnstartedJobs()
    {
        ObjectNode notTrue = JsonUtil.createObject();
        notTrue.put("$ne", true);

        ObjectNode query = JsonUtil.createObject();
        query.put(Job.FIELD_STARTED, notTrue);

        return countJobs(query);
    }

    @Override
    public int countJobsInState(Set<JobState> states)
    {
        ArrayNode inArray = JsonUtil.createArray();
        for (JobState state: states)
        {
            inArray.add(state.toString());
        }
        ObjectNode in = JsonUtil.createObject();
        in.put("$in", inArray);

        ObjectNode query = JsonUtil.createObject();
        query.put(Job.FIELD_STATE, in);

        return countJobs(query);
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
        getRemote().post("/jobs/" + jobId + "/kill");
    }

    @Override
    public Job pollForJobCompletion(String jobId)
    {
        return pollForJobCompletion(jobId,null, null, null);
    }

    @Override
    public Job pollForJobCompletion(String jobId, Class jobClass, Class jobDataClass, Class jobResultClass)
    {
        if (jobClass == null)
        {
            jobClass = JobImpl.class;
        }

        if (jobDataClass == null)
        {
            jobDataClass = JobDataImpl.class;
        }

        if (jobResultClass == null)
        {
            jobResultClass = JobResultImpl.class;
        }

        ObjectNode _job = _pollForJobCompletion(jobId);

        // instantiate desired job type
        Job job = null;
        try
        {
            job = (Job) jobClass.getConstructor(Cluster.class, ObjectNode.class, boolean.class).newInstance(this, _job, true);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        // instantiate desired job data type
        JobData jobData = null;
        try
        {
            ObjectNode _jobData = (ObjectNode) job.getObject().get("_data");
            if (_jobData != null)
            {
                jobData = (JobData) jobDataClass.getConstructor(Cluster.class, ObjectNode.class, boolean.class).newInstance(this, _jobData, true);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        // instantiate desired job result type
        JobResult jobResult = null;
        try
        {
            ObjectNode _jobResult = (ObjectNode) job.getObject().get("_result");
            if (_jobResult != null)
            {
                jobResult = (JobResult) jobResultClass.getConstructor(Cluster.class, ObjectNode.class, boolean.class).newInstance(this, _jobResult, true);
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        ((JobImpl) job).setData(jobData);
        ((JobImpl) job).setResult(jobResult);

        return job;
    }

    private ObjectNode _pollForJobCompletion(String jobId)
    {
        ObjectNode completedJobObject = null;

        do
        {
            Response response = getRemote().get("/jobs/" + jobId + "/poll");
            ObjectNode jobObject = response.getObjectNode();

            String jobState = JsonUtil.objectGetString(jobObject, Job.FIELD_STATE);

            if (JobState.FINISHED.toString().equals(jobState))
            {
                completedJobObject = jobObject;
            }
            else if (JobState.ERROR.toString().equals(jobState))
            {
                completedJobObject = jobObject;
            }
            else
            {
                // otherwise, try again
                try
                {
                    Thread.sleep(250);
                }
                catch (InterruptedException ie)
                {
                    throw new RuntimeException(ie);
                }
            }
        }
        while (completedJobObject == null);

        return completedJobObject;
    }
}
