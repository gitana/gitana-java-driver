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

import org.gitana.JSONBuilder;
import org.gitana.platform.client.api.Consumer;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class ConsumerTest extends AbstractTestCase
{
    @Test
    public void testConsumers1()
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
        Tenant tenant1 = registrar.createTenant(user1, "starter");
        Consumer tenantConsumer1 = tenant1.readDefaultConsumer();

        assertNotNull(tenantConsumer1);

        // authenticate as this new tenant + principal
        platform = new Gitana(tenantConsumer1.getKey(), tenantConsumer1.getSecret()).authenticate(user1.getDomainQualifiedName(), "pw");

        // count the # of consumer objects on the platform
        int startingSize = platform.listConsumers().size();
        assertEquals(1, startingSize); // should just be the default consumer

        // create a consumer
        Consumer c1 = platform.createConsumer(
                JSONBuilder.start(Consumer.FIELD_ALLOW_OPENDRIVER_AUTHENTICATION).is(true)
                        .and(Consumer.FIELD_ALLOW_TICKET_AUTHENTICATION).is(true)
                        .and(Consumer.FIELD_TITLE).is("My First Consumer")
                        .and(Consumer.FIELD_DESCRIPTION).is("xyz")
                        .get()
        );

        Consumer c2 = platform.createConsumer(
            JSONBuilder.start(Consumer.FIELD_ALLOW_OPENDRIVER_AUTHENTICATION).is(false)
                    .and(Consumer.FIELD_ALLOW_TICKET_AUTHENTICATION).is(true)
                    .and(Consumer.FIELD_TITLE).is("My Second Consumer")
                    .and(Consumer.FIELD_DESCRIPTION).is("abc")
                    .get()
        );

        assertEquals(3, platform.listConsumers().size()); // 2 custom, 1 default



        // query #1
        ResultMap<Consumer> q1 = platform.queryConsumers(
                JSONBuilder.start(Consumer.FIELD_ALLOW_OPENDRIVER_AUTHENTICATION).is(true).get());
        assertEquals(1, q1.size());

        // query #2
        ResultMap<Consumer> q2 = platform.queryConsumers(
                JSONBuilder.start(Consumer.FIELD_ALLOW_TICKET_AUTHENTICATION).is(true).get());
        assertTrue(q2.size() >= 2);

        // query #3
        ResultMap<Consumer> q3 = platform.queryConsumers(
                JSONBuilder.start(Consumer.FIELD_DESCRIPTION).is("def").get());
        assertEquals(0, q3.size());



        // update
        c2.set(Consumer.FIELD_DESCRIPTION, "def");
        c2.update();

        // query #4
        ResultMap<Consumer> q4 = platform.queryConsumers(
                JSONBuilder.start(Consumer.FIELD_DESCRIPTION).is("def").get());
        assertEquals(1, q4.size());



        // delete
        c2.delete();

        // query #5
        ResultMap<Consumer> q5 = platform.queryConsumers(
                JSONBuilder.start(Consumer.FIELD_DESCRIPTION).is("def").get());
        assertEquals(0, q5.size());



        assertEquals(2, platform.listConsumers().size()); // 1 custom, 1 default



        // delete consumer 1
        c1.delete();

        // count consumers and ensure back to original count (which has the default consumer)
        int endingSize = platform.listConsumers().size();
        assertEquals(startingSize, endingSize);

    }
}
