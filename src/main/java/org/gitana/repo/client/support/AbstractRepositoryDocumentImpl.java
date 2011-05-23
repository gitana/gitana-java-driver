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
import org.gitana.repo.client.*;

/**
 * @author uzi
 */
public abstract class AbstractRepositoryDocumentImpl extends DocumentImpl implements RepositoryDocument
{
    private Driver driver;
    private Repository repository;

    protected AbstractRepositoryDocumentImpl(Driver driver, Repository repository, ObjectNode obj, boolean isSaved)
    {
    	super(obj, isSaved);

        this.driver = driver;
        this.repository = repository;
    }

    protected Remote getRemote()
    {
        return driver.getRemote();
    }

    protected ObjectFactory getFactory()
    {
        return this.driver.getFactory();
    }

    @Override
    public Repository getRepository()
    {
        return this.repository;
    }

    @Override
    public String getRepositoryId()
    {
        return getRepository().getId();
    }

    @Override
    public Server getServer()
    {
        return getRepository().getServer();
    }

}
