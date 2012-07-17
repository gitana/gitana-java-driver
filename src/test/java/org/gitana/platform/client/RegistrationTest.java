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

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.application.Email;
import org.gitana.platform.client.application.EmailProvider;
import org.gitana.platform.client.application.Registration;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * Registration Test
 * 
 * @author uzi
 */
public class RegistrationTest extends AbstractTestCase
{
    @Test
    public void testRegistration()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a domain where our registered user will be created
        Domain domain = platform.createDomain();
        
        // create a registrar where our registered tenant will be created
        // this will have two plans: plan1, plan2
        Registrar registrar = platform.createRegistrar();
        registrar.createPlan("plan1");
        registrar.createPlan("plan2");
        
        // create an application
        Application application = platform.createApplication();

        // create some registrations
        Registration registration1 = application.createRegistration(
                JSONBuilder.start(Registration.FIELD_USER_EMAIL).is("uzi@test.com")
                        .and(Registration.FIELD_TENANT_PLAN_KEY).is("plan1")
                        .and(Registration.FIELD_USER_DOMAIN_ID).is(domain.getId())
                        .and(Registration.FIELD_TENANT_REGISTRAR_ID).is(registrar.getId())
                        .get()
        );
        Registration registration2 = application.createRegistration(
                JSONBuilder.start(Registration.FIELD_USER_EMAIL).is("drq@test.com")
                        .and(Registration.FIELD_TENANT_PLAN_KEY).is("plan2")
                        .and(Registration.FIELD_USER_DOMAIN_ID).is(domain.getId())
                        .and(Registration.FIELD_TENANT_REGISTRAR_ID).is(registrar.getId())
                        .get()
        );
        Registration registration3 = application.createRegistration(
                JSONBuilder.start(Registration.FIELD_USER_EMAIL).is("spikelee@test.com")
                        .and(Registration.FIELD_TENANT_PLAN_KEY).is("plan2")
                        .and(Registration.FIELD_USER_DOMAIN_ID).is(domain.getId())
                        .and(Registration.FIELD_TENANT_REGISTRAR_ID).is(registrar.getId())
                        .get()
        );
        
        // confirm via query
        assertEquals(2, application.queryRegistrations(QueryBuilder.start(Registration.FIELD_TENANT_PLAN_KEY).is("plan2").get()).size());
        assertEquals(1, application.queryRegistrations(QueryBuilder.start(Registration.FIELD_USER_EMAIL).is("uzi@test.com").get()).size());

        // now set up registration 1 to be emailed

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

        // update registration 1 to identify the email provider
        registration1.setEmailProviderId(emailProvider.getId());
        registration1.update();

        // confirmation email settings
        ObjectNode confirmationEmailObject = JsonUtil.createObject();
        confirmationEmailObject.put(Email.FIELD_BODY, "Please confirm!");
        confirmationEmailObject.put(Email.FIELD_FROM, "buildtest@gitanasoftware.com");
        registration1.setEmailConfiguration("confirmation", confirmationEmailObject);
        registration1.update();

        // welcome email settings
        ObjectNode welcomeEmailObject = JsonUtil.createObject();
        welcomeEmailObject.put(Email.FIELD_BODY, "Welcome!");
        welcomeEmailObject.put(Email.FIELD_FROM, "buildtest@gitanasoftware.com");
        registration1.setEmailConfiguration("welcome", welcomeEmailObject);
        registration1.update();

        // send the first one's confirmation email
        registration1.sendConfirmationEmail();
        
        // verify the confirmation email was sent
        registration1.reload();
        assertTrue(registration1.getConfirmationSent());

        // and now, at this point, they'd presumably supply some more information
        // so we supply it here
        registration1.setUserName("bud");
        ObjectNode userProperties = JsonUtil.createObject();
        userProperties.put(DomainUser.FIELD_FIRST_NAME, "Houston");
        userProperties.put(DomainUser.FIELD_LAST_NAME, "Wilson");
        userProperties.put("school", "Elm Dale");
        registration1.setUserProperties(userProperties);
        registration1.update();
        
        // additional tenant information
        registration1.setTenantTitle("Dixie");
        registration1.setTenantDescription("Flatline");

        // add in some signup properties
        registration1.getSignupProperties().put("company", "Illymani Designs");
        registration1.update();

        // confirm (and supply the password for the new user)
        registration1.confirm("password");
        
        // verify the registration completed
        registration1.reload();
        assertTrue(registration1.getCompleted());
        assertNotNull(registration1.getCompletedPrincipalId()); // NOTE: this is the user object in parent tenant
        assertNotNull(registration1.getCompletedTenantId());
        assertEquals("Illymani Designs", JsonUtil.objectGetString(registration1.getSignupProperties(), "company"));

        // assert we can read back the tenant
        Tenant newTenant = registrar.readTenant(registration1.getCompletedTenantId());
        assertNotNull(newTenant);
        assertEquals("Dixie", newTenant.getTitle());
        assertEquals("Flatline", newTenant.getDescription());

        // read the newly created user
        DomainUser user = (DomainUser) domain.readPrincipal(registration1.getCompletedPrincipalId());
        ObjectNode newUserObject = user.readIdentity().findUserObjectForTenant(registration1.getCompletedTenantId());
        String newPrincipalId = JsonUtil.objectGetString(newUserObject, DomainUser.FIELD_ID);

        // now we will authenticate against the new tenant using our new user
        // we can do this because we happen to know their password

        // find the default client for the new tenant
        ObjectNode newClientObject = newTenant.readDefaultAllocatedClientObject();
        String newClientKey = JsonUtil.objectGetString(newClientObject, Client.FIELD_KEY);
        String newClientSecret = JsonUtil.objectGetString(newClientObject, Client.FIELD_SECRET);

        // connect to the platform as the new user
        Platform newPlatform = new Gitana(newClientKey, newClientSecret).authenticate("bud", "password");

        // load the user object
        DomainUser newUser = (DomainUser) newPlatform.readPrimaryDomain().readPrincipal(newPrincipalId);
        assertNotNull(newUser);

        // assert that we have all the properties of the user
        assertEquals("Houston", newUser.getFirstName());
        assertEquals("Wilson", newUser.getLastName());
        assertEquals("Elm Dale", newUser.getString("school"));
    }

}
