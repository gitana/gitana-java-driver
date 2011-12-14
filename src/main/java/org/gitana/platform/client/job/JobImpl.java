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
import org.gitana.platform.client.platform.AbstractPlatformDocumentImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.util.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author uzi
 */
public class JobImpl extends AbstractPlatformDocumentImpl implements Job
{
    public JobImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
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
    public String getRunAs()
    {
        return getString(FIELD_RUN_AS);
    }

    @Override
    public boolean isStarted()
    {
        return getBoolean(FIELD_IS_STARTED);
    }

    @Override
    public boolean isRunning()
    {
        return getBoolean(FIELD_IS_RUNNING);
    }

    @Override
    public boolean isError()
    {
        return getBoolean(FIELD_IS_ERROR);
    }

    @Override
    public boolean isFinished()
    {
        return getBoolean(FIELD_IS_FINISHED);
    }

    @Override
    public boolean isSubmitted()
    {
        return getBoolean(FIELD_IS_SUBMITTED);
    }

    @Override
    public String getStartedBy()
    {
        return getString(FIELD_STARTED_BY);
    }

    @Override
    public String getStoppedBy()
    {
        return getString(FIELD_STOPPED_BY);
    }

    @Override
    public String getSubmittedBy()
    {
        return getString(FIELD_SUBMITTED_BY);
    }

    @Override
    public ObjectNode getStartTimestamp()
    {
        return getObject(FIELD_START_TIMESTAMP);
    }

    @Override
    public ObjectNode getStopTimestamp()
    {
        return getObject(FIELD_STOP_TIMESTAMP);
    }

    @Override
    public Calendar getStartTime()
    {
        return DateUtil.getTime(getStartTimestamp());
    }

    @Override
    public Calendar getStopTime()
    {
        return DateUtil.getTime(getStopTimestamp());
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

    public String getCurrentThreadId()
    {
        return getString(FIELD_CURRENT_THREAD);
    }

    @Override
    public String getCurrentServerId()
    {
        return getString(FIELD_CURRENT_SERVER);
    }

    @Override
    public long getCurrentServerTimestamp()
    {
        return getLong(FIELD_CURRENT_SERVER_TIMESTAMP);
    }

    @Override
    public long getScheduleStart()
    {
        return getLong(FIELD_SCHEDULE_START_MS);
    }

    @Override
    public int getAttempts()
    {
        return getInt(FIELD_ATTEMPTS);
    }
}
