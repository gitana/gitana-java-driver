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
 *   info@cloudcms.com
 */
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.transfer.TransferImportStrategy;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class DraftMasterCopy2Test extends AbstractTestCase
{
    @Test
    public void test()
        throws Exception
    {
        // authenticate as admin
        Platform platform = new Gitana().authenticate("admin", "admin");

        // create repository #1 (live)
        Repository repository1 = platform.createRepository();

        // create repository #2 (draft)
        Repository repository2 = platform.createRepository();

        // live branch
        Branch master = repository1.readBranch("master");

        // draft branch
        Branch draft = repository2.readBranch("master");

        // on the draft branch, create a node
        ObjectNode data = ClasspathUtil.objectFromClasspath("org/gitana/platform/client/sample.json");
        Node draftNode = (Node) draft.createNode(data);
        draftNode.set("version", "1");
        draftNode.update();

        // put three attachments onto the node
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg");
        draftNode.uploadAttachment("attach1", bytes, "image/jpeg");
        draftNode.uploadAttachment("attach2", bytes, "image/jpeg");
        draftNode.uploadAttachment("attach3", bytes, "image/jpeg");

        // assert - node exists on draft, not on master
        assertEquals(1, draft.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(0, master.queryNodes(JSONBuilder.start("version").is("1").get()).size());

        // copy the draft node to the master branch
        draftNode.copy(master, TransferImportStrategy.CLONE, null);

        // assert - node exists on draft and on master
        assertEquals(1, draft.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(1, master.queryNodes(JSONBuilder.start("version").is("1").get()).size());

        // update on draft
        draftNode.set("version", "2");
        draftNode.update();

        // assert - node exists on draft (v2) and on master (v1)
        assertEquals(0, draft.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(1, draft.queryNodes(JSONBuilder.start("version").is("2").get()).size());
        assertEquals(1, master.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(0, master.queryNodes(JSONBuilder.start("version").is("2").get()).size());

        // copy the draft node to the master branch
        draftNode.copy(master, TransferImportStrategy.CLONE, null);

        // assert - node exists on draft (v2) and on master (v2)
        assertEquals(0, draft.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(1, draft.queryNodes(JSONBuilder.start("version").is("2").get()).size());
        assertEquals(0, master.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(1, master.queryNodes(JSONBuilder.start("version").is("2").get()).size());

        // find the node on master
        // make sure it has three attachments
        Node copiedNode = (Node) master.queryNodes(JSONBuilder.start("version").is("2").get()).values().iterator().next();
        assertEquals(3, copiedNode.listAttachments().size());

    }
}
