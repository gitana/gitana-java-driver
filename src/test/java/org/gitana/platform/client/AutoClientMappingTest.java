/**
 * Copyright 2026 Gitana Software, Inc.
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

import org.gitana.platform.client.api.AuthenticationGrant;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.webhost.AutoClientMapping;
import org.gitana.platform.client.webhost.WebHost;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.junit.Test;

/**
 * Auto-client mapping tests
 * 
 * @author uzi
 */
public class AutoClientMappingTest extends AbstractTestCase
{
    @Test
    public void testAutoClientMappingTest()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default domain
        Domain domain = platform.readDomain("default");

        // create a user
        String userName1 = "testuser1_" + System.currentTimeMillis();
        DomainUser user1 = domain.createUser(userName1, TestConstants.TEST_PASSWORD);

        // create client + app #1
        Application application1 = platform.createApplication();
        Client client1 = platform.createClient();
        AuthenticationGrant authGrant1 = platform.createAuthenticationGrant(client1, user1);

        // create client + app #2
        Application application2 = platform.createApplication();
        Client client2 = platform.createClient();
        AuthenticationGrant authGrant2 = platform.createAuthenticationGrant(client1, user1);

        // create client + app #3
        Application application3 = platform.createApplication();
        Client client3 = platform.createClient();
        AuthenticationGrant authGrant3 = platform.createAuthenticationGrant(client1, user1);

        // create a web host
        WebHost webhost = platform.createWebHost();

        // create the following mappings
        //
        //      abc.test.com / appKey1                            -> app1, client1
        //      private.company.com / appKey2                     -> app2, client2
        //      abc.test.com / appKey3                            -> app3, client3
        //
        
        String host1 = "abc.test.com";
        String host2 = "private.company.com";

        String appKey1 = "appKey1";
        String appKey2 = "appKey2";
        String appKey3 = "appKey3";

        webhost.createAutoClientMapping(host1, appKey1, application1.getId(), client1.getId(), authGrant1.getKey());
        webhost.createAutoClientMapping(host2, appKey2, application2.getId(), client2.getId(), authGrant2.getKey());
        webhost.createAutoClientMapping(host1, appKey3, application3.getId(), client3.getId(), authGrant3.getKey());
        
        // now test out querying by host / appKey
        ResultMap<AutoClientMapping> mappings0 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_HOST).is(host1).and(AutoClientMapping.FIELD_APPLICATION_KEY).is(appKey1).get());
        assertEquals(1, mappings0.size());
        ResultMap<AutoClientMapping> mappings1 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_HOST).is(host2).and(AutoClientMapping.FIELD_APPLICATION_KEY).is(appKey2).get());
        assertEquals(1, mappings1.size());
        ResultMap<AutoClientMapping> mappings2 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_HOST).is(host1).and(AutoClientMapping.FIELD_APPLICATION_KEY).is(appKey3).get());
        assertEquals(1, mappings2.size());
        ResultMap<AutoClientMapping> mappings3 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_HOST).is(host1).and(AutoClientMapping.FIELD_APPLICATION_KEY).is("blah").get());
        assertEquals(0, mappings3.size());

        // test our querying by app
        ResultMap<AutoClientMapping> mappings10 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_APPLICATION_ID).is(application1.getId()).get());
        assertEquals(1, mappings10.size());
        ResultMap<AutoClientMapping> mappings11 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_APPLICATION_ID).is(application2.getId()).get());
        assertEquals(1, mappings11.size());
        ResultMap<AutoClientMapping> mappings12 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_APPLICATION_ID).is(application3.getId()).get());
        assertEquals(1, mappings12.size());
        ResultMap<AutoClientMapping> mappings13 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_APPLICATION_ID).is("blah").get());
        assertEquals(0, mappings13.size());

        // test our querying by client
        ResultMap<AutoClientMapping> mappings20 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_CLIENT_KEY).is(client1.getKey()).get());
        assertEquals(1, mappings20.size());
        ResultMap<AutoClientMapping> mappings21 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_CLIENT_KEY).is(client2.getKey()).get());
        assertEquals(1, mappings21.size());
        ResultMap<AutoClientMapping> mappings22 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_CLIENT_KEY).is(client3.getKey()).get());
        assertEquals(1, mappings22.size());
        ResultMap<AutoClientMapping> mappings23 = webhost.queryAutoClientMappings(QueryBuilder.start(AutoClientMapping.FIELD_CLIENT_KEY).is("blah").get());
        assertEquals(0, mappings23.size());

    }

}
