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

package org.gitana.platform.client.job;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.cluster.ClusterDocument;
import org.gitana.platform.services.job.JobState;

import java.util.Calendar;
import java.util.List;

/**
 * @author uzi
 */
public interface Job extends ClusterDocument
{
    // fields
    public final static String FIELD_TYPE = "type";
    public final static String FIELD_RUN_AS_PRINCIPAL_ID = "runAsPrincipalId";
    public final static String FIELD_RUN_AS_PRINCIPAL_DOMAIN_ID = "runAsPrincipalDomainId";

    public final static String FIELD_STATE = "state";
    public final static String FIELD_PLATFORM_ID = "platformId";

    public final static String FIELD_PRIORITY = "priority";
    public final static String FIELD_ATTEMPTS = "attempts";

    public final static String FIELD_SCHEDULE_START_MS = "schedule_start_ms";

    public final static String FIELD_LOG_ENTRIES = "log_entries";

    public final static String FIELD_CURRENT_THREAD = "current_thread";
    public final static String FIELD_CURRENT_SERVER = "current_server";
    public final static String FIELD_CURRENT_SERVER_TIMESTAMP = "current_server_timestmap";

    // submitted
    public final static String FIELD_SUBMITTED_BY = "submitted_by";
    public final static String FIELD_SUBMITTED_TIMESTAMP = "submitted_timestamp";

    // started
    public final static String FIELD_STARTED = "started";
    public final static String FIELD_STARTED_TIMESTAMP = "started_timestamp";

    // stopped
    public final static String FIELD_STOPPED = "stopped";
    public final static String FIELD_STOPPED_TIMESTAMP = "stopped_timestamp";

    // paused
    public final static String FIELD_PAUSED = "paused";
    public final static String FIELD_PAUSED_BY = "paused_by";
    public final static String FIELD_PAUSED_TIMESTAMP = "paused_timestamp";


    // accessors

    public String getType();
    public String getRunAsPrincipalId();
    public String getRunAsPrincipalDomainId();

    // if the job is submitted, these get set
    public String getSubmittedBy();
    public ObjectNode getSubmittedTimestamp();
    public Calendar getSubmittedTime();

    // if a job ever started at any time at all, these get set
    public boolean getStarted();
    public ObjectNode getStartedTimestamp();
    public Calendar getStartedTime();

    // if the job hits any final state (i.e. finished or error), these get set
    public boolean getStopped();
    public ObjectNode getStoppedTimestamp();
    public Calendar getStoppedTime();

    // if the job is paused, these get set
    public boolean getPaused();
    public String getPausedBy();
    public ObjectNode getPausedTimestamp();
    public Calendar getPausedTime();

    // job log entries
    public List<JobLogEntry> getLogEntries();

    public int getPriority();
    public void setPriority(int priority);

    public void setCurrentThreadId(String currentThreadId);
    public String getCurrentThreadId();

    public void setCurrentServerId(String currentServerId);
    public String getCurrentServerId();

    public void setCurrentServerTimestamp(long currentServerTimestamp);
    public long getCurrentServerTimestamp();

    public void setScheduleStart(long ms);
    public long getScheduleStart();

    public int getAttempts();
    public void setAttempts(int attempts);

    public JobState getState();
    public void setState(JobState state);

    public String getPlatformId();
    public void setPlatformId(String platformId);
}