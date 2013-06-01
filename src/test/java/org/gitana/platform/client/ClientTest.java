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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.JSONBuilder;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.services.api.GrantTypes;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author uzi
 */
public class ClientTest extends AbstractTestCase
{
    @Test
    public void testClients1()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // create a domain
        Domain domain = platform.createDomain();

        // create user #1 and tenant #1
        DomainUser user1 = domain.createUser("abc-" + System.currentTimeMillis(), "pw");
        Tenant tenant1 = registrar.createTenant(user1, "unlimited");
        ObjectNode defaultClientObject1 = tenant1.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject1);
        String clientKey1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_KEY);
        String clientSecret1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_SECRET);

        // authenticate as this new tenant + principal
        platform = new Gitana(clientKey1, clientSecret1).authenticate(user1.getName(), "pw");

        // count the # of client objects on the platform
        int startingSize = platform.listClients().size();
        assertEquals(1, startingSize); // should just be the default consumer

        // create client #1
        Client c1 = platform.createClient();
        c1.setTitle("My first client");
        c1.setDescription("xyz");
        c1.setAuthorizedGrantTypes(Arrays.asList(GrantTypes.AUTHORIZATION_CODE, GrantTypes.PASSWORD, GrantTypes.REFRESH_TOKEN));
        c1.setScope(Arrays.asList("api", "webdav"));
        c1.update();
        

        // create client #2
        Client c2 = platform.createClient();
        c2.setTitle("My second client");
        c2.setDescription("abc");
        c2.setAuthorizedGrantTypes(Arrays.asList(GrantTypes.AUTHORIZATION_CODE, GrantTypes.REFRESH_TOKEN));
        c2.setScope(Arrays.asList("api", "ftp"));
        c2.update();

        assertEquals(3, platform.listClients().size()); // 2 custom, 1 default



        // query #1
        ResultMap<Client> q1 = platform.queryClients(
                JSONBuilder.start(Client.FIELD_AUTHORIZED_GRANT_TYPES).is(GrantTypes.AUTHORIZATION_CODE).get());
        assertEquals(3, q1.size()); // two custom + default

        // query #2
        ResultMap<Client> q2 = platform.queryClients(
                JSONBuilder.start(Client.FIELD_SCOPE).is("webdav").get());
        assertEquals(1, q2.size());

        // query #3
        ResultMap<Client> q3 = platform.queryClients(
                JSONBuilder.start(Client.FIELD_DESCRIPTION).is("def").get());
        assertEquals(0, q3.size());



        // update
        c2.setDescription("def");
        c2.update();

        // query #4
        ResultMap<Client> q4 = platform.queryClients(
                JSONBuilder.start(Client.FIELD_DESCRIPTION).is("def").get());
        assertEquals(1, q4.size());



        // delete
        c2.delete();

        // query #5
        ResultMap<Client> q5 = platform.queryClients(
                JSONBuilder.start(Client.FIELD_DESCRIPTION).is("def").get());
        assertEquals(0, q5.size());



        assertEquals(2, platform.listClients().size()); // 1 custom, 1 default



        // delete client 1
        c1.delete();

        // count clients and ensure back to original count (which has the default client)
        int endingSize = platform.listClients().size();
        assertEquals(startingSize, endingSize);

    }
}
