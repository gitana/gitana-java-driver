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
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.gitana.util.StreamUtil;
import org.junit.Test;

import java.io.FileInputStream;

/**
 * @author uzi
 */
public class NodeAttachment2Test extends AbstractTestCase
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

        // define a custom content type
        QName IMAGE_TYPE_QNAME = QName.create("mm:image");
        master.defineType(IMAGE_TYPE_QNAME);

        // create a node
        ObjectNode object = JsonUtil.createObject();
        object.put("title", "Test Title");
        object.put("description", "Test Description");
        object.put("filename", "Test Filename");
        object.put("fid", "Test ID");
        object.put("localid", "Test Local ID");
        object.put("netid", "Test New ID");
        object.put("_parentFolderPath","/DEPARTMENTS/INFORMATION_TECHNOLOGY/MULTIMEDIA/PICTURES");
        object.put("_type", IMAGE_TYPE_QNAME.toString());
        Node n = (Node) master.createNode(object);

        // upload an attachment
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/avatar.png");
        n.uploadAttachment("default", bytes, "image/png");

        // read back
        Node node2 = (Node) master.readNode(n.getId());

        // it should have 1 attachment
        assertEquals(1, node2.listAttachments().size());
        Attachment attachment1 = node2.listAttachments().get("default");
        assertNotNull(attachment1);
        assertEquals("image/png", attachment1.getContentType());
        assertTrue(attachment1.getLength() > 0);

        // download attachment
        byte[] bytes2 = node2.downloadAttachment("default");
        assertTrue(bytes2.length > 0);
    }
}
