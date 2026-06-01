/**
 * Copyright 2026 Gitana Software, Inc.
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

import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

/**
 * Tests out internationalized characters on attachment upload.
 * s
 * @author uzi
 */
public class NodeAttachment3Test extends AbstractTestCase
{
    @Test
    public void testAttachments()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repo = platform.createRepository();

        // master branch
        Branch master = repo.readBranch("master");

        // create a node
        Node node = (Node) master.createNode();

        // upload an attachment
        String filename = "员工福利说明.pdf";
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/" + filename);
        node.uploadAttachment("default", bytes, "application/pdf", filename);

        // list attachment
        ResultMap<Attachment> attachments = node.listAttachments();
        assertTrue(attachments.size() == 1);

        assertNotNull(attachments.get("default"));

        assertEquals("application/pdf", attachments.get("default").getContentType());
        assertEquals(bytes.length, attachments.get("default").getLength());
        String filename1 = attachments.get("default").getFilename();
        System.out.println("F: " + filename1);
        assertTrue(filename.equals(filename1));
    }
}
