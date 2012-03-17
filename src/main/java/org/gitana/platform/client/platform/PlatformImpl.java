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

package org.gitana.platform.client.platform;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.billing.BillingProviderConfiguration;
import org.gitana.platform.client.cluster.AbstractClusterDataStoreImpl;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.directory.Directory;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.log.LogEntry;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.client.webhost.WebHost;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class PlatformImpl extends AbstractClusterDataStoreImpl implements Platform
{
    public PlatformImpl(Cluster cluster, ObjectNode obj, boolean isSaved)
    {
        super(cluster, obj, isSaved);
    }

    @Override
    public String getType()
    {
        return "platform";
    }

    @Override
    public String getResourceUri()
    {
        return "";
    }

    @Override
    public void reload()
    {
        Response response = getRemote().get(getResourceUri());

        Platform platform = getFactory().platform(getCluster(), response);
        this.reload(platform);
    }

    @Override
    public Domain readPrimaryDomain()
    {
        return readDomain("primary");
    }

    @Override
    public Directory readPrimaryDirectory()
    {
        return readDirectory("primary");
    }

    @Override
    public String getOwnerRegistrarId()
    {
        return getString("ownerRegistrarId");
    }

    @Override
    public String getOwnerTenantId()
    {
        return getString("ownerTenantId");
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // REPOSITORIES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Repository> listRepositories()
    {
        return listRepositories(null);
    }

    @Override
    public ResultMap<Repository> listRepositories(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/repositories", params);
        return getFactory().repositories(this, response);
    }

    @Override
    public Repository readRepository(String repositoryId)
    {
        Repository repository = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/repositories/" + repositoryId);
            repository = getFactory().repository(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return repository;
    }

    @Override
    public Repository createRepository()
    {
        return createRepository(null);
    }

    @Override
    public Repository createRepository(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/repositories", object);

        String repositoryId = response.getId();
        return readRepository(repositoryId);
    }

    @Override
    public ResultMap<Repository> queryRepositories(ObjectNode query)
    {
        return queryRepositories(query, null);
    }

    @Override
    public ResultMap<Repository> queryRepositories(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/repositories/query", params, query);
        return getFactory().repositories(this, response);
    }

    @Override
    public PermissionCheckResults checkRepositoryPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/repositories/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // LOG ENTRIES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<LogEntry> listLogEntries()
    {
        return listLogEntries(null);
    }

    @Override
    public ResultMap<LogEntry> listLogEntries(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/logs", params);
        return getFactory().logEntries(getCluster(), response);
    }

    @Override
    public ResultMap<LogEntry> queryLogEntries(ObjectNode query)
    {
        return queryLogEntries(query, null);
    }

    @Override
    public ResultMap<LogEntry> queryLogEntries(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/logs/query", params, query);
        return getFactory().logEntries(getCluster(), response);
    }

    @Override
    public LogEntry readLogEntry(String logEntryId)
    {
        LogEntry logEntry = null;

        try
        {
            Response response = getRemote().get("/logs/" + logEntryId);
            logEntry = getFactory().logEntry(getCluster(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return logEntry;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // STACKS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public ResultMap<Stack> listStacks()
    {
        return listStacks(null);
    }

    @Override
    public ResultMap<Stack> listStacks(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/stacks", params);
        return getFactory().stacks(this, response);
    }

    @Override
    public ResultMap<Stack> queryStacks(ObjectNode query)
    {
        return queryStacks(query, null);
    }

    @Override
    public ResultMap<Stack> queryStacks(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/stacks/query", params, query);
        return getFactory().stacks(this, response);
    }

    @Override
    public Stack readStack(String stackId)
    {
        Stack stack = null;

        try
        {
            Response response = getRemote().get("/stacks/" + stackId);
            stack = getFactory().stack(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return stack;
    }

    @Override
    public Stack createStack()
    {
        return createStack(null);
    }

    @Override
    public Stack createStack(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/stacks", object);

        String stackId = response.getId();
        return readStack(stackId);
    }

    @Override
    public void updateStack(Stack stack)
    {
        getRemote().put("/stacks/" + stack.getId(), stack.getObject());
    }

    @Override
    public void deleteStack(Stack stack)
    {
        deleteStack(stack.getId());
    }

    @Override
    public void deleteStack(String stackId)
    {
        getRemote().delete("/stacks/" + stackId);
    }

    @Override
    public PermissionCheckResults checkStackPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post("/stacks/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DOMAINS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Domain> listDomains()
    {
        return listDomains(null);
    }

    @Override
    public ResultMap<Domain> listDomains(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/domains", params);
        return getFactory().domains(this, response);
    }

    @Override
    public Domain readDomain(String domainId)
    {
        Domain domain = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/domains/" + domainId);
            domain = getFactory().domain(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return domain;
    }

    @Override
    public Domain createDomain()
    {
        return createDomain(null);
    }

    @Override
    public Domain createDomain(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/domains", object);

        String domainId = response.getId();
        return readDomain(domainId);
    }

    @Override
    public ResultMap<Domain> queryDomains(ObjectNode query)
    {
        return queryDomains(query, null);
    }

    @Override
    public ResultMap<Domain> queryDomains(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/domains/query", params, query);
        return getFactory().domains(this, response);
    }

    @Override
    public PermissionCheckResults checkDomainPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/domains/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // VAULTS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Vault> listVaults()
    {
        return listVaults(null);
    }

    @Override
    public ResultMap<Vault> listVaults(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/vaults", params);
        return getFactory().vaults(this, response);
    }

    @Override
    public Vault readVault(String domainId)
    {
        Vault vault = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/vaults/" + domainId);
            vault = getFactory().vault(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return vault;
    }

    @Override
    public Vault createVault()
    {
        return createVault(null);
    }

    @Override
    public Vault createVault(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/vaults", object);

        String vaultId = response.getId();
        return readVault(vaultId);
    }

    @Override
    public ResultMap<Vault> queryVaults(ObjectNode query)
    {
        return queryVaults(query, null);
    }

    @Override
    public ResultMap<Vault> queryVaults(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/vaults/query", params, query);
        return getFactory().vaults(this, response);
    }

    @Override
    public PermissionCheckResults checkVaultPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/vaults/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DIRECTORIES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Directory> listDirectories()
    {
        return listDirectories(null);
    }

    @Override
    public ResultMap<Directory> listDirectories(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/directories", params);
        return getFactory().directories(this, response);
    }

    @Override
    public Directory readDirectory(String directoryId)
    {
        Directory directory = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/directories/" + directoryId);
            directory = getFactory().directory(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return directory;
    }

    @Override
    public Directory createDirectory()
    {
        return createDirectory(null);
    }

    @Override
    public Directory createDirectory(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/directories", object);

        String directoryId = response.getId();
        return readDirectory(directoryId);
    }

    @Override
    public ResultMap<Directory> queryDirectories(ObjectNode query)
    {
        return queryDirectories(query, null);
    }

    @Override
    public ResultMap<Directory> queryDirectories(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/directories/query", params, query);
        return getFactory().directories(this, response);
    }

    @Override
    public PermissionCheckResults checkDirectoryPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/directories/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }
    



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // APPLICATIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Application> listApplications()
    {
        return listApplications(null);
    }

    @Override
    public ResultMap<Application> listApplications(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/applications", params);
        return getFactory().applications(this, response);
    }

    @Override
    public Application readApplication(String applicationId)
    {
        Application application = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/applications/" + applicationId);
            application = getFactory().application(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return application;
    }

    @Override
    public Application createApplication()
    {
        return createApplication(null);
    }

    @Override
    public Application createApplication(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/applications", object);

        String applicationId = response.getId();
        return readApplication(applicationId);
    }

    @Override
    public ResultMap<Application> queryApplications(ObjectNode query)
    {
        return queryApplications(query, null);
    }

    @Override
    public ResultMap<Application> queryApplications(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/applications/query", params, query);
        return getFactory().applications(this, response);
    }

    @Override
    public PermissionCheckResults checkApplicationPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/vaults/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CLIENTS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Client> listClients()
    {
        return listClients(null);
    }

    @Override
    public ResultMap<Client> listClients(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/clients", params);
        return getFactory().clients(this, response);
    }

    @Override
    public ResultMap<Client> queryClients(ObjectNode query)
    {
        return queryClients(query, null);
    }

    @Override
    public ResultMap<Client> queryClients(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/clients/query", params, query);
        return getFactory().clients(this, response);
    }

    @Override
    public Client readClient(String clientId)
    {
        Client client = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/clients/" + clientId);
            client = getFactory().client(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return client;
    }

    @Override
    public Client createClient()
    {
        return createClient(null);
    }

    @Override
    public Client createClient(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/clients", object);

        String clientKey = response.getId();
        return readClient(clientKey);
    }

    @Override
    public void updateClient(Client client)
    {
        getRemote().put("/clients/" + client.getId(), client.getObject());
    }

    @Override
    public void deleteClient(Client client)
    {
        deleteClient(client.getId());
    }

    @Override
    public void deleteClient(String clientId)
    {
        getRemote().delete("/clients/" + clientId);
    }




    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // REGISTRARS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Registrar> listRegistrars()
    {
        return listRegistrars(null);
    }

    @Override
    public ResultMap<Registrar> listRegistrars(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/registrars", params);
        return getFactory().registrars(this, response);
    }

    @Override
    public Registrar readRegistrar(String registrarId)
    {
        Registrar registrar = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/registrars/" + registrarId);
            registrar = getFactory().registrar(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return registrar;
    }

    @Override
    public Registrar createRegistrar()
    {
        return createRegistrar(null);
    }

    @Override
    public Registrar createRegistrar(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/registrars", object);

        String registrarId = response.getId();
        return readRegistrar(registrarId);
    }

    @Override
    public ResultMap<Registrar> queryRegistrars(ObjectNode query)
    {
        return queryRegistrars(query, null);
    }

    @Override
    public ResultMap<Registrar> queryRegistrars(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/registrars/query", params, query);
        return getFactory().registrars(this, response);
    }

    @Override
    public PermissionCheckResults checkRegistrarPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/registrars/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }




    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // WEB HOSTS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<WebHost> listWebHosts()
    {
        return listWebHosts(null);
    }

    @Override
    public ResultMap<WebHost> listWebHosts(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/webhosts", params);
        return getFactory().webhosts(this, response);
    }

    @Override
    public WebHost readWebHost(String webhostId)
    {
        WebHost webhost = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/webhosts/" + webhostId);
            webhost = getFactory().webhost(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return webhost;
    }

    @Override
    public WebHost createWebHost()
    {
        return createWebHost(null);
    }

    @Override
    public WebHost createWebHost(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/webhosts", object);

        String webhostId = response.getId();
        return readWebHost(webhostId);
    }

    @Override
    public ResultMap<WebHost> queryWebHosts(ObjectNode query)
    {
        return queryWebHosts(query, null);
    }

    @Override
    public ResultMap<WebHost> queryWebHosts(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/webhosts/query", params, query);
        return getFactory().webhosts(this, response);
    }

    @Override
    public PermissionCheckResults checkWebHostPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post(getResourceUri() + "/registrars/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BILLIN PROVIDER CONFIGURATIONS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<BillingProviderConfiguration> listBillingProviderConfigurations()
    {
        return listBillingProviderConfigurations(null);
    }

    @Override
    public ResultMap<BillingProviderConfiguration> listBillingProviderConfigurations(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/billing/configurations", params);
        return getFactory().billingProviderConfigurations(this, response);
    }

    @Override
    public ResultMap<BillingProviderConfiguration> queryBillingProviderConfigurations(ObjectNode query)
    {
        return queryBillingProviderConfigurations(query, null);
    }

    @Override
    public ResultMap<BillingProviderConfiguration> queryBillingProviderConfigurations(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/billing/configurations/query", params, query);
        return getFactory().billingProviderConfigurations(this, response);
    }

    @Override
    public BillingProviderConfiguration readBillingProviderConfiguration(String billingProviderConfigurationId)
    {
        BillingProviderConfiguration billingProviderConfiguration = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/billing/configurations/" + billingProviderConfigurationId);
            billingProviderConfiguration = getFactory().billingProviderConfiguration(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return billingProviderConfiguration;
    }

    @Override
    public BillingProviderConfiguration createBillingProviderConfiguration(String providerId, ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }
        
        object.put(BillingProviderConfiguration.FIELD_PROVIDER_ID, providerId);

        Response response = getRemote().post(getResourceUri() + "/billing/configurations", object);

        String billingProviderConfigurationId = response.getId();
        return readBillingProviderConfiguration(billingProviderConfigurationId);
    }

}
