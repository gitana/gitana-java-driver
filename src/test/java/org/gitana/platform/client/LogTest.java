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

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.support.QName;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author uzi
 */
@Ignore
public class LogTest extends AbstractTestCase
{
    @Test
    public void testLogs()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate as "admin"
        Platform platform = gitana.authenticate("admin", "admin");

        // create a user on default domain
        DomainUser user = platform.readDomain("default").createUser("testuser-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);

        // create a tenant for this user
        Tenant tenant = platform.readRegistrar("default").createTenant(user, "unlimited");
        ObjectNode defaultClientObject = tenant.readDefaultAllocatedClientObject();
        String clientKey = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_KEY);
        String clientSecret = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_SECRET);



        //
        // AUTHENTICATE AS THE TENANT USER
        //

        gitana = new Gitana(clientKey, clientSecret);
        platform = gitana.authenticate(user.getName(), TestConstants.TEST_PASSWORD);

        // build a stack with repository, domain, vault...
        Stack stack1 = platform.createStack();
        Repository repository1 = platform.createRepository();
        stack1.assignDataStore(repository1);
        Domain domain1 = platform.createDomain();
        stack1.assignDataStore(domain1);
        Vault vault1 = platform.createVault();
        stack1.assignDataStore(vault1);

        // check stack datastore size
        assertEquals(3, stack1.listDataStores().size());

        // build another stack for giggles
        Stack stack2 = platform.createStack();
        Repository repository2 = platform.createRepository();
        stack2.assignDataStore(repository2);


        // check the base size of the platform logs
        int platformSize1 = platform.queryLogEntries(JsonUtil.createObject()).size();

        // check the base size of the stack #1 logs
        int stack1Size1 = stack1.queryLogEntries(JsonUtil.createObject()).size();

        // check the base size of the stack #2 logs
        int stack2Size1 = stack2.queryLogEntries(JsonUtil.createObject()).size();


        // now generate a log message on stack #1

        Branch master = repository1.readBranch("master");

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


        // verify that platform logs are larger by (at least) 1
        int platformSize2 = platform.queryLogEntries(JsonUtil.createObject()).size();
        assertTrue(platformSize2 - platformSize1 >= 1);

        // verify that stack #1 logs are larger by (at least) 1
        int stack1Size2 = stack1.queryLogEntries(JsonUtil.createObject()).size();
        assertTrue(stack1Size2 - stack1Size1 >= 1);

        // verify that stack #2 logs are unchanged
        int stack2Size2 = stack2.queryLogEntries(JsonUtil.createObject()).size();
        assertTrue(stack2Size2 - stack2Size1 == 0);

    }
}
