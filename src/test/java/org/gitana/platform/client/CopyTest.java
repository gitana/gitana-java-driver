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
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Association;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.transfer.CopyJob;
import org.gitana.platform.services.association.Direction;
import org.gitana.platform.support.QName;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class CopyTest extends AbstractTestCase
{
    /*
    @Test
    public void testCopyDomain()
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

        // AUTHENTICATE AS THE TENANT USER
        gitana = new Gitana(clientKey, clientSecret);
        platform = gitana.authenticate(user.getName(), TestConstants.TEST_PASSWORD);


        /////////////////////////////////////////////////////////////////////////////


        // create a domain
        // and some users and groups
        Domain domain = platform.createDomain();
        domain.createUser("joe", TestConstants.TEST_PASSWORD);
        domain.createUser("bob", TestConstants.TEST_PASSWORD);
        domain.createGroup("guitarists");

        // make a copy of the domain
        String domainId2 = domain.copy(platform);
        Domain domain2 = platform.readDomain(domainId2);
        assertNotNull(domain2);
        assertEquals(2, domain2.listUsers().size());
        assertEquals(1, domain2.listGroups().size());
    }

    @Test
    public void testCopyRepository()
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

        // AUTHENTICATE AS THE TENANT USER
        gitana = new Gitana(clientKey, clientSecret);
        platform = gitana.authenticate(user.getName(), TestConstants.TEST_PASSWORD);


        /////////////////////////////////////////////////////////////////////////////


        // create a repository
        // and some nodes
        Repository repository = platform.createRepository();
        Branch branch = repository.readBranch("master");
        branch.createNode(JSONBuilder.start("prop1").is("val1").get());
        branch.createNode(JSONBuilder.start("prop1").is("val1").get());
        branch.createNode(JSONBuilder.start("prop1").is("val1").get());

        // make a copy of the repository
        String repositoryId2 = repository.copy(platform);
        Repository repository2 = platform.readRepository(repositoryId2);
        assertNotNull(repository2);
        assertEquals(3, repository2.readBranch("master").queryNodes(JSONBuilder.start("prop1").is("val1").get()).size());
    }
    */

    @Test
    public void testCopyNode()
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

        // AUTHENTICATE AS THE TENANT USER
        gitana = new Gitana(clientKey, clientSecret);
        platform = gitana.authenticate(user.getName(), TestConstants.TEST_PASSWORD);


        /////////////////////////////////////////////////////////////////////////////


        // create a repository
        // and some nodes
        Repository repository = platform.createRepository();
        Branch branch = repository.readBranch("master");
        Node rootNode1 = branch.rootNode();
        rootNode1.setTitle("ROOT NODE");
        rootNode1.update();

        Node container1 = (Node) branch.createNode();
        container1.setTitle("CONTAINER");
        container1.update();

        Association association1 = rootNode1.associate(container1, QName.create("a:child"));
        association1.setTitle("ROOT NODE to CONTAINER");
        association1.update();

        Node node1 = (Node) branch.createNode(JSONBuilder.start("prop1").is("val1").get());
        node1.setTitle("NODE 1");
        node1.update();

        Association association2 = container1.associate(node1, QName.create("a:child"));
        association2.setTitle("CONTAINER to NODE 1");
        association2.update();

        Node node2 = (Node) branch.createNode(JSONBuilder.start("prop1").is("val1").get());
        node2.setTitle("NODE 2");
        node2.update();

        Association association3 = container1.associate(node2, QName.create("a:child"));
        association3.setTitle("CONTAINER to NODE 2");
        association3.update();

        // make a copy of the container
        CopyJob job = container1.copy(rootNode1);
        String nodeId2 = job.getSingleImportTargetId();
        Node container2 = (Node) branch.readNode(nodeId2);
        assertNotNull(container2);
        assertEquals(1, container2.associations(QName.create("a:child"), Direction.INCOMING).size());
        assertEquals(2, container2.associations(QName.create("a:child"), Direction.OUTGOING).size());
    }

}
