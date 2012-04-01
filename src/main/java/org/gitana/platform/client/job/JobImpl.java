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

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.cluster.AbstractClusterDocumentImpl;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.services.job.JobState;
import org.gitana.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author uzi
 */
public class JobImpl extends AbstractClusterDocumentImpl implements Job
{
    public JobImpl(Cluster cluster, ObjectNode obj, boolean isSaved)
    {
        super(cluster, obj, isSaved);
    }

    @Override
    protected String getResourceUri()
    {
        return "/jobs/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Job)
        {
            Job other = (Job) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    @Override
    public String getType()
    {
        return getString(FIELD_TYPE);
    }

    @Override
    public String getRunAsPrincipalId()
    {
        return getString(FIELD_RUN_AS_PRINCIPAL_ID);
    }

    @Override
    public String getRunAsPrincipalDomainId()
    {
        return getString(FIELD_RUN_AS_PRINCIPAL_DOMAIN_ID);
    }

    @Override
    public ObjectNode getStartedTimestamp()
    {
        return getObject(FIELD_STARTED_TIMESTAMP);
    }

    @Override
    public ObjectNode getStoppedTimestamp()
    {
        return getObject(FIELD_STOPPED_TIMESTAMP);
    }

    @Override
    public Calendar getStartedTime()
    {
        Calendar calendar = null;

        ObjectNode timestamp = getStartedTimestamp();
        if (timestamp != null)
        {
            calendar = DateUtil.getTime(timestamp);
        }

        return calendar;
    }

    @Override
    public boolean getStopped()
    {
        return getBoolean(FIELD_STOPPED);
    }

    @Override
    public Calendar getStoppedTime()
    {
        Calendar calendar = null;

        ObjectNode timestamp = getStoppedTimestamp();
        if (timestamp != null)
        {
            calendar = DateUtil.getTime(timestamp);
        }

        return calendar;
    }

    @Override
    public boolean getPaused()
    {
        return getBoolean(FIELD_PAUSED);
    }

    @Override
    public String getPausedBy()
    {
        return getString(FIELD_PAUSED_BY);
    }

    @Override
    public ObjectNode getPausedTimestamp()
    {
        return getObject(FIELD_PAUSED_TIMESTAMP);
    }

    @Override
    public Calendar getPausedTime()
    {
        Calendar calendar = null;

        ObjectNode timestamp = getPausedTimestamp();
        if (timestamp != null)
        {
            calendar = DateUtil.getTime(timestamp);
        }

        return calendar;
    }

    @Override
    public String getSubmittedBy()
    {
        return getString(FIELD_SUBMITTED_BY);
    }

    @Override
    public ObjectNode getSubmittedTimestamp()
    {
        return getObject(FIELD_SUBMITTED_TIMESTAMP);
    }

    @Override
    public Calendar getSubmittedTime()
    {
        Calendar calendar = null;

        ObjectNode timestamp = getSubmittedTimestamp();
        if (timestamp != null)
        {
            calendar = DateUtil.getTime(timestamp);
        }

        return calendar;
    }

    @Override
    public boolean getStarted()
    {
        return getBoolean(FIELD_STARTED);
    }

    @Override
    public List<JobLogEntry> getLogEntries()
    {
        List<JobLogEntry> list = new ArrayList<JobLogEntry>();

        ArrayNode entries = getArray(FIELD_LOG_ENTRIES);
        for (int i = 0; i < entries.size(); i++)
        {
            ObjectNode object = (ObjectNode) entries.get(i);

            JobLogEntry entry = new JobLogEntry(object);
            list.add(entry);
        }

        return list;
    }

    @Override
    public int getPriority()
    {
        return getInt(FIELD_PRIORITY);
    }

    @Override
    public void setPriority(int priority)
    {
        set(FIELD_PRIORITY, priority);
    }

    @Override
    public void setCurrentThreadId(String currentThreadId)
    {
        set(FIELD_CURRENT_THREAD, currentThreadId);
    }

    @Override
    public String getCurrentThreadId()
    {
        return getString(FIELD_CURRENT_THREAD);
    }

    @Override
    public void setCurrentServerId(String currentMemberId)
    {
        set(FIELD_CURRENT_SERVER, currentMemberId);
    }

    @Override
    public String getCurrentServerId()
    {
        return getString(FIELD_CURRENT_SERVER);
    }

    @Override
    public void setCurrentServerTimestamp(long currentServerTimestamp)
    {
        set(FIELD_CURRENT_SERVER_TIMESTAMP, currentServerTimestamp);
    }

    @Override
    public long getCurrentServerTimestamp()
    {
        return getLong(FIELD_CURRENT_SERVER_TIMESTAMP);
    }


    @Override
    public void setScheduleStart(long ms)
    {
        set(FIELD_SCHEDULE_START_MS, ms);
    }

    @Override
    public long getScheduleStart()
    {
        return getLong(FIELD_SCHEDULE_START_MS);
    }

    @Override
    public int getAttempts()
    {
        int attempts = getInt(FIELD_ATTEMPTS);
        if (attempts == -1)
        {
            attempts = 0;
        }

        return attempts;
    }

    @Override
    public void setAttempts(int attempts)
    {
        set(FIELD_ATTEMPTS, attempts);
    }

    @Override
    public String getPlatformId()
    {
        return getString(FIELD_PLATFORM_ID);
    }

    @Override
    public void setPlatformId(String platformId)
    {
        set(FIELD_PLATFORM_ID, platformId);
    }

    @Override
    public JobState getState()
    {
        JobState state = null;

        String str = getString(FIELD_STATE);
        if (str != null)
        {
            state = JobState.valueOf(str);
        }

        return state;
    }

    @Override
    public void setState(JobState state)
    {
        set(FIELD_STATE, state.toString());
    }
}
