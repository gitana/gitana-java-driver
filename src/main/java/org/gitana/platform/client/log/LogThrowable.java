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
package org.gitana.platform.client.log;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.GitanaObjectImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public class LogThrowable extends GitanaObjectImpl
{
    public final static String FIELD_MESSAGE = "message";
    public final static String FIELD_STACKTRACE = "stacktrace";

    public LogThrowable(ObjectNode object)
    {
        super(object);
    }

    public String getMessage()
    {
        return getString(FIELD_MESSAGE);
    }

    public List<LogStackTraceElement> getStackTrace()
    {
        List<LogStackTraceElement> stacktrace = new ArrayList<LogStackTraceElement>();

        ArrayNode array = (ArrayNode) getObject().get(FIELD_STACKTRACE);
        for (int i = 0; i < array.size(); i++)
        {
            ObjectNode object = (ObjectNode) array.get(i);

            stacktrace.add(new LogStackTraceElement(object));
        }

        return stacktrace;
    }
}