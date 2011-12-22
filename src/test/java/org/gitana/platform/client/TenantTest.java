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

import org.gitana.platform.client.management.Management;
import org.gitana.platform.client.management.ManagementImpl;
import org.gitana.platform.client.management.Tenant;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class TenantTest extends AbstractTestCase
{
    @Test
    public void testTenantTeams()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a user
        DomainUser user = platform.readDefaultDomain().createUser("user1-" + System.currentTimeMillis(), "pw");

        // create a tenant for this user
        Management management = new ManagementImpl(platform, "default");
        Tenant tenant = management.createTenant(user, "starter");

        // verify we can find tenants with this user
        ResultMap<Tenant> tenants = management.findTenantsWithPrincipalTeamMember(user, null);
        assertEquals(1, tenants.size());
    }
}
