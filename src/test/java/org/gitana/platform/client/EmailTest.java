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
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.application.Email;
import org.gitana.platform.client.application.EmailProvider;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.nodes.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.util.ClasspathUtil;

/**
 * Application email tests
 * 
 * @author uzi
 */
public class EmailTest extends AbstractTestCase
{
    /**
     * @throws Exception
     */
    public void testEmailProviders()
            throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create an application
        Application application = platform.createApplication();

        // create an email provider #1
        EmailProvider emailProvider1 = application.createEmailProvider(
                JSONBuilder.start(EmailProvider.FIELD_HOST).is("xyz.com")
                        .and(EmailProvider.FIELD_USERNAME).is("test1")
                        .get()
        );

        // create another email provider #2
        EmailProvider emailProvider2 = application.createEmailProvider(
                JSONBuilder.start(EmailProvider.FIELD_HOST).is("booya.com")
                    .and(EmailProvider.FIELD_USERNAME).is("test2")
                    .get()
        );

        // create another email provider #3
        EmailProvider emailProvider3 = application.createEmailProvider(
                JSONBuilder.start(EmailProvider.FIELD_HOST).is("booya.com")
                        .and(EmailProvider.FIELD_USERNAME).is("test3")
                        .get()
        );

        // query
        assertEquals(2, application.queryEmailProviders(QueryBuilder.start(EmailProvider.FIELD_HOST).is("booya.com").get()).size());
        assertEquals(1, application.queryEmailProviders(QueryBuilder.start(EmailProvider.FIELD_USERNAME).is("test3").get()).size());
        assertEquals(3, application.listEmailProviders().size());

        // delete
        emailProvider3.delete();

        // verify
        assertEquals(0, application.queryEmailProviders(QueryBuilder.start(EmailProvider.FIELD_USERNAME).is("test3").get()).size());
        assertEquals(2, application.listEmailProviders().size());
    }

    /**
     * Tests sending an email straight away, no frills.
     * Also does some query tests
     *
     * @throws Exception
     */
    public void testSendEmail1()
            throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create an application
        Application application = platform.createApplication();

        // create an email provider
        EmailProvider emailProvider = application.createEmailProvider(
                JSONBuilder.start(EmailProvider.FIELD_HOST).is("smtp.gmail.com")
                    .and(EmailProvider.FIELD_USERNAME).is("buildtest@gitanasoftware.com")
                    .and(EmailProvider.FIELD_PASSWORD).is("buildt@st11")
                    .and(EmailProvider.FIELD_SMTP_ENABLED).is(true)
                    .and(EmailProvider.FIELD_SMTP_IS_SECURE).is(true)
                    .and(EmailProvider.FIELD_SMTP_REQUIRES_AUTH).is(true)
                    .and(EmailProvider.FIELD_SMTP_STARTTLS_ENABLED).is(true)
                    .get()
        );
        
        // create an email
        Email email = application.createEmail(
                JSONBuilder.start(Email.FIELD_TO).is("buildtest@gitanasoftware.com")
                    .and(Email.FIELD_BODY).is("Here is a test body")
                    .and(Email.FIELD_FROM).is("buildtest@gitanasoftware.com")
                    .get()
        );
        
        // send the email
        emailProvider.send(email);

        // check to ensure was marked as sent, along with some data
        email.reload();
        assertTrue(email.getSent());
        assertNotNull(email.dateSent());
        assertNotNull(email.getSentBy());

        // create a few additional emails for query

        // 1
        Email email1 = application.createEmail(
                JSONBuilder.start(Email.FIELD_TO).is("user3@test.com")
                        .and(Email.FIELD_BODY).is("Here is a test body")
                        .and(Email.FIELD_FROM).is("user1@user.com")
                        .get()
        );

        // 2
        Email email2 = application.createEmail(
                JSONBuilder.start(Email.FIELD_TO).is("user3@test.com")
                        .and(Email.FIELD_BODY).is("Here is a test body")
                        .and(Email.FIELD_FROM).is("user2@user.com")
                        .get()
        );

        // query
        assertEquals(2, application.queryEmails(QueryBuilder.start(Email.FIELD_TO).is("user3@test.com").get()).size());
        assertEquals(1, application.queryEmails(QueryBuilder.start(Email.FIELD_FROM).is("user2@user.com").get()).size());
        assertEquals(0, application.queryEmails(QueryBuilder.start(Email.FIELD_FROM).is("user4@user.com").get()).size());
        assertEquals(3, application.listEmails().size());

        // delete
        email2.delete();

        // query
        assertEquals(2, application.listEmails().size());
    }

    /**
     * Tests sending an email using a domain user.
     *
     * @throws Exception
     */
    public void testSendEmail2()
            throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a domain and a principal
        Domain domain = platform.createDomain();
        DomainUser user = domain.createUser("test-" + System.currentTimeMillis(), "pw");
        user.setEmail("buildtest@gitanasoftware.com");
        user.update();

        // create an application
        Application application = platform.createApplication();

        // create an email provider
        EmailProvider emailProvider = application.createEmailProvider(
                JSONBuilder.start(EmailProvider.FIELD_HOST).is("smtp.gmail.com")
                        .and(EmailProvider.FIELD_USERNAME).is("buildtest@gitanasoftware.com")
                        .and(EmailProvider.FIELD_PASSWORD).is("buildt@st11")
                        .and(EmailProvider.FIELD_SMTP_ENABLED).is(true)
                        .and(EmailProvider.FIELD_SMTP_IS_SECURE).is(true)
                        .and(EmailProvider.FIELD_SMTP_REQUIRES_AUTH).is(true)
                        .and(EmailProvider.FIELD_SMTP_STARTTLS_ENABLED).is(true)
                        .get()
        );

        // create an email
        Email email = application.createEmail(
                JSONBuilder.start(Email.FIELD_BODY).is("Here is a test body")
                        .and(Email.FIELD_FROM).is("buildtest@gitanasoftware.com")
                        .get()
        );
        email.setToDomainUser(user);
        email.update();

        // send the email
        emailProvider.send(email);

        // check to ensure was marked as sent, along with some data
        email.reload();
        assertTrue(email.getSent());
        assertNotNull(email.dateSent());
        assertNotNull(email.getSentBy());    
    }


    /**
     * Tests using a domain user and a repository node.
     *
     * @throws Exception
     */
    public void testSendEmail3()
            throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a domain and a principal
        Domain domain = platform.createDomain();
        DomainUser user = domain.createUser("test-" + System.currentTimeMillis(), "pw");
        user.setEmail("buildtest@gitanasoftware.com");
        user.update();

        // create a repository
        Repository repository = platform.createRepository();
        Branch branch = repository.readBranch("master");

        // create a node with a text attachment that serves as the email body
        Node node = (Node) branch.createNode();
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/email1.html");
        node.uploadAttachment(bytes, MimeTypeMap.TEXT_HTML);

        // create an application
        Application application = platform.createApplication();

        // create an email provider
        EmailProvider emailProvider = application.createEmailProvider(
                JSONBuilder.start(EmailProvider.FIELD_HOST).is("smtp.gmail.com")
                        .and(EmailProvider.FIELD_USERNAME).is("buildtest@gitanasoftware.com")
                        .and(EmailProvider.FIELD_PASSWORD).is("buildt@st11")
                        .and(EmailProvider.FIELD_SMTP_ENABLED).is(true)
                        .and(EmailProvider.FIELD_SMTP_IS_SECURE).is(true)
                        .and(EmailProvider.FIELD_SMTP_REQUIRES_AUTH).is(true)
                        .and(EmailProvider.FIELD_SMTP_STARTTLS_ENABLED).is(true)
                        .get()
        );

        // create an email
        Email email = application.createEmail(
                JSONBuilder.start(Email.FIELD_BODY).is("Here is a test body")
                        .and(Email.FIELD_FROM).is("buildtest@gitanasoftware.com")
                        .get()
        );
        email.setToDomainUser(user);
        email.update();

        // send the email
        emailProvider.send(email);

        // check to ensure was marked as sent, along with some data
        email.reload();
        assertTrue(email.getSent());
        assertNotNull(email.dateSent());
        assertNotNull(email.getSentBy());
    }    
}
