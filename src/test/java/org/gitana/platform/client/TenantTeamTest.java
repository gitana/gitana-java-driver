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

import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class TenantTeamTest extends AbstractTestCase
{
    @Test
    public void testTenantTeams()
    {
        /////////////////////////////////////////////////////////////////////////////////////////////
        //
        // authenticate as ADMIN
        //

        Gitana gitana = new Gitana();
        
        Platform platform = gitana.authenticate("admin", "admin");

        // create user #1
        DomainUser user1 = platform.readDomain("default").createUser("user1-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);
        // create user #2
        DomainUser user2 = platform.readDomain("default").createUser("user2-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // create a tenant for user #1
        Tenant tenant1 = registrar.createTenant(user1, "unlimited");

        // create a tenant for user #2
        Tenant tenant2 = registrar.createTenant(user2, "unlimited");
        

        // verify that user1's identity has multiple users (user1 in default and user1prime in tenant1 domain)
        ResultMap<ObjectNode> userObjects = user1.readIdentity().findPolicyUserObjects();
        assertEquals(2, userObjects.size());

        // verify we can find tenants with this user identity
        // there will be 2 for user1 (the tenant we created above + the admin tenant)
        ResultMap<ObjectNode> tenantObjects = user1.readIdentity().findPolicyTenantObjects(registrar);
        assertEquals(2, tenantObjects.size());
        
        // verify we can pick out the account this user has on tenant1
        ObjectNode foundTenantObject = tenantObjects.get(tenant1.getId());
        assertEquals(tenant1.getId(), foundTenantObject.get(Tenant.FIELD_ID).textValue());
        ObjectNode user1inTenant1Object = user1.readIdentity().findPolicyUserObjectForTenant(foundTenantObject.get(Tenant.FIELD_ID).textValue());
        assertNotNull(user1inTenant1Object);
        assertNotNull(userObjects.get(user1inTenant1Object.get(Tenant.FIELD_ID).textValue()));

        // user1 has two identities right now
        userObjects = user1.readIdentity().findPolicyUserObjects();
        assertEquals(2, userObjects.size());


        /////////////////////////////////////////////////////////////////////////////////////////////
        //
        // authenticate as USER 2 (to tenant2)
        //

        // now sign into the tenant
        ObjectNode defaultClientObject2 = tenant2.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject2);
        String clientKey2 = JsonUtil.objectGetString(defaultClientObject2, Client.FIELD_KEY);
        String clientSecret2 = JsonUtil.objectGetString(defaultClientObject2, Client.FIELD_SECRET);
        gitana = new Gitana(clientKey2, clientSecret2);
        platform = gitana.authenticateOnTenant(user2, TestConstants.TEST_PASSWORD, tenant2.getId());

        // invite user 1 into user2's default domain
        DomainUser user1inTenant2 = platform.readPrimaryDomain().inviteUser(user1);



        /////////////////////////////////////////////////////////////////////////////////////////////
        //
        // authenticate as ADMIN
        //

        gitana = new Gitana();
        platform = gitana.authenticate("admin", "admin");

        // verify that user1's identity has multiple users (user1 in default, user1prime1 in tenant1 domain, user1prime2 in tenant2 domain)
        userObjects = user1.readIdentity().findPolicyUserObjects();
        assertEquals(3, userObjects.size());

        // verify we can find tenants with this user
        // there will be 3 (admin + tenant1 + tenant2)
        tenantObjects = user1.readIdentity().findPolicyTenantObjects(registrar);
        assertEquals(3, tenantObjects.size());

        // verify we can pick out the account this user has on tenant1
        ObjectNode userObject1inTenant1check = user1.readIdentity().findPolicyUserObjectForTenant(tenant1.getId());
        assertNotNull(userObject1inTenant1check);
        assertNotNull(userObjects.get(userObject1inTenant1check.get(DomainUser.FIELD_ID).textValue()));
        ObjectNode userObject1inTenant2check = user1.readIdentity().findPolicyUserObjectForTenant(tenant2.getId());
        assertNotNull(userObject1inTenant2check);
        assertNotNull(userObjects.get(userObject1inTenant2check.get(DomainUser.FIELD_ID).textValue()));
        
    }

}
