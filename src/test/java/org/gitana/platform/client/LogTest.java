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

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.log.LogEntry;
import org.gitana.platform.client.nodes.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class LogTest extends AbstractTestCase
{
    @Test
    public void testQuery()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");
        ResultMap<LogEntry> serverLogs1 = platform.queryLogEntries(JsonUtil.createObject());

        // create repo
        Repository repo = platform.createRepository();
        ResultMap<LogEntry> repoLogs1 = repo.queryLogEntries(JsonUtil.createObject());

        // master branch
        Branch master = repo.readBranch("master");
        ResultMap<LogEntry> branchLogs1 = master.queryLogEntries(JsonUtil.createObject());

        // create a script node that just logs
        Node scriptNode = (Node) master.createNode();
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/afterUpdateNodeLog.js");
        scriptNode.uploadAttachment(bytes, MimeTypeMap.APPLICATION_JAVASCRIPT);

        // bind the script as a behavior for a new node
        Node node = (Node) master.createNode();

        // associate the script as a behavior
        ObjectNode associationObject = JsonUtil.createObject();
        associationObject.put("policy", "p:afterUpdateNode");
        associationObject.put("scope", 0); // 0 = instance, 1 = class
        node.associate(scriptNode, QName.create("a:has_behavior"), associationObject);

        // now update the node
        node.update();

        // verify that branch logs are larger by 1
        ResultMap<LogEntry> branchLogs2 = master.queryLogEntries(JsonUtil.createObject());
        assertTrue(branchLogs2.totalRows() - branchLogs1.totalRows() == 1);

        // verify that repo logs are larger by 1
        ResultMap<LogEntry> repoLogs2 = repo.queryLogEntries(JsonUtil.createObject());
        assertTrue(repoLogs2.totalRows() - repoLogs1.totalRows() == 1);

        // verify that server logs are larger by 1
        ResultMap<LogEntry> serverLogs2 = platform.queryLogEntries(JsonUtil.createObject());
        assertTrue(serverLogs2.totalRows() - serverLogs1.totalRows() >= 1); // might be > 1

    }
}
