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

import org.gitana.http.HttpPayload;
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class PrincipalBulkAttachmentUploadTest extends AbstractTestCase
{
    @Test
    public void testBulkUpload()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Domain domain = platform.readDomain("default");

        // create a user
        String userId = "testuser-" + System.currentTimeMillis();
        DomainUser user = domain.createUser(userId, TestConstants.TEST_PASSWORD);

        // payload 1
        HttpPayload payload1 = new HttpPayload();
        payload1.setFilename("file1.jpg");
        payload1.setName("attachment1");
        payload1.setBytes(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg"));
        payload1.setContentType(MimeTypeMap.IMAGE_JPG);

        // payload 2
        HttpPayload payload2 = new HttpPayload();
        payload2.setFilename("file2.jpg");
        payload2.setName("attachment2");
        payload2.setBytes(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg"));
        payload2.setContentType(MimeTypeMap.IMAGE_JPG);

        // payload 3
        HttpPayload payload3 = new HttpPayload();
        payload3.setFilename("file3.jpg");
        payload3.setName("attachment3");
        payload3.setBytes(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg"));
        payload3.setContentType(MimeTypeMap.IMAGE_JPG);

        // upload attachments
        // test out an override of "attachment1" id to "spoof1"
        Map<String, String> params = new HashMap<String, String>();
        params.put("attachmentId_0", "spoof1");
        user.uploadAttachments(params, payload1, payload2, payload3);

        // list attachment
        ResultMap<Attachment> attachments = user.listAttachments();
        assertTrue(attachments.size() == 3);

        assertNull(attachments.get("attachment1"));
        assertNotNull(attachments.get("attachment2"));
        assertNotNull(attachments.get("attachment3"));
        assertNotNull(attachments.get("spoof1"));
    }

}
