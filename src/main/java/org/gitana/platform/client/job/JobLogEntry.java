/**
 * Copyright 2026 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.GitanaObjectImpl;
import org.gitana.util.DateUtil;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public class JobLogEntry extends GitanaObjectImpl
{
    // message attributes
    public final static String FIELD_TYPE = "type";
    public final static String FIELD_MESSAGE = "message";
    public final static String FIELD_TIMESTAMP = "timestamp";
    public final static String FIELD_STACKTRACE = "stacktrace";

    public JobLogEntry()
    {
        super();

        initStatus();
    }

    public JobLogEntry(ObjectNode object)
    {
        super(object);

        initStatus();
    }

    private void initStatus()
    {
        set(FIELD_TIMESTAMP, DateUtil.getTimestamp());
    }

    public JobLogEntryType getType()
    {
        JobLogEntryType type = null;

        if (has(FIELD_TYPE))
        {
            type = JobLogEntryType.valueOf(getString(FIELD_TYPE));
        }

        return type;
    }

    public void setType(JobLogEntryType type)
    {
        set(FIELD_TYPE, type.toString());
    }

    public String getMessage()
    {
        return getString(FIELD_MESSAGE);
    }

    public void setMessage(String message)
    {
        set(FIELD_MESSAGE, message);
    }

    public ObjectNode getTimestamp()
    {
        return getObject(FIELD_TIMESTAMP);
    }

    public void setTimestamp(ObjectNode timestamp)
    {
        set(FIELD_TIMESTAMP, timestamp);
    }

    public List<String> getStackTrace()
    {
        List<String> list = new ArrayList<String>();

        if (has(FIELD_STACKTRACE))
        {
            ArrayNode array = getArray(FIELD_STACKTRACE);
            for (int i = 0; i < array.size(); i++)
            {
                String element = (String) array.get(i).textValue();

                list.add(element);
            }
        }

        return list;
    }

    public void setStackTrace(List<String> stacktrace)
    {
        ArrayNode array = JsonUtil.createArray(stacktrace);

        set(FIELD_STACKTRACE, array);
    }

    public void setStackTrace(StackTraceElement[] elements)
    {
        ArrayNode array = JsonUtil.createArray();

        for (StackTraceElement element: elements)
        {
            array.add(element.toString());
        }

        set(FIELD_STACKTRACE, array);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());
        for (String trace: getStackTrace())
        {
            sb.append("\r\n");
            sb.append(trace);
        }

        return sb.toString();
    }
}