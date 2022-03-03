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
import org.gitana.platform.client.billing.BillingProviderConfiguration;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class BillingProviderConfigurationTest extends AbstractTestCase
{
    @Test
    public void test1()
        throws Exception
    {
        // authenticate as "admin"
        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // create a domain
        Domain domain = platform.createDomain();

        // create a principal
        DomainUser user = domain.createUser("test-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);

        // create a tenant for this principal (unlimited plan);
        Tenant tenant = registrar.createTenant(user, "unlimited");

        // get the client info
        ObjectNode defaultClientObject = tenant.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject);
        String clientKey = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_KEY);
        String clientSecret = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_SECRET);



        // authenticate as the tenant user
        gitana = new Gitana(clientKey, clientSecret);
        platform = gitana.authenticateOnTenant(user, TestConstants.TEST_PASSWORD, tenant);

        // query billing configurations
        ResultMap<BillingProviderConfiguration> configurations = platform.listBillingProviderConfigurations();
        assertEquals(0, configurations.size());

        // create a billing configuration #1
        ObjectNode configObject1 = JsonUtil.createObject();
        configObject1.put("abc", 1);
        configObject1.put("def", 2);
        BillingProviderConfiguration configuration1 = platform.createBillingProviderConfiguration("braintree", configObject1);
        assertNotNull(configuration1);

        // create a billing configuration #1
        ObjectNode configObject2 = JsonUtil.createObject();
        configObject2.put("abc", 1);
        configObject2.put("def", 3);
        BillingProviderConfiguration configuration2 = platform.createBillingProviderConfiguration("braintree", configObject2);
        assertNotNull(configuration2);

        // query billing configurations
        configurations = platform.listBillingProviderConfigurations();
        assertEquals(2, configurations.size());

        // query for match
        configurations = platform.queryBillingProviderConfigurations(QueryBuilder.start("abc").is(1).get());
        assertEquals(2, configurations.size());

        // query for match
        configurations = platform.queryBillingProviderConfigurations(QueryBuilder.start("def").is(3).get());
        assertEquals(1, configurations.size());

        // now try creating one for a billing provider id that doesn't exist
        Exception ex1 = null;
        try
        {
            platform.createBillingProviderConfiguration("shabbadoo", configObject2);
        }
        catch (Exception ex)
        {
            ex1 = ex;
        }
        assertNotNull(ex1);

        // query for match
        configurations = platform.queryBillingProviderConfigurations(QueryBuilder.start("def").is(3).get());
        assertEquals(1, configurations.size());

        // delete one
        configuration2.delete();

        // query billing configurations
        configurations = platform.listBillingProviderConfigurations();
        assertEquals(1, configurations.size());

        // query for match
        configurations = platform.queryBillingProviderConfigurations(QueryBuilder.start("abc").is(1).get());
        assertEquals(1, configurations.size());

        // query for match
        configurations = platform.queryBillingProviderConfigurations(QueryBuilder.start("def").is(3).get());
        assertEquals(0, configurations.size());

    }

}
