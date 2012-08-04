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

package org.gitana.platform.client.application;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Application extends PlatformDataStore
{
    public final static String FIELD_KEY = "key";

    public String getKey();
    public void setKey(String key);

    /**
     * @return platform
     */
    public Platform getPlatform();



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // GENERAL SETTINGS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Settings> listSettings();
    public ResultMap<Settings> listSettings(Pagination pagination);

    public Settings readSettings(String settingsId);

    public Settings createSettings(ObjectNode object);

    public ResultMap<Settings> querySettings(ObjectNode query);
    public ResultMap<Settings> querySettings(ObjectNode query, Pagination pagination);

    public void updateSettings(Settings settings);

    public void deleteSettings(Settings settings);
    public void deleteSettings(String settingsId);


    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // APPLICATION SETTINGS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public Settings readApplicationSettings();


    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // APPLICATION USER SETTINGS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public Settings readApplicationSettings(String scope, String key);
    public Settings readApplicationPrincipalSettings(DomainPrincipal principal);
    public Settings readApplicationPrincipalSettings(String domainId, String principalId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EMAILS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Email> listEmails();
    public ResultMap<Email> listEmails(Pagination pagination);

    public Email readEmail(String emailId);

    public Email createEmail();
    public Email createEmail(ObjectNode object);
    public Email createEmail(DomainUser to, String subject, String body, String fromEmailAddress);
    public Email createEmail(DomainUser to, String subject, String body, String fromEmailAddress, String ccEmailAddresses, String bccEmailAddresses);
    public Email createEmail(DomainUser to, String subject, Node node, String attachmentId, String fromEmailAddress);
    public Email createEmail(DomainUser to, String subject, Node node, String attachmentId, String fromEmailAddress, String ccEmailAddresses, String bccEmailAddresses);

    public ResultMap<Email> queryEmails(ObjectNode query);
    public ResultMap<Email> queryEmails(ObjectNode query, Pagination pagination);

    public void updateEmail(Email email);

    public void deleteEmail(Email email);
    public void deleteEmail(String emailId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EMAIL PROVIDERS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<EmailProvider> listEmailProviders();
    public ResultMap<EmailProvider> listEmailProviders(Pagination pagination);

    public EmailProvider readEmailProvider(String emailProviderId);

    public EmailProvider createEmailProvider();
    public EmailProvider createEmailProvider(ObjectNode object);

    public ResultMap<EmailProvider> queryEmailProviders(ObjectNode query);
    public ResultMap<EmailProvider> queryEmailProviders(ObjectNode query, Pagination pagination);

    public void updateEmailProvider(EmailProvider emailProvider);

    public void deleteEmailProvider(EmailProvider emailProvider);
    public void deleteEmailProvider(String emailProviderId);



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // REGISTRATIONS
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Registration> listRegistrations();
    public ResultMap<Registration> listRegistrations(Pagination pagination);

    public Registration readRegistration(String registrationId);

    public Registration createRegistration(ObjectNode object);
    public Registration createRegistration(String email);

    public ResultMap<Registration> queryRegistrations(ObjectNode query);
    public ResultMap<Registration> queryRegistrations(ObjectNode query, Pagination pagination);

    public void updateRegistration(Registration registration);

    public void deleteRegistration(Registration registration);
    public void deleteRegistration(String registrationId);


}