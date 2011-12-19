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

import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.management.Management;
import org.gitana.platform.client.management.ManagementImpl;
import org.gitana.platform.client.management.Tenant;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class ManagementTenantTest extends AbstractTestCase
{
    @Test
    public void testTenantCRUD()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Management management = new ManagementImpl(platform, "default");

        // count the current number of tenants
        int currentSize = management.listTenants().size();

        String tenantId = null;

        String userName = "user-" + System.currentTimeMillis();
        try
        {
            // create a domain
            Domain domain = platform.createDomain();

            // create a principal
            DomainUser user = domain.createUser(userName, "pw");

            // create a tenant for this principal (starter plan)
            Tenant tenant = management.createTenant(user, "starter");
            tenantId = tenant.getId();

            // count tenants
            assertEquals(currentSize + 1, management.listTenants().size());

            // update the tenant
            tenant.set("abc", "def");
            management.updateTenant(tenant);

            // read back and confirm
            Tenant testTenant = management.readTenant(tenant.getId());
            assertNotNull(testTenant);
            assertEquals("def", testTenant.getString("abc"));

            // delete the tenant
            management.deleteTenant(tenantId);

            // query and check sizes
            int newSize = management.queryTenants(JsonUtil.createObject()).size();
            assertEquals(currentSize, newSize);
        }
        finally
        {
            if (tenantId != null)
            {
                if (management.readTenant(tenantId) != null)
                {
                    management.deleteTenant(tenantId);
                }
            }
        }
    }

}
