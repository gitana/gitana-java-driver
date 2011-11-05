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

package org.gitana.repo.client;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.support.LogThrowable;

import java.util.List;

/**
 * @author uzi
 */
public interface LogEntry extends Document
{
    // top level fields
    public final static String FIELD_PRINCIPAL_ID = "principalId";
    public final static String FIELD_REPOSITORY_ID = "repositoryId";
    public final static String FIELD_BRANCH_ID = "branchId";

    public final static String FIELD_LEVEL = "level";
    public final static String FIELD_THREAD = "thread";
    public final static String FIELD_TIMESTAMP = "timestamp";
    public final static String FIELD_THROWABLES = "throwables";

    public final static String FIELD_MESSAGE = "message";

    // stack trace element
    public final static String FIELD_FILENAME = "filename";
    public final static String FIELD_METHOD = "method";
    public final static String FIELD_LINE_NUMBER = "line";
    public final static String FIELD_CLASS = "class";
    public final static String FIELD_CLASS_FULLY_QUALIFIED_NAME = "fullyQualifiedName";
    public final static String FIELD_CLASS_PACKAGE = "package";
    public final static String FIELD_CLASS_NAME = "name";

    // the json looks like

    /*
    {
        "_doc": "<documentId>",
        "_system": {
            ...system
        },

        "principalId": "<principalId>",
        "repositoryId": "<repositoryId>",
        "branchId": "<branchId>",

        "level": "ERROR",
        "thread": "main",
        "timestamp": {
            ... timestamp object ...
        },

        "message": "<message>",

        "filename": "List.java",
        "method": "print",
        "line": 123,
        "class": {
            "qualifiedName": "java.util.List",
            "package": "java.util",
            "name": "List"
        },

        "throwables": [{
            "message": "<message>"
            "stacktrace": [{
                "filename": "List.java",
                "method": "print",
                "line": 123,
                "class": {
                    "qualifiedName": "java.util.List",
                    "package": "java.util",
                    "name": "List"
                }
            }]
        }]
    }
    */

    public String getLogPrincipalId();
    public String getLogRepositoryId();
    public String getLogBranchId();

    public String getLevel();
    public String getThread();
    public ObjectNode getTimestamp();

    public String getMessage();

    public String getFileName();
    public String getMethod();
    public int getLineNumber();
    public String getClassFullyQualifiedName();
    public String getClassPackage();
    public String getClassName();

    public List<LogThrowable> getLogThrowables();
}
