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
package org.gitana.platform.client.log;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.cluster.AbstractClusterDocumentImpl;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public class LogEntryImpl extends AbstractClusterDocumentImpl implements LogEntry
{
    public LogEntryImpl(Cluster cluster, ObjectNode obj, boolean isSaved)
    {
        super(cluster, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_LOG_ENTRY;
    }

    @Override
    protected String getResourceUri()
    {
        return "/logs/" + getId();
    }

    @Override
    public String getLogPrincipalId()
    {
        return getString(FIELD_PRINCIPAL_ID);
    }

    @Override
    public String getLogRepositoryId()
    {
        return getString(FIELD_REPOSITORY_ID);
    }

    @Override
    public String getLogBranchId()
    {
        return getString(FIELD_BRANCH_ID);
    }

    @Override
    public String getLevel()
    {
        return getString(FIELD_LEVEL);
    }

    @Override
    public String getThread()
    {
        return getString(FIELD_THREAD);
    }

    @Override
    public ObjectNode getTimestamp()
    {
        return getObject(FIELD_TIMESTAMP);
    }

    @Override
    public String getMessage()
    {
        return getString(FIELD_MESSAGE);
    }

    @Override
    public String getFileName()
    {
        return getString(FIELD_FILENAME);
    }

    @Override
    public String getMethod()
    {
        return getString(FIELD_METHOD);
    }

    @Override
    public int getLineNumber()
    {
        return getInt(FIELD_LINE_NUMBER);
    }

    @Override
    public String getClassFullyQualifiedName()
    {
        ObjectNode classObject = getObject(FIELD_CLASS);

        return JsonUtil.objectGetString(classObject, FIELD_CLASS_FULLY_QUALIFIED_NAME);
    }

    @Override
    public String getClassPackage()
    {
        ObjectNode classObject = getObject(FIELD_CLASS);

        return JsonUtil.objectGetString(classObject, FIELD_CLASS_PACKAGE);

    }

    @Override
    public String getClassName()
    {
        ObjectNode classObject = getObject(FIELD_CLASS);

        return JsonUtil.objectGetString(classObject, FIELD_CLASS_NAME);
    }

    @Override
    public List<LogThrowable> getLogThrowables()
    {
        List<LogThrowable> throwables = new ArrayList<LogThrowable>();

        ArrayNode array = getArray(FIELD_THROWABLES);
        for (int i = 0; i < array.size(); i++)
        {
            ObjectNode object = (ObjectNode) array.get(i);

            throwables.add(new LogThrowable(object));
        }

        return throwables;
    }
}
