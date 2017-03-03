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

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.support.QName;
import org.gitana.util.ClasspathUtil;

/**
 * @author uzi
 */
public class NodeImageUploadRunner extends AbstractRunner<Void>
{
    private String repositoryId = null;
    private String branchId = null;

    private Branch branch = null;

    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    public void setBranchId(String branchId)
    {
        this.branchId = branchId;
    }

    public NodeImageUploadRunner(String id)
    {
        super(id);
    }

    @Override
    protected void doBeforeExecute() throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // branch reference
        branch = platform.readRepository(repositoryId).readBranch(branchId);
    }

    @Override
    protected void doAfterExecute() throws Exception
    {
        DriverContext.releaseDriver();
        branch = null;
    }

    @Override
    protected Void doExecute() throws Exception
    {
        // create a node
        Node node = (Node) branch.createNode(QName.create("custom:type"));

        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/load/IPCIMMGLPICT000000274104.JPEG");

        // add an attachment
        node.uploadAttachment("image", bytes, MimeTypeMap.IMAGE_JPEG);

        // generate the preview image
        String previewUrl = "/repositories/" + node.getRepositoryId() + "/branches/" + node.getBranchId() + "/nodes/" + node.getId() + "/preview/512?attachment=image&mimetype=image/png&size=512";
        DriverContext.getDriver().getRemote().downloadBytes(previewUrl);

        System.out.println("Thread: " + Thread.currentThread().getId() + ", runner: " + getId() + ", completed");

        return null;
    }
}
