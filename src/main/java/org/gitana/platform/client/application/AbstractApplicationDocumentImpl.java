/**
 * Copyright 2025 Gitana Software, Inc.
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
package org.gitana.platform.client.application;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.platform.AbstractPlatformDocumentImpl;
import org.gitana.platform.services.reference.Reference;

/**
 * @author uzi
 */
public abstract class AbstractApplicationDocumentImpl extends AbstractPlatformDocumentImpl implements ApplicationDocument
{
    private Application application;

    public AbstractApplicationDocumentImpl(Application application, ObjectNode obj, boolean isSaved)
    {
    	super(application.getPlatform(), obj, isSaved);

        this.application = application;
    }

    @Override
    public Reference ref()
    {
        return Reference.create(getTypeId(), getPlatformId(), getApplicationId(), getId());
    }

    @Override
    public Application getApplication()
    {
        return this.application;
    }

    @Override
    public String getApplicationId()
    {
        return getApplication().getId();
    }

}
