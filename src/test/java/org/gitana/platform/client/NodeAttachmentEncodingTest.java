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

import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

/**
 * Uploads an attachment of a ISO-8859-1 encoded text document.
 *
 * Retrieves the document using UTF-8 to verify it is different.
 *
 * @author uzi
 */
public class NodeAttachmentEncodingTest extends AbstractTestCase
{
    @Test
    public void test1()
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
        byte[] bytes1 = ClasspathUtil.bytesFromClasspath("org/gitana/platform/attachment/scholl-ISO-8859-1.txt");
        node.uploadAttachment("scholl", bytes1, "text/plain; charset=ISO-8859-1", "scholl.txt");

        // read the attachment using ISO-88591-1
        byte[] bytes2 = node.downloadAttachment("scholl");

        // the length of each byte array should be different
        // since they are in two different encodings
        assertTrue(bytes1.length != bytes2.length);
    }
}
