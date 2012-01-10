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

import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class TenantTeamTest extends AbstractTestCase
{
    @Test
    public void testTenantTeams()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a user
        DomainUser user = platform.readDomain("default").createUser("user1-" + System.currentTimeMillis(), "pw");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // create a tenant for this user
        Tenant tenant = registrar.createTenant(user, "starter");

        // verify we can find tenants with this user
        ResultMap<Tenant> tenants = registrar.findTenantsWithPrincipalTeamMember(user, null);
        assertEquals(1, tenants.size());
    }
}
