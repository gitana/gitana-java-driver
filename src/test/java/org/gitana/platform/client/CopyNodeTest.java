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
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.services.transfer.TransferImportConfiguration;
import org.gitana.platform.services.transfer.TransferImportStrategy;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class CopyNodeTest extends AbstractTestCase
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
    {
        ObjectNode o = JsonUtil.createObject();
        o.put("title", name);

        Node node = (Node) branch.createNode(o);
        node.addFeature("f:filename", JSONBuilder.start("filename").is(name).get());

        parent.associate(node, QNAME_A_CHILD, Directionality.DIRECTED);

        return node;
    }

    private Node getChild(Node parent, String name)
    {
        List<Node> children = new ArrayList<Node>();

        ResultMap<Association> associations = parent.associations(QNAME_A_CHILD, Direction.OUTGOING);
        for (Association association: associations.values())
        {
            Node targetNode = association.getTargetNode();

            if (name.equals(getFilename(targetNode)))
            {
                children.add(targetNode);
            }
        }

        if (children.size() > 1)
        {
            throw new RuntimeException("Found multiple children with name: " + name + " when only expected 1");
        }

        return children.get(0);
    }

    private String getFilename(Node node)
    {
        String filename = node.getId();

        if (node.hasFeature("f:filename"))
        {
            ObjectNode c = node.getFeature("f:filename");
            if (c != null)
            {
                filename = JsonUtil.objectGetString(c, "filename");
            }
        }

        return filename;
    }


    @Test
    public void test()
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
        Repository repository = platform.createRepository();
        Branch branch = repository.readBranch("master");
        Node rootNode = branch.rootNode();

        // create devFolder, qaFolder, liveFolder
        Node devFolder = createFolder(branch, rootNode, "dev");
        Node qaFolder = createFolder(branch, rootNode, "qa");
        Node liveFolder = createFolder(branch, rootNode, "live");

        // create a node in dev
        Node devFile = createFile(branch, devFolder, "file.txt");

        // set up copy config so that we don't create duplicates when we find existing targets
        Map<String, Object> additionalConfiguration = new HashMap<String, Object>();
        additionalConfiguration.put(TransferImportConfiguration.FIELD_COPY_ON_EXISTING, false);

        // explicitly copy to qa
        CopyJob job1 = devFile.copy(qaFolder, TransferImportStrategy.COPY_EVERYTHING, additionalConfiguration);
        assertTrue(job1.getSuccess());

        // check exists in qa
        Node qaFile = getChild(qaFolder, "file.txt");
        assertNotNull(qaFile);

        // explicitly copy to live
        CopyJob job2 = devFile.copy(liveFolder, TransferImportStrategy.COPY_EVERYTHING, additionalConfiguration);
        assertTrue(job2.getSuccess());

        // check exists in live
        Node liveFile = getChild(liveFolder, "file.txt");
        assertNotNull(liveFile);

        // make an update to devFile
        devFile.set("abc", 123);
        devFile.update();

        // explicitly copy to qa
        CopyJob job3 = devFile.copy(qaFolder, TransferImportStrategy.COPY_EVERYTHING, additionalConfiguration);
        assertTrue(job3.getSuccess());

        // validate in qa
        qaFile = getChild(qaFolder, "file.txt");
        assertNotNull(qaFile);
        assertEquals(123, qaFile.getInt("abc"));

        // explicitly copy to live
        CopyJob job4 = devFile.copy(liveFolder, TransferImportStrategy.COPY_EVERYTHING, additionalConfiguration);
        assertTrue(job4.getSuccess());

        // validate in live
        liveFile = getChild(liveFolder, "file.txt");
        assertNotNull(liveFile);
        assertEquals(123, liveFile.getInt("abc"));
    }

}
