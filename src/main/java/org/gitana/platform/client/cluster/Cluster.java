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
package org.gitana.platform.client.cluster;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.datastore.DataStore;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Cluster extends DataStore
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // JOBS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Job> queryJobs(ObjectNode query);
    public ResultMap<Job> queryJobs(ObjectNode query, Pagination pagination);

    public ResultMap<Job> listUnstartedJobs();
    public ResultMap<Job> listUnstartedJobs(Pagination pagination);
    public ResultMap<Job> queryUnstartedJobs(ObjectNode query);
    public ResultMap<Job> queryUnstartedJobs(ObjectNode query, Pagination pagination);

    public ResultMap<Job> listRunningJobs();
    public ResultMap<Job> listRunningJobs(Pagination pagination);
    public ResultMap<Job> queryRunningJobs(ObjectNode query);
    public ResultMap<Job> queryRunningJobs(ObjectNode query, Pagination pagination);

    public ResultMap<Job> listFailedJobs();
    public ResultMap<Job> listFailedJobs(Pagination pagination);
    public ResultMap<Job> queryFailedJobs(ObjectNode query);
    public ResultMap<Job> queryFailedJobs(ObjectNode query, Pagination pagination);

    public ResultMap<Job> listWaitingJobs();
    public ResultMap<Job> listWaitingJobs(Pagination pagination);
    public ResultMap<Job> queryWaitingJobs(ObjectNode query);
    public ResultMap<Job> queryWaitingJobs(ObjectNode query, Pagination pagination);

    public ResultMap<Job> listFinishedJobs();
    public ResultMap<Job> listFinishedJobs(Pagination pagination);
    public ResultMap<Job> queryFinishedJobs(ObjectNode query);
    public ResultMap<Job> queryFinishedJobs(ObjectNode query, Pagination pagination);

    public Job readJob(String jobId);

    public void killJob(String jobId);

    public Job waitForJobCompletion(String jobId);

    /**
     * Similar to waitForCompletion() call but uses Cloud CMS 4.0 Job Polling.
     *
     * @param jobId
     * @return
     */
    public Job pollForJobCompletion(String jobId);
}