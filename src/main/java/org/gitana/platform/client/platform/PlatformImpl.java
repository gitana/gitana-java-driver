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
import org.gitana.platform.client.datastore.AbstractDataStoreImpl;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.log.LogEntry;
import org.gitana.platform.client.organization.Organization;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.repository.Repository;
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
public class PlatformImpl extends AbstractDataStoreImpl implements Platform
{
    public PlatformImpl(ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);
    }

    public PlatformImpl(String platformId)
    {
        this(JsonUtil.createObject(), true);

        setId(platformId);
    }

    @Override
    public String getType()
    {
        return "platform";
    }

    @Override
    protected Platform getPlatform()
    {
        return this;
    }

    @Override
    public String getResourceUri()
    {
        return "";
    }

    @Override
    public Domain readDefaultDomain()
    {
        return readDomain("default");
    }

    @Override
    public Vault readDefaultVault()
    {
        return readVault("default");
    }

    @Override
    public void reload()
    {
        Response response = getRemote().get(getResourceUri());
        Platform platform = getFactory().platform(response);
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
    // JOBS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Job> queryJobs(ObjectNode query)
    {
        return queryJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/query", params, query);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listUnstartedJobs()
    {
        return listUnstartedJobs(null);
    }

    @Override
    public ResultMap<Job> listUnstartedJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/unstarted", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryUnstartedJobs(ObjectNode query)
    {
        return queryUnstartedJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryUnstartedJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/unstarted/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listRunningJobs()
    {
        return listRunningJobs(null);
    }

    @Override
    public ResultMap<Job> listRunningJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/running", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryRunningJobs(ObjectNode query)
    {
        return queryRunningJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryRunningJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/running/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listFailedJobs()
    {
        return listFailedJobs(null);
    }

    @Override
    public ResultMap<Job> listFailedJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/failed", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryFailedJobs(ObjectNode query)
    {
        return queryFailedJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryFailedJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/failed/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listCandidateJobs()
    {
        return listCandidateJobs(null);
    }

    @Override
    public ResultMap<Job> listCandidateJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/candidate", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryCandidateJobs(ObjectNode query)
    {
        return queryCandidateJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryCandidateJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/candidate/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> listFinishedJobs()
    {
        return listFinishedJobs(null);
    }

    @Override
    public ResultMap<Job> listFinishedJobs(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/jobs/finished", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public ResultMap<Job> queryFinishedJobs(ObjectNode query)
    {
        return queryFinishedJobs(query, null);
    }

    @Override
    public ResultMap<Job> queryFinishedJobs(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/jobs/finished/query", params);
        return getFactory().jobs(this, response);
    }

    @Override
    public Job readJob(String jobId)
    {
        Job job = null;

        try
        {
            Response response = getRemote().get("/jobs/" + jobId);
            job = getFactory().job(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return job;
    }

    @Override
    public void killJob(String jobId)
    {
        getRemote().post("/jobs/" + jobId);
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
        return getFactory().logEntries(this, response);
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
        return getFactory().logEntries(this, response);
    }

    @Override
    public LogEntry readLogEntry(String logEntryId)
    {
        LogEntry logEntry = null;

        try
        {
            Response response = getRemote().get("/logs/" + logEntryId);
            logEntry = getFactory().logEntry(this, response);
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
    // ORGANIZATIONS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public ResultMap<Organization> listOrganizations()
    {
        return listOrganizations(null);
    }

    @Override
    public ResultMap<Organization> listOrganizations(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/organizations", params);
        return getFactory().organizations(this, response);
    }

    @Override
    public ResultMap<Organization> queryOrganizations(ObjectNode query)
    {
        return queryOrganizations(query, null);
    }

    @Override
    public ResultMap<Organization> queryOrganizations(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/organizations/query", params, query);
        return getFactory().organizations(this, response);
    }

    @Override
    public Organization readOrganization(String organizationId)
    {
        Organization organization = null;

        try
        {
            Response response = getRemote().get("/organizations/" + organizationId);
            organization = getFactory().organization(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return organization;
    }

    @Override
    public Organization createOrganization()
    {
        return createOrganization(null);
    }

    @Override
    public Organization createOrganization(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/organizations", object);

        String organizationId = response.getId();
        return readOrganization(organizationId);
    }

    @Override
    public void updateOrganization(Organization organization)
    {
        getRemote().put("/organizations/" + organization.getId(), organization.getObject());
    }

    @Override
    public void deleteOrganization(Organization organization)
    {
        deleteOrganization(organization.getId());
    }

    @Override
    public void deleteOrganization(String organizationId)
    {
        getRemote().delete("/organizations/" + organizationId);
    }

    @Override
    public PermissionCheckResults checkOrganizationPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post("/organizations/permissions/check", object);
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

    @Override
    public Consumer lookupDefaultConsumerForTenant(String tenantId)
    {
        ObjectNode query = JsonUtil.createObject();
        query.put(Consumer.FIELD_IS_TENANT_DEFAULT, true);
        query.put(Consumer.FIELD_DEFAULT_TENANT_ID, tenantId);

        Consumer consumer = null;

        ResultMap<Consumer> consumers = queryConsumers(query);
        if (consumers.size() > 0)
        {
            consumer = consumers.values().iterator().next();
        }

        return consumer;
    }


}
