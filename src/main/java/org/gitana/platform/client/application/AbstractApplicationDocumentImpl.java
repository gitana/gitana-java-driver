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

package org.gitana.platform.client.application;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.document.DocumentImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;

/**
 * @author uzi
 */
public abstract class AbstractApplicationDocumentImpl extends DocumentImpl implements ApplicationDocument
{
    private Application application;

    public AbstractApplicationDocumentImpl(Application application, ObjectNode obj, boolean isSaved)
    {
    	super(obj, isSaved);

        this.application = application;
    }

    public abstract String getResourceUri();

    protected ObjectFactory getFactory()
    {
        return getDriver().getFactory();
    }

    protected Driver getDriver()
    {
        return DriverContext.getDriver();
    }

    protected Remote getRemote()
    {
        return getDriver().getRemote();
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

    @Override
    public Platform getPlatform()
    {
        return getApplication().getPlatform();
    }

}
