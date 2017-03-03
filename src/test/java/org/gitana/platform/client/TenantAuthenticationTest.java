/**
 * Copyright 2017 Gitana Software, Inc.
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

import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class TenantAuthenticationTest extends AbstractTestCase
{
    @Test
    public void test1()
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
        String userName = "user1-" + System.currentTimeMillis();
        DomainUser user = domain.createUser(userName, TestConstants.TEST_PASSWORD);
        Tenant tenant = registrar.createTenant(user, "unlimited");
        ObjectNode defaultClientObject = tenant.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject);
        String clientKey = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_KEY);
        String clientSecret = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_SECRET);



        // 1. Authenticate as tenant (should work)
        gitana = new Gitana(clientKey, clientSecret);
        gitana.authenticateOnTenant(user, TestConstants.TEST_PASSWORD, tenant);


        // 2. Authenticate as admin and disable tenant
        gitana = new Gitana();
        gitana.authenticate("admin", "admin");
        tenant.setEnabled(false);
        tenant.update();


        // 3. Authenticate as tenant (should fail)
        Exception ex1 = null;
        try
        {
            gitana = new Gitana(clientKey, clientSecret);
            gitana.authenticateOnTenant(user, TestConstants.TEST_PASSWORD, tenant);
        }
        catch (Exception ex)
        {
            ex1 = ex;
        }
        assertNotNull(ex1);


        // 4. Authenticate as admin and enable tenant
        gitana = new Gitana();
        gitana.authenticate("admin", "admin");
        tenant.setEnabled(true);
        tenant.update();


        // 5. Authenticate as tenant (should work)
        gitana = new Gitana(clientKey, clientSecret);
        gitana.authenticateOnTenant(user, TestConstants.TEST_PASSWORD, tenant);

    }

}
