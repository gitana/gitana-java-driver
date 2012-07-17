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
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class TenantObjectTest extends AbstractTestCase
{
    @Test
    public void testObjectAllocationsPerTenant()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // create a domain
        Domain domain = platform.createDomain();

        // create a principal + tenant (#1)
        String userName1 = "user1-" + System.currentTimeMillis();
        DomainUser user1 = domain.createUser(userName1, "pw");
        Tenant tenant1 = registrar.createTenant(user1, "unlimited");
        ObjectNode defaultClientObject1 = tenant1.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject1);
        String clientKey1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_KEY);
        String clientSecret1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_SECRET);

        // create a principal + tenant (#2)
        String userName2 = "user2-" + System.currentTimeMillis();
        DomainUser user2 = domain.createUser(userName2, "pw");
        Tenant tenant2 = registrar.createTenant(user2, "unlimited");
        ObjectNode defaultClientObject2 = tenant2.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject2);
        String clientKey2 = JsonUtil.objectGetString(defaultClientObject2, Client.FIELD_KEY);
        String clientSecret2 = JsonUtil.objectGetString(defaultClientObject2, Client.FIELD_SECRET);


        //
        // now authenticate as the tenant principal #1
        //

        gitana = new Gitana(clientKey1, clientSecret1);
        platform = gitana.authenticateOnTenant(user1, "pw", tenant1);

        // now we create 12 things
        //
        //   5 repositories
        //   4 domains
        //   3 vaults
        //   2 consumers
        //   1 registrar

        // as we create these, they should be automatically allocated to our tenant

        platform.createRepository();
        platform.createRepository();
        platform.createRepository();
        platform.createRepository();
        platform.createRepository();
        platform.createDomain();
        platform.createDomain();
        platform.createDomain();
        platform.createDomain();
        platform.createVault();
        platform.createVault();
        platform.createVault();
        platform.createClient();
        platform.createClient();
        platform.createRegistrar();

        // validate via general queries
        assertEquals(5, platform.listRepositories().size());
        assertEquals(4+1, platform.listDomains().size()); // 1 custom, 1 default
        assertEquals(3+1, platform.listVaults().size()); // 3 custom, 1 default
        assertEquals(2+1, platform.listClients().size()); // 2 custom, 1 default
        assertEquals(1, platform.listRegistrars().size());




        //
        // now authenticate as the tenant principal #2
        //

        gitana = new Gitana(clientKey2, clientSecret2);
        platform = gitana.authenticate(user2.getName(), "pw");

        // now we create 15 things
        //
        //   1 repositories
        //   2 domains
        //   3 vaults
        //   4 consumers
        //   5 registrars

        // as we create these, they should be automatically allocated to our tenant

        platform.createRepository();
        platform.createDomain();
        platform.createDomain();
        platform.createVault();
        platform.createVault();
        platform.createVault();
        platform.createClient();
        platform.createClient();
        platform.createClient();
        platform.createClient();
        platform.createRegistrar();
        platform.createRegistrar();
        platform.createRegistrar();
        platform.createRegistrar();
        platform.createRegistrar();

        // validate via general queries
        assertEquals(1, platform.listRepositories().size());
        assertEquals(2+1, platform.listDomains().size()); // 2 custom, 1 default
        assertEquals(3+1, platform.listVaults().size()); // 3 custom, 1 default
        assertEquals(4+1, platform.listClients().size()); // 4 custom, 1 default
        assertEquals(5, platform.listRegistrars().size());
    }

}
