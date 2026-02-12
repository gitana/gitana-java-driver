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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.GitanaObjectImpl;
import org.gitana.util.JsonUtil;

/**
 * @author uzi
 */
public class LogStackTraceElement extends GitanaObjectImpl
{
    public final static String FIELD_FILENAME = "filename";
    public final static String FIELD_METHOD = "method";
    public final static String FIELD_LINE_NUMBER = "line";
    public final static String FIELD_CLASS = "class";
    public final static String FIELD_CLASS_FULLY_QUALIFIED_NAME = "fullyQualifiedName";
    public final static String FIELD_CLASS_PACKAGE = "package";
    public final static String FIELD_CLASS_NAME = "name";

    public LogStackTraceElement(ObjectNode object)
    {
        super(object);
    }

    public String getFilename()
    {
        return getString(FIELD_FILENAME);
    }

    public String getMethod()
    {
        return getString(FIELD_METHOD);
    }

    public int getLine()
    {
        return getInt(FIELD_LINE_NUMBER);
    }

    public String getClassFullyQualifiedName()
    {
        ObjectNode classObject = getObject(FIELD_CLASS);

        return JsonUtil.objectGetString(classObject, FIELD_CLASS_FULLY_QUALIFIED_NAME);
    }

    public String getClassPackage()
    {
        ObjectNode classObject = getObject(FIELD_CLASS);

        return JsonUtil.objectGetString(classObject, FIELD_CLASS_PACKAGE);

    }

    public String getClassName()
    {
        ObjectNode classObject = getObject(FIELD_CLASS);

        return JsonUtil.objectGetString(classObject, FIELD_CLASS_NAME);
    }

}