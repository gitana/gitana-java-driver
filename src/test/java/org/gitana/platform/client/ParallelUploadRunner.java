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
package org.gitana.platform.client;

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.load.AbstractRunner;
import org.gitana.util.ClasspathUtil;

/**
 * @author uzi
 */
public class ParallelUploadRunner extends AbstractRunner<Void>
{
    private Branch branch = null;
    private Driver originalDriver = null;

    public ParallelUploadRunner(String runnerId)
    {
        super(runnerId);
    }

    public void setDriver(Driver originalDriver)
    {
        this.originalDriver = originalDriver;
    }

    public void setBranch(Branch branch)
    {
        this.branch = branch;
    }

    @Override
    protected void doBeforeExecute() throws Exception
    {
        DriverContext.setDriver(this.originalDriver);
    }

    @Override
    protected void doAfterExecute() throws Exception
    {
        DriverContext.releaseDriver();
        this.branch = null;
    }

    @Override
    protected Void doExecute() throws Exception
    {
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/load/IPCIMMGLPICT000000274104.JPEG");

        long t1 = System.currentTimeMillis();

        // create a node and upload attachment
        Node node = (Node) branch.createNode();
        node.uploadAttachment("image", bytes, MimeTypeMap.IMAGE_JPEG);
        long t2 = System.currentTimeMillis();

        System.out.println("Thread: " + Thread.currentThread().getId() + ", runner: " + getId() + ", completed in: " + (t2-t1) + " ms");

        return null;
    }
}
