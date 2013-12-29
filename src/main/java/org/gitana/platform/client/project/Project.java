/**
 * Copyright 2013 Gitana Software, Inc.
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

package org.gitana.platform.client.project;

import org.gitana.platform.client.attachment.Attachable;
import org.gitana.platform.client.platform.PlatformDocument;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.Selfable;

/**
 * @author uzi
 */
public interface Project extends PlatformDocument, AccessControllable, Selfable, Attachable
{
    public final static String FIELD_STACK_ID = "stackId";
    public final static String FIELD_PROJECT_TYPE = "projectType";
    public final static String FIELD_FAMILY = "family";

    public final static String FIELD_SHARED_STACK = "sharedStack";

    public String getStackId();
    public void setStackId(String stackId);

    public String getProjectType();
    public void setProjectType(String projectType);

    public String getFamily();
    public void setFamily(String projectFamily);

    public Stack getStack();

    public boolean getSharedStack();
    public void setSharedStack(boolean sharedStack);
}
