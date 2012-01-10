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
import org.gitana.platform.client.api.Consumer;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.cluster.AbstractClusterDataStoreImpl;
import org.gitana.platform.client.cluster.Cluster;
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
    // CONSUMERS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Consumer> listConsumers()
    {
        return listConsumers(null);
    }

    @Override
    public ResultMap<Consumer> listConsumers(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getResourceUri() + "/consumers", params);
        return getFactory().consumers(this, response);
    }

    @Override
    public ResultMap<Consumer> queryConsumers(ObjectNode query)
    {
        return queryConsumers(query, null);
    }

    @Override
    public ResultMap<Consumer> queryConsumers(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post(getResourceUri() + "/consumers/query", params, query);
        return getFactory().consumers(this, response);
    }

    @Override
    public Consumer readConsumer(String consumerKey)
    {
        Consumer consumer = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/consumers/" + consumerKey);
            consumer = getFactory().consumer(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return consumer;
    }

    @Override
    public Consumer createConsumer()
    {
        return createConsumer(null);
    }

    @Override
    public Consumer createConsumer(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post(getResourceUri() + "/consumers", object);

        String consumerId = response.getId();
        return readConsumer(consumerId);
    }

    @Override
    public void updateConsumer(Consumer consumer)
    {
        getRemote().put("/consumers/" + consumer.getId(), consumer.getObject());
    }

    @Override
    public void deleteConsumer(Consumer consumer)
    {
        deleteConsumer(consumer.getKey());
    }

    @Override
    public void deleteConsumer(String consumerKey)
    {
        getRemote().delete("/consumers/" + consumerKey);
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

}
