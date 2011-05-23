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

package org.gitana.repo.client.support;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Driver;
import org.gitana.repo.client.Job;
import org.gitana.repo.client.ObjectFactory;
import org.gitana.repo.client.Server;
import org.gitana.util.DateUtil;

import java.util.Calendar;

/**
 * @author uzi
 */
public class JobImpl extends DocumentImpl implements Job
{
    private Driver driver;
    private Server server;

    protected JobImpl(Driver driver, Server server, ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);

        this.driver = driver;
        this.server = server;
    }

    protected Remote getRemote()
    {
        return driver.getRemote();
    }

    protected ObjectFactory getFactory()
    {
        return driver.getFactory();
    }

    @Override
    public Server getServer()
    {
        return this.server;
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
    public String getStackTrace()
    {
        return getString(FIELD_STACKTRACE);
    }

    @Override
    public String getMessage()
    {
        return getString(FIELD_MESSAGE);
    }

}
