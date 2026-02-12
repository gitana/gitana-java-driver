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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.project.Project;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.transfer.CopyJob;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.services.transfer.TransferImportConfiguration;
import org.gitana.platform.services.transfer.TransferImportStrategy;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class MultiProjectCopyNodeTest
    extends AbstractTestCase
{
    private static QName QNAME_A_CHILD = QName.create("a:child");

    private Node createFolder(Branch branch, Node parent, String name)
    {
        ObjectNode o = JsonUtil.createObject();
        o.put("title", name);

        Node node = (Node) branch.createNode(o);

        node.addFeature("f:filename", JSONBuilder.start("filename").is(name).get());
        node.addFeature("f:container", JsonUtil.createObject());
        node.update();

        parent.associate(node, QNAME_A_CHILD, Directionality.DIRECTED);

        return node;
    }

    private Node createFile(Branch branch, Node parent, String name)
        throws IOException
    {
        ObjectNode o = JsonUtil.createObject();
        o.put("title", name);
        o.put("category", "first");

        Node node = (Node) branch.createNode(o);
        node.addFeature("f:filename", JSONBuilder.start("filename").is(name).get());
        node.update();

        parent.associate(node, QNAME_A_CHILD, Directionality.DIRECTED);

        // upload attachment
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/gone.pdf");
        node.uploadAttachment(bytes, "application/pdf");

        return node;
    }

    @Test
    public void test()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate as "admin"
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // create a tenant user
        DomainUser user = platform.readDomain("default").createUser("user-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);

        // create a tenant for tenatn user
        Tenant tenant = registrar.createTenant(user, "unlimited");

        // now sign into the tenant
        ObjectNode defaultClientObject2 = tenant.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject2);
        String clientKey2 = JsonUtil.objectGetString(defaultClientObject2, Client.FIELD_KEY);
        String clientSecret2 = JsonUtil.objectGetString(defaultClientObject2, Client.FIELD_SECRET);
        gitana = new Gitana(clientKey2, clientSecret2);
        platform = gitana.authenticateOnTenant(user, TestConstants.TEST_PASSWORD, tenant.getId());
        Driver driver0 = DriverContext.getDriver();



        // NOTE: platform = tenant platform





        // create project #1
        // give it a project
        // get the API keys
        Project project1 = platform.createProject();
        ObjectNode appObject1 = JsonUtil.createObject();
        appObject1.put("projectId", project1.getId());
        ObjectNode deployments1 = JsonUtil.createObject();
        ObjectNode defaultDeployment1 = JsonUtil.createObject();
        defaultDeployment1.put("webhost", "primary");
        deployments1.put("default", defaultDeployment1);
        appObject1.put("deployments", deployments1);
        Application application1 = platform.createApplication(appObject1);
        Map<String, String> apiKeys1 = application1.readApiKeys("default");


        // create project #2
        // give it a project
        // get the API keys
        Project project2 = platform.createProject();
        ObjectNode appObject2 = JsonUtil.createObject();
        appObject2.put("projectId", project2.getId());
        ObjectNode deployments2 = JsonUtil.createObject();
        ObjectNode defaultDeployment2 = JsonUtil.createObject();
        defaultDeployment2.put("webhost", "primary");
        deployments2.put("default", defaultDeployment2);
        appObject2.put("deployments", deployments2);
        Application application2 = platform.createApplication(appObject2);
        Map<String, String> apiKeys2 = application2.readApiKeys("default");

        // authenticate to using API keys #1
        Gitana.connect(apiKeys1);
        Driver driver1 = DriverContext.getDriver();

        // authenticate to using API keys #2
        Gitana.connect(apiKeys2);
        Driver driver2 = DriverContext.getDriver();


        ////////////////////////////////////////////
        // using API Keys #1
        DriverContext.setDriver(driver1);

        // in project #1, create folders
        Repository repository1 = (Repository) project1.getStack().readDataStore("content");
        Branch master1 = repository1.readBranch("master");
        Node root1 = master1.rootNode();
        Node company1 = createFolder(master1, root1, "company");
        Node qa1 = createFolder(master1, company1, "qa");
        Node public1 = createFolder(master1, qa1, "public");
        Node test1 = createFile(master1, public1, "test");

        ////////////////////////////////////////////
        // using API Keys #2
        DriverContext.setDriver(driver2);

        // in project #2, create folders
        Repository repository2 = (Repository) project2.getStack().readDataStore("content");
        Branch master2 = repository2.readBranch("master");
        Node root2 = master2.rootNode();
        Node company2 = createFolder(master2, root2, "company");
        Node qa2 = createFolder(master2, company2, "staging");
        Node public2 = createFolder(master2, qa2, "public");

        ////////////////////////////////////////////
        // using API Keys #1
        DriverContext.setDriver(driver1);

        // copy settings
        TransferImportStrategy strategy = TransferImportStrategy.COPY_EVERYTHING;
        Map<String, Object> additionalConfiguration = new HashMap<String, Object>();
        additionalConfiguration.put(TransferImportConfiguration.FIELD_COPY_ON_EXISTING, false);

        // copy #1
        CopyJob copy1 = test1.copy(public2, strategy, additionalConfiguration);
        assertTrue(copy1.getSuccess());

        // copy #2
        CopyJob copy2 = test1.copy(public2, strategy, additionalConfiguration);
        assertTrue(copy2.getSuccess());

        // copy #3
        CopyJob copy3 = test1.copy(public2, strategy, additionalConfiguration);
        assertTrue(copy3.getSuccess());

        ////////////////////////////////////////////
        // using API Keys #2
        DriverContext.setDriver(driver2);

        // verify there is only one "test" folder in project2
        ObjectNode q = JsonUtil.createObject();
        q.put("category", "first");
        ResultMap<BaseNode> testNodes = master2.queryNodes(q);
        assertEquals(1, testNodes.size());
    }

}
