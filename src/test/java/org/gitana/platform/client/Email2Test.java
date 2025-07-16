/**
 * Copyright 2025 Gitana Software, Inc.
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

import org.gitana.JSONBuilder;
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.application.Email;
import org.gitana.platform.client.application.EmailProvider;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.ClasspathUtil;

/**
 * Application email tests 2
 * 
 * @author uzi
 */
public class Email2Test extends AbstractTestCase
{
    public void testSendEmail2()
            throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a domain and a principal
        Domain domain = platform.createDomain();
        DomainUser user = domain.createUser("test-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);
        user.setEmail("buildtest@gitanasoftware.com");
        user.update();

        // create an application
        Application application = platform.createApplication();

        // create an email provider
        EmailProvider emailProvider = application.createEmailProvider(
                JSONBuilder.start(EmailProvider.FIELD_HOST).is("smtp.gmail.com")
                        .and(EmailProvider.FIELD_USERNAME).is("buildtest@cloudcms.com")
                        .and(EmailProvider.FIELD_PASSWORD).is("buildt@st11")
                        .and(EmailProvider.FIELD_SMTP_ENABLED).is(true)
                        .and(EmailProvider.FIELD_SMTP_IS_SECURE).is(true)
                        .and(EmailProvider.FIELD_SMTP_REQUIRES_AUTH).is(true)
                        .and(EmailProvider.FIELD_SMTP_STARTTLS_ENABLED).is(true)
                        .get()
        );

        // create an email
        String subject = "test1";
        String body = "body1";
        String fromEmailAddress = "buildtest@gitanasoftware.com";
        Email email = application.createEmail(user, subject, body, fromEmailAddress);

        // send the email
        emailProvider.send(email);

        // check to ensure was marked as sent, along with some data
        email.reload();
        assertTrue(email.getSent());
        assertNotNull(email.dateSent());
        assertNotNull(email.getSentBy());    
    }
}
