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

import org.gitana.platform.client.api.Consumer;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.management.Allocation;
import org.gitana.platform.client.management.Management;
import org.gitana.platform.client.management.ManagementImpl;
import org.gitana.platform.client.management.Tenant;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class ManagementAllocationTest extends AbstractTestCase
{
    @Test
    public void testAllocation()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // management
        Management management = new ManagementImpl(platform, "default");

        // create a domain
        Domain domain = platform.createDomain();

        // create a principal
        String userName = "user-" + System.currentTimeMillis();
        DomainUser user = domain.createUser(userName, "pw");

        // grant this principal MANAGER role to the server
        // that way, we don't worry about permissions in this test
        platform.grant(user.getDomainQualifiedName(), "manager");

        // create a tenant for this principal
        Tenant tenant = management.createTenant(user, "starter");
        //System.out.println("TENANT: " + tenant.getId());

        // when we created a tenant, a default consumer should have been created for this tenant
        Consumer consumer = platform.lookupDefaultConsumerForTenant(tenant.getId());
        //System.out.println("CONSUMER: " + consumer.getKey());
        assertNotNull(consumer);


        //
        // now authenticate as the new principal
        //

        gitana = new Gitana(consumer.getKey(), consumer.getSecret());
        platform = gitana.authenticate(user.getDomainQualifiedName(), "pw");

        // now we create 11 things
        //
        //   2 repositories
        //   3 domains
        //   4 vaults
        //   2 consumers

        // as we create these, they should be automatically allocated to our tenant

        Repository repository1 = platform.createRepository();
        Repository repository2 = platform.createRepository();
        Domain domain1 = platform.createDomain();
        Domain domain2 = platform.createDomain();
        Domain domain3 = platform.createDomain();
        Vault vault1 = platform.createVault();
        Vault vault2 = platform.createVault();
        Vault vault3 = platform.createVault();
        Vault vault4 = platform.createVault();
        Consumer consumer1 = platform.createConsumer();
        Consumer consumer2 = platform.createConsumer();


        //
        // now authenticate again as the admin user
        //

        gitana = new Gitana();
        platform = gitana.authenticate("admin", "admin");

        // validate the allocations
        ResultMap<Allocation> allocations = tenant.listAllocations();
        assertEquals(11+1, allocations.size()); // should be 11 + our default consumer (1)

        // test out independent methods
        assertEquals(2, tenant.listRepositories().size());
        assertEquals(3, tenant.listDomains().size());
        assertEquals(4, tenant.listVaults().size());
        assertEquals(2+1, tenant.listConsumers().size()); // + 1 for default

        // delete the third domain
        domain3.delete();

        // validate the allocations
        allocations = tenant.listAllocations();
        assertEquals(10+1, allocations.size()); // should be 10 + our default consumer (1)

        // manually deallocate vault4
        tenant.deallocate("vault", vault4.getId());

        // validate the allocations
        allocations = tenant.listAllocations();
        assertEquals(9+1, allocations.size()); // should be 9 + our default consumer (1)

        // manually allocate vault4
        tenant.allocate("vault", vault4.getId());

        // validate the allocations
        allocations = tenant.listAllocations();
        assertEquals(10+1, allocations.size()); // should be 10 + our default consumer (1)


    }

}
