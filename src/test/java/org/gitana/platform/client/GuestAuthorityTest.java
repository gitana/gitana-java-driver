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

import org.gitana.http.HttpPayload;
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.ClasspathResource;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Marking as ignore since "guest" access is disabled by default within API.
 *
 * @author uzi
 */
@Ignore
public class GuestAuthorityTest extends AbstractTestCase
{
    @Test
    @Ignore
    public void testGuest()
        throws Exception
    {
        // create a tenant
        Platform platform = new Gitana().authenticate("admin", "admin");
        Registrar registrar = platform.readRegistrar("default");
        Domain tenantDomain = platform.createDomain();
        DomainUser tenantUser = tenantDomain.createUser("user-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);
        Tenant tenant = registrar.createTenant(tenantUser, "unlimited");
        ObjectNode tenantClientObject = tenant.readDefaultAllocatedClientObject();
        String tenantClientKey = JsonUtil.objectGetString(tenantClientObject, Client.FIELD_KEY);
        String tenantClientSecret = JsonUtil.objectGetString(tenantClientObject, Client.FIELD_SECRET);

        // connect to tenant as tenant user
        platform = new Gitana(tenantClientKey, tenantClientSecret).authenticate(tenantUser.getName(), TestConstants.TEST_PASSWORD);

        // create a repository, node and attachment
        Repository repository = platform.createRepository();
        Branch master = repository.readBranch("master");
        Node node = (Node) master.createNode();
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/avatar.png");
        node.uploadAttachment(bytes, MimeTypeMap.IMAGE_PNG);

        // create a client
        Client client = platform.createClient();

        // create TESTUSER and give consumer to platform + repository, but not node
        Domain domain = platform.createDomain();
        DomainUser user = domain.createUser("testuser-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);
        platform.grant(user, "consumer");
        repository.grant(user, "consumer");
        master.grant(user, "consumer");

        // log in as test user with the client and verify we can't access node
        platform = new Gitana(client.getKey(), client.getSecret()).authenticate(user, TestConstants.TEST_PASSWORD);
        Node emptyNode = (Node) platform.readRepository(repository.getId()).readBranch("master").readNode(node.getId());
        assertNull(emptyNode);

        // log in as tenant user again and grant guest user access to the node
        platform = new Gitana(tenantClientKey, tenantClientSecret).authenticate(tenantUser.getName(), TestConstants.TEST_PASSWORD);
        platform.grant("guest", "consumer");
        repository = platform.readRepository(repository.getId());
        repository.grant("guest", "consumer");
        master = repository.readBranch("master");
        master.grant("guest", "consumer");
        node = ((Node) master.readNode(node.getId()));
        node.grant("guest", "consumer");

        // log in as the guest user, verify we can read node + attachment
        platform = new Gitana(client.getKey(), client.getSecret()).authenticateAsGuest();
        node = (Node) platform.readRepository(repository.getId()).readBranch("master").readNode(node.getId());
        assertNotNull(node);
        bytes = node.downloadAttachment();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        // log in as tenant user again and switch off guest access on the client
        platform = new Gitana(tenantClientKey, tenantClientSecret).authenticate(tenantUser.getName(), TestConstants.TEST_PASSWORD);
        client = platform.readClient(client.getId());
        client.setAllowGuestLogin(false);
        client.update();

        // log in as the guest user, verify we can't log in
        Exception ex = null;
        try
        {
            platform = new Gitana(client.getKey(), client.getSecret()).authenticateAsGuest();
        }
        catch (Exception ex1)
        {
            ex = ex1;
        }
        assertNotNull(ex);
    }
}
