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
 *   info@gitana.io
 */
package org.gitana.platform.client;

import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class TenantTest extends AbstractTestCase
{
    @Test
    public void testTenantCRUD()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // count the current number of tenants
        int currentSize = registrar.listTenants(Pagination.limit(-1)).size();

        String tenantId = null;

        String userName = "user-" + System.currentTimeMillis();
        try
        {
            // create a domain
            Domain domain = platform.createDomain();

            // create a principal
            DomainUser user = domain.createUser(userName, TestConstants.TEST_PASSWORD);

            // create a tenant for this principal (starter plan)
            Tenant tenant = registrar.createTenant(user, "unlimited");
            tenantId = tenant.getId();

            // try to create another tenant with a plan that doesn't exist and assert we get an exception
            Exception ex = null;
            try
            {
                registrar.createTenant(user, "21k3j1k23j1l2");
            }
            catch (Exception exe)
            {
                ex = exe;
            }
            assertNotNull(ex);

            // count tenants
            assertEquals(currentSize + 1, registrar.listTenants(Pagination.limit(-1)).size());

            // update the tenant
            tenant.set("abc", "def");
            registrar.updateTenant(tenant);

            // read back and confirm
            Tenant testTenant = registrar.readTenant(tenant.getId());
            assertNotNull(testTenant);
            assertEquals("def", testTenant.getString("abc"));

            // delete the tenant
            registrar.deleteTenant(tenantId);

            // query and check sizes
            int newSize = registrar.queryTenants(JsonUtil.createObject(), Pagination.limit(-1)).size();
            assertEquals(currentSize, newSize);
        }
        finally
        {
            if (tenantId != null)
            {
                if (registrar.readTenant(tenantId) != null)
                {
                    registrar.deleteTenant(tenantId);
                }
            }
        }
    }

}
