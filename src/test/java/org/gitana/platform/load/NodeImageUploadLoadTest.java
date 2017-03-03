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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * 10 concurrent users, creating nodes 1000
 *
 * @author uzi
 */
public class NodeImageUploadLoadTest extends AbstractLoadTest<Void>
{
    private String repositoryId = null;
    private String branchId = null;

    @Override
    protected Runner<Void> createRunner(String id)
    {
        NodeImageUploadRunner runner = new NodeImageUploadRunner(id);
        runner.setRepositoryId(repositoryId);
        runner.setBranchId(branchId);

        return runner;
    }

    @Override
    protected int getNumberOfRunners()
    {
        return 5;
    }

    @Override
    protected int getIterationCount()
    {
        return 100;
    }

    @Test
    public void test1()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();
        this.repositoryId = repository.getId();

        // branch
        Branch branch = repository.readBranch("master");
        this.branchId = branch.getId();

        // add watermark node
        byte[] watermarkBytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/load/watermark.png");
        Node watermarkNode = (Node) branch.createNode();
        watermarkNode.uploadAttachment(watermarkBytes, MimeTypeMap.IMAGE_PNG);

        // declare custom type
        // configure watermarkable node
        ObjectNode typeObject = ClasspathUtil.objectFromClasspath("org/gitana/platform/load/custom_type.json");
        ObjectNode mandatoryFeatures = JsonUtil.objectGetObject(typeObject, "mandatoryFeatures");
        ObjectNode feature = JsonUtil.objectGetObject(mandatoryFeatures, "f:watermarkable");
        //ObjectNode _previewImage = JsonUtil.objectGetObject(feature, "_preview_image_512_512");
        //_previewImage.put("watermarkNodeId", watermarkNode.getId());
        ObjectNode _preview_image_512_512 = JsonUtil.objectGetObject(feature, "_preview_image_512_512");
        _preview_image_512_512.put("watermarkNodeId", watermarkNode.getId());
        branch.createNode(typeObject);

        long t1 = System.currentTimeMillis();
        execute();
        long totalTime = System.currentTimeMillis() - t1;

        float timePerRunner = ((float) ((float) totalTime) / ((float) getIterationCount()));

        // read back the histogram
        System.out.println("Total time: " + totalTime + ", time per runner: " + timePerRunner);
    }

}
