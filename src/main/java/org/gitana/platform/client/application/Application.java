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
 *   info@gitana.io
 */
package org.gitana.platform.client.application;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.webhost.DeployedApplication;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public interface Application extends PlatformDataStore
{
    public final static String FIELD_KEY = "key";

    public String getKey();
    public void setKey(String key);

    public final static String FIELD_DEPLOYMENTS = "deployments";
    public final static String FIELD_DEPLOYMENT_WEBHOST = "webhost";
    public final static String FIELD_DEPLOYMENT_SUBDOMAIN = "subdomain";
    public final static String FIELD_DEPLOYMENT_DOMAIN = "domain";
    public final static String FIELD_DEPLOYMENT_CLIENT_ID = "clientId";
    public final static String FIELD_DEPLOYMENT_AUTH_GRANT_ID = "authGrantId";

    public final static String FIELD_SOURCE = "source";
    public final static String FIELD_SOURCE_TYPE = "type";
    public final static String FIELD_SOURCE_PUBLIC = "public";
    public final static String FIELD_SOURCE_URI = "uri";

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



    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DEPLOYMENT
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public void addDeployment(String key, String webhost, String subdomain, String domain, ObjectNode config);
    public List<String> getDeploymentKeys();
    public ObjectNode getDeployment(String key);
    public void removeDeployment(String key);

    public void setSource(String type, boolean isPublic, String uri);
    public String getSourceType();
    public boolean getSourcePublic();
    public String getSourceUri();

    public DeployedApplication deploy(String deploymentKey);

    public DeployedApplication findDeployed(String deploymentKey);

    public Map<String, Map<String, String>> readApiKeys();
    public Map<String, String> readApiKeys(String deploymentKey);


}