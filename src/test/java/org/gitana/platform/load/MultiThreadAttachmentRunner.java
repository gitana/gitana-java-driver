/**
 * Copyright 2017 Gitana Software, Inc.
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

package org.gitana.platform.load;

import org.gitana.platform.client.Driver;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.support.DriverContext;

/**
 * @author uzi
 */
public class MultiThreadAttachmentRunner extends AbstractRunner<RunnerResponse>
{
    private Driver driver = null;
    private BaseNode node = null;

    public void setDriver(Driver driver)
    {
        this.driver = driver;
    }

    public void setNode(BaseNode node)
    {
        this.node = node;
    }

    public MultiThreadAttachmentRunner(String id)
    {
        super(id);
    }

    @Override
    protected void doBeforeExecute() throws Exception
    {
        DriverContext.setDriver(driver);
    }

    @Override
    protected void doAfterExecute() throws Exception
    {
        DriverContext.releaseDriver();
        node = null;
        driver = null;
    }

    @Override
    protected RunnerResponse doExecute() throws Exception
    {
        long t1 = System.currentTimeMillis();
        this.node.downloadAttachment();
        long t2 = System.currentTimeMillis();

        return new RunnerResponse(t2 - t1);
    }
}
