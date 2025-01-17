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
 *   info@gitana.io
 */
package org.gitana.platform.client.cluster;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.Driver;
import org.gitana.platform.client.document.DocumentImpl;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.services.reference.Reference;

/**
 * @author uzi
 */
public abstract class AbstractClusterDocumentImpl extends DocumentImpl implements ClusterDocument
{
    private Cluster cluster;

    public AbstractClusterDocumentImpl(Cluster cluster, ObjectNode obj, boolean isSaved)
    {
    	super(obj, isSaved);

        this.cluster = cluster;
    }

    @Override
    public Reference ref()
    {
        return Reference.create(getTypeId(), "default", getId());
    }

    protected abstract String getResourceUri();

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
    public Cluster getCluster()
    {
        return this.cluster;
    }

    @Override
    public String getClusterId()
    {
        return getCluster().getId();
    }

}
