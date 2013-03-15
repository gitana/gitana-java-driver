/**
 * Copyright 2010 Gitana Software, Inc.
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
 *   info@gitanasoftware.com
 */

package org.gitana.platform.client;

import org.gitana.http.HttpPayload;
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author uzi
 */
public class NodeMultiCreateTest extends AbstractTestCase
{
    @Test
    public void testBulkCreate()
        throws IOException
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // payload 1
        HttpPayload payload1 = new HttpPayload();
        payload1.setFilename("file1.jpg");
        payload1.setName("part1");
        payload1.setBytes(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg"));
        payload1.setContentType(MimeTypeMap.IMAGE_JPG);

        // payload 2
        HttpPayload payload2 = new HttpPayload();
        payload2.setFilename("file2.jpg");
        payload2.setName("part2");
        payload2.setBytes(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg"));
        payload2.setContentType(MimeTypeMap.IMAGE_JPG);

        // payload 3
        HttpPayload payload3 = new HttpPayload();
        payload3.setFilename("file3.jpg");
        payload3.setName("part3");
        payload3.setBytes(ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg"));
        payload3.setContentType(MimeTypeMap.IMAGE_JPG);

        // pass in some properties for each via request parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("property0__title", "title1");
        params.put("property0__description", "description1");
        params.put("property1__title", "title2");
        params.put("property1__description", "description2");
        params.put("property2__title", "title3");
        params.put("property2__description", "description3");

        // bulk create three nodes
        Map<String, BaseNode> nodes = master.createNodes(params, payload1, payload2, payload3);
        assertTrue(nodes.size() == 3);

        // validate that all three have the right properties and attachments
        int count = 0;
        Iterator<BaseNode> it = nodes.values().iterator();
        while (it.hasNext())
        {
            Node node = (Node) it.next();

            assertEquals("title" + (count+1), node.getTitle());
            assertEquals("description" + (count+1), node.getDescription());

            assertTrue(node.listAttachments().size() == 1);

            count++;
        }
    }

}
