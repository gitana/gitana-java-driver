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
package org.gitana.platform.client.application;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.client.webhost.DeployedApplication;
import org.gitana.platform.client.webhost.WebHost;
import org.gitana.platform.services.reference.Reference;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.*;

/**
 * @author uzi
 */
public class ApplicationImpl extends AbstractPlatformDataStoreImpl implements Application
{
    public ApplicationImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_APPLICATION;
    }

    @Override
    public String getResourceUri()
    {
        return "/applications/" + getId();
    }

    @Override
    public void reload()
    {
        Application application = getPlatform().readApplication(getId());
        this.reload(application.getObject());
    }

    @Override
    public String getKey()
    {
        return getString(FIELD_KEY);
    }

    @Override
    public void setKey(String key)
    {
        set(FIELD_KEY, key);
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SETTINGS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Settings readApplicationSettings() 
    {
        return readApplicationSettings("application", "application");
    }

    @Override
    public Settings readApplicationSettings(String scope, String key)
    {
        Settings settings = null;
        
        ObjectNode object = QueryBuilder.start("scope").is(scope).and("key").is(key).get();

        ResultMap<Settings> map = querySettings(object);
        if (map.size() > 0)
        {
            settings = map.values().iterator().next();
        }

        // if nothing, then create it
        if (settings == null)
        {
            settings = createSettings(object);
        }

        return settings;
    }

    @Override
    public Settings readApplicationPrincipalSettings(DomainPrincipal principal)
    {
        return readApplicationPrincipalSettings(principal.getDomainId(), principal.getId());
    }

    @Override
    public Settings readApplicationPrincipalSettings(String domainId, String principalId)
    {
        return readApplicationSettings("principal", domainId + "/" + principalId);
    }

    @Override
    public ResultMap<Settings> listSettings() 
    {
        return listSettings(null);
    }

    @Override
    public ResultMap<Settings> listSettings(Pagination pagination) 
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/settings", params);
        return getFactory().settingsMap(this, response);
    }

    @Override
    public Settings readSettings(String settingsId) 
    {
        Settings settings = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/settings/" + settingsId);
            settings = getFactory().settings(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return settings;
    }

    @Override
    public Settings createSettings(ObjectNode object) 
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        if (object.get(Settings.ROOT_KEY) == null)
        {
            object.putObject(Settings.ROOT_KEY);
        }

        Response response = getRemote().post(getResourceUri() + "/settings", object);

        String settingsId = response.getId();
        return readSettings(settingsId);
    }

    @Override
    public ResultMap<Settings> querySettings(ObjectNode query) 
    {
        return querySettings(query, null);
    }

    @Override
    public ResultMap<Settings> querySettings(ObjectNode query, Pagination pagination) 
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/settings/query", params, query);
        return getFactory().settingsMap(this, response);
    }

    @Override
    public void updateSettings(Settings settings) 
    {
        getRemote().put(getResourceUri() + "/settings/" + settings.getId(), settings.getObject());
    }

    @Override
    public void deleteSettings(Settings settings) 
    {
        deleteSettings(settings.getId());
    }

    @Override
    public void deleteSettings(String settingsId)
    {
        getRemote().delete(getResourceUri() + "/settings/" + settingsId);
    }




    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // EMAIL CONFIRMATIONS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Email> listEmails()
    {
        return listEmails(null);
    }

    @Override
    public ResultMap<Email> listEmails(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/emails", params);
        return getFactory().emails(this, response);
    }

    @Override
    public Email readEmail(String emailId)
    {
        Email email = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/emails/" + emailId);
            email = getFactory().email(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return email;
    }

    @Override
    public Email createEmail()
    {
        return createEmail(null);
    }

    @Override
    public Email createEmail(ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/emails", object);

        String emailId = response.getId();
        return readEmail(emailId);
    }

    @Override
    public Email createEmail(DomainUser to, String subject, String body, String fromEmailAddress)
    {
        return createEmail(to, subject, body, fromEmailAddress, null, null);
    }

    @Override
    public Email createEmail(DomainUser to, String subject, String body, String fromEmailAddress, String ccEmailAddresses, String bccEmailAddresses)
    {
        ObjectNode object = JsonUtil.createObject();
        
        object.put(Email.FIELD_TO_DOMAIN_ID, to.getDomainId());
        object.put(Email.FIELD_TO_PRINCIPAL_ID, to.getId());
        object.put(Email.FIELD_SUBJECT, subject);
        object.put(Email.FIELD_FROM, fromEmailAddress);

        object.put(Email.FIELD_BODY, body);

        if (ccEmailAddresses != null)
        {
            object.put(Email.FIELD_CC, ccEmailAddresses);
        }

        if (bccEmailAddresses != null)
        {
            object.put(Email.FIELD_BCC, bccEmailAddresses);
        }

        return createEmail(object);
    }

    @Override
    public Email createEmail(DomainUser to, String subject, Node node, String attachmentId, String fromEmailAddress)
    {
        return createEmail(to, subject, node, attachmentId, fromEmailAddress);
    }    

    @Override
    public Email createEmail(DomainUser to, String subject, Node node, String attachmentId, String fromEmailAddress, String ccEmailAddresses, String bccEmailAddresses)
    {
        ObjectNode object = JsonUtil.createObject();

        object.put(Email.FIELD_TO_DOMAIN_ID, to.getDomainId());
        object.put(Email.FIELD_TO_PRINCIPAL_ID, to.getId());
        object.put(Email.FIELD_SUBJECT, subject);
        object.put(Email.FIELD_FROM, fromEmailAddress);

        object.put(Email.FIELD_BODY_REPOSITORY_ID, node.getRepositoryId());
        object.put(Email.FIELD_BODY_BRANCH_ID, node.getBranchId());
        object.put(Email.FIELD_BODY_NODE_ID, node.getId());

        if (attachmentId == null)
        {
            attachmentId = "default";
        }
        object.put(Email.FIELD_BODY_ATTACHMENT_ID, attachmentId);

        if (ccEmailAddresses != null)
        {
            object.put(Email.FIELD_CC, ccEmailAddresses);
        }

        if (bccEmailAddresses != null)
        {
            object.put(Email.FIELD_BCC, bccEmailAddresses);
        }

        return createEmail(object);
    }

    @Override
    public ResultMap<Email> queryEmails(ObjectNode query)
    {
        return queryEmails(query, null);
    }

    @Override
    public ResultMap<Email> queryEmails(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/emails/query", params, query);
        return getFactory().emails(this, response);
    }

    @Override
    public void updateEmail(Email email)
    {
        getRemote().put(getResourceUri() + "/emails/" + email.getId(), email.getObject());
    }

    @Override
    public void deleteEmail(Email email)
    {
        deleteEmail(email.getId());
    }

    @Override
    public void deleteEmail(String emailId)
    {
        getRemote().delete(getResourceUri() + "/emails/" + emailId);
    }

    @Override
    public ResultMap<EmailProvider> listEmailProviders() 
    {
        return listEmailProviders(null);
    }

    @Override
    public ResultMap<EmailProvider> listEmailProviders(Pagination pagination) 
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/emailproviders", params);
        return getFactory().emailProviders(this, response);
    }

    @Override
    public EmailProvider readEmailProvider(String emailProviderId) 
    {
        EmailProvider emailProvider = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/emailproviders/" + emailProviderId);
            emailProvider = getFactory().emailProvider(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return emailProvider;
    }

    @Override
    public EmailProvider createEmailProvider()
    {
        return createEmailProvider(null);
    }

    @Override
    public EmailProvider createEmailProvider(ObjectNode object) 
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/emailproviders", object);

        String emailProviderId = response.getId();
        return readEmailProvider(emailProviderId);
    }

    @Override
    public ResultMap<EmailProvider> queryEmailProviders(ObjectNode query) 
    {
        return queryEmailProviders(query, null);
    }

    @Override
    public ResultMap<EmailProvider> queryEmailProviders(ObjectNode query, Pagination pagination) 
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/emailproviders/query", params, query);
        return getFactory().emailProviders(this, response);
    }

    @Override
    public void updateEmailProvider(EmailProvider emailProvider) 
    {
        getRemote().put(getResourceUri() + "/emailproviders/" + emailProvider.getId(), emailProvider.getObject());
    }

    @Override
    public void deleteEmailProvider(EmailProvider emailProvider) 
    {
        deleteEmailProvider(emailProvider.getId());
    }

    @Override
    public void deleteEmailProvider(String emailProviderId) 
    {
        getRemote().delete(getResourceUri() + "/emailproviders/" + emailProviderId);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // REGISTRATIONS
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Registration> listRegistrations()
    {
        return listRegistrations(null);
    }

    @Override
    public ResultMap<Registration> listRegistrations(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/registrations", params);
        return getFactory().registrations(this, response);
    }

    @Override
    public Registration readRegistration(String registrationId)
    {
        Registration registration = null;

        try
        {
            // we use the "/lookup" URI since the registrationId might have non URI-compatible characters in it
            // like . or @ (for email addresses)
            Response response = getRemote().get(getResourceUri() + "/registrations/lookup?registrationId=" + registrationId);
            registration = getFactory().registration(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return registration;
    }

    @Override
    public Registration createRegistration(ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/registrations", object);

        String registrationId = response.getId();
        return readRegistration(registrationId);
    }

    @Override
    public Registration createRegistration(String email) 
    {
        ObjectNode object = JsonUtil.createObject();
        object.put(Registration.FIELD_USER_EMAIL, email);

        return createRegistration(object);
    }

    @Override
    public ResultMap<Registration> queryRegistrations(ObjectNode query)
    {
        return queryRegistrations(query, null);
    }

    @Override
    public ResultMap<Registration> queryRegistrations(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/registrations/query", params, query);
        return getFactory().registrations(this, response);
    }

    @Override
    public void updateRegistration(Registration registration)
    {
        getRemote().put(getResourceUri() + "/registrations/" + registration.getId(), registration.getObject());
    }

    @Override
    public void deleteRegistration(Registration registration)
    {
        deleteRegistration(registration.getId());
    }

    @Override
    public void deleteRegistration(String registrationId)
    {
        getRemote().delete(getResourceUri() + "/registrations/" + registrationId);
    }




    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DEPLOYMENT
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void addDeployment(String key, String webhost, String subdomain, String domain, ObjectNode config)
    {
        if (!has(FIELD_DEPLOYMENTS))
        {
            set(FIELD_DEPLOYMENTS, JsonUtil.createObject());
        }

        if (config == null)
        {
            config = JsonUtil.createObject();
        }

        config.put(FIELD_DEPLOYMENT_WEBHOST, webhost);
        config.put(FIELD_DEPLOYMENT_SUBDOMAIN, subdomain);
        config.put(FIELD_DEPLOYMENT_DOMAIN, domain);

        ObjectNode deployments = getObject(FIELD_DEPLOYMENTS);
        deployments.put(key, config);
    }

    @Override
    public List<String> getDeploymentKeys()
    {
        if (!has(FIELD_DEPLOYMENTS))
        {
            set(FIELD_DEPLOYMENTS, JsonUtil.createObject());
        }

        ObjectNode deployments = getObject(FIELD_DEPLOYMENTS);

        List<String> keys = new ArrayList<String>();

        Iterator<String> it = deployments.fieldNames();
        while (it.hasNext())
        {
            keys.add(it.next());
        }

        return keys;
    }

    @Override
    public ObjectNode getDeployment(String key)
    {
        if (!has(FIELD_DEPLOYMENTS))
        {
            set(FIELD_DEPLOYMENTS, JsonUtil.createObject());
        }

        return (ObjectNode) getObject(FIELD_DEPLOYMENTS).get(key);
    }

    @Override
    public void removeDeployment(String key)
    {
        if (!has(FIELD_DEPLOYMENTS))
        {
            set(FIELD_DEPLOYMENTS, JsonUtil.createObject());
        }

        getObject(FIELD_DEPLOYMENTS).remove(key);
    }

    @Override
    public void setSource(String type, boolean isPublic, String uri)
    {
        ObjectNode source = JsonUtil.createObject();
        source.put("type", type);
        source.put("public", isPublic);
        source.put("uri", uri);

        set(FIELD_SOURCE, source);
    }

    @Override
    public String getSourceType()
    {
        return getString(FIELD_SOURCE_TYPE);
    }

    @Override
    public boolean getSourcePublic()
    {
        return getBoolean(FIELD_SOURCE_PUBLIC);
    }

    @Override
    public String getSourceUri()
    {
        return getString(FIELD_SOURCE_URI);
    }

    @Override
    public DeployedApplication deploy(String deploymentKey)
    {
        ObjectNode deploymentConfig = getDeployment(deploymentKey);
        String webhostId = JsonUtil.objectGetString(deploymentConfig, FIELD_DEPLOYMENT_WEBHOST);

        // read web host
        WebHost webhost = getPlatform().readWebHost(webhostId);

        Response response = getRemote().post(getResourceUri() + "/deploy/" + deploymentKey);
        return getFactory().deployedApplication(webhost, response);
    }

    @Override
    public DeployedApplication findDeployed(String deploymentKey)
    {
        ObjectNode deploymentConfig = getDeployment(deploymentKey);
        String webhostId = JsonUtil.objectGetString(deploymentConfig, FIELD_DEPLOYMENT_WEBHOST);

        // read web host
        WebHost webhost = getPlatform().readWebHost(webhostId);

        Response response = getRemote().post(getResourceUri() + "/deployed/" + deploymentKey);
        return getFactory().deployedApplication(webhost, response);
    }

    @Override
    public Map<String, Map<String, String>> readApiKeys()
    {
        Map<String, Map<String, String>> results = new HashMap<String, Map<String, String>>();

        Response response = getRemote().get("/applications/" + getId() + "/apikeys");
        ObjectNode deployments = JsonUtil.objectGetObject(response.getObjectNode(), "deployments");

        Iterator<String> deploymentIds = deployments.fieldNames();
        while (deploymentIds.hasNext())
        {
            String deploymentId = deploymentIds.next();

            ObjectNode deployment = JsonUtil.objectGetObject(deployments, deploymentId);

            Map<String, String> properties = new HashMap<String, String>();
            properties.put("clientKey", JsonUtil.objectGetString(deployment, "clientKey"));
            properties.put("clientSecret", JsonUtil.objectGetString(deployment, "clientSecret"));
            properties.put("username", JsonUtil.objectGetString(deployment, "username"));
            properties.put("password", JsonUtil.objectGetString(deployment, "password"));
            properties.put("application", JsonUtil.objectGetString(deployment, "application"));
            properties.put("baseURL", DriverContext.getDriver().getBaseURL());

            results.put(deploymentId, properties);
        }

        return results;
    }

    @Override
    public Map<String, String> readApiKeys(String deploymentKey)
    {
        Map<String, Map<String, String>> results = readApiKeys();

        return results.get(deploymentKey);
    }
}
