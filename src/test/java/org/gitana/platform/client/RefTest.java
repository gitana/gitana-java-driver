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

import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.util.TestConstants;
import org.junit.Test;

/**
 * Tests out ref() method.
 *
 * @author uzi
 */
public class RefTest extends AbstractTestCase
{
    @Test
    public void testClients1()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");
        assertEquals(platform.getTypeId(), platform.ref().getType());
        assertEquals(platform.getId(), platform.ref().getId());

        // default registrar
        Registrar registrar = platform.readRegistrar("default");
        assertEquals(registrar.getTypeId(), registrar.ref().getType());
        assertEquals(registrar.getId(), registrar.ref().getId());

        // create a domain
        Domain domain = platform.createDomain();
        assertEquals(domain.getTypeId(), domain.ref().getType());
        assertEquals(domain.getId(), domain.ref().getId());

        // create user #1 and tenant #1
        DomainUser user1 = domain.createUser("abc-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);
        assertEquals(user1.getTypeId(), user1.ref().getType());
        assertEquals(user1.getId(), user1.ref().getId());

        Tenant tenant1 = registrar.createTenant(user1, "unlimited");
        assertEquals(tenant1.getTypeId(), tenant1.ref().getType());
        assertEquals(tenant1.getId(), tenant1.ref().getId());

        // create client
        Client client = platform.createClient();
        assertEquals(client.getTypeId(), client.ref().getType());
        assertEquals(client.getId(), client.ref().getId());

        // create repository
        Repository repository = platform.createRepository();
        assertEquals(repository.getTypeId(), repository.ref().getType());
        assertEquals(repository.getId(), repository.ref().getId());

        // branch
        Branch branch = repository.readBranch("master");
        assertEquals(branch.getTypeId(), branch.ref().getType());
        assertEquals(branch.getId(), branch.ref().getId());

        // node
        Node node = (Node) branch.createNode();
        assertEquals(node.getTypeId(), node.ref().getType());
        assertEquals(node.getId(), node.ref().getId());
    }
}
