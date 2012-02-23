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

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.billing.Billing;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.datastore.DataStore;
import org.gitana.platform.client.directory.Directory;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.log.LogEntry;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

import java.util.List;

/**
 * @author uzi
 */
public interface Platform extends DataStore
{
    /**
     * @return the cluster instance
     */
    public Cluster getCluster();
    
    public String getDefaultDomainId();
    
    public Domain readDefaultDomain();

    public String getDefaultDirectoryId();

    public Directory readDefaultDirectory();

    public String getOwnerRegistrarId();
    public String getOwnerTenantId();
    public Registrar getOwnerRegistrar();
    public Tenant getOwnerTenant();

    
    // billing
    public Billing getBilling();


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // REPOSITORIES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieves repositories from the server as a map.
     *
     * @return a map of repository objects keyed by repository id
     */
    public ResultMap<Repository> listRepositories();

    /**
     * Retrieves repositories from the server as a map.
     *
     * @param pagination
     *
     * @return a map of repository objects keyed by repository id
     */
    public ResultMap<Repository> listRepositories(Pagination pagination);

    /**
     * Reads a single repository from the server.
     *
     * @param repositoryId
     *
     * @return repository
     */
    public Repository readRepository(String repositoryId);

    /**
     * Creates an empty repository on the server.
     *
     * @return repository
     */
    public Repository createRepository();

    /**
     * Creates a repository on the server.
     *
     * @param object
     *
     * @return repository
     */
    public Repository createRepository(ObjectNode object);

    /**
     * Performs a query over the repository index.
     *
     * @param query
     * @return
     */
    public ResultMap<Repository> queryRepositories(ObjectNode query);

    public ResultMap<Repository> queryRepositories(ObjectNode query, Pagination pagination);

    /**
     * Checks permissions on multiple repository/principal combinations.
     *
     * @param list
     * @return
     */
    public PermissionCheckResults checkRepositoryPermissions(List<PermissionCheck> list);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DOMAINS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Domain> listDomains();

    public ResultMap<Domain> listDomains(Pagination pagination);

    public Domain readDomain(String domainId);

    public Domain createDomain();

    public Domain createDomain(ObjectNode object);

    public ResultMap<Domain> queryDomains(ObjectNode query);

    public ResultMap<Domain> queryDomains(ObjectNode query, Pagination pagination);

    public PermissionCheckResults checkDomainPermissions(List<PermissionCheck> list);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // VAULTS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Vault> listVaults();

    public ResultMap<Vault> listVaults(Pagination pagination);

    public Vault readVault(String vaultId);

    public Vault createVault();

    public Vault createVault(ObjectNode object);

    public ResultMap<Vault> queryVaults(ObjectNode query);

    public ResultMap<Vault> queryVaults(ObjectNode query, Pagination pagination);

    public PermissionCheckResults checkVaultPermissions(List<PermissionCheck> list);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // REGISTRARS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Registrar> listRegistrars();

    public ResultMap<Registrar> listRegistrars(Pagination pagination);

    public Registrar readRegistrar(String registrarId);

    public Registrar createRegistrar();

    public Registrar createRegistrar(ObjectNode object);

    public ResultMap<Registrar> queryRegistrars(ObjectNode query);

    public ResultMap<Registrar> queryRegistrars(ObjectNode query, Pagination pagination);

    public PermissionCheckResults checkRegistrarPermissions(List<PermissionCheck> list);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // APPLICATIONS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Application> listApplications();

    public ResultMap<Application> listApplications(Pagination pagination);

    public Application readApplication(String applicationId);

    public Application createApplication();

    public Application createApplication(ObjectNode object);

    public ResultMap<Application> queryApplications(ObjectNode query);

    public ResultMap<Application> queryApplications(ObjectNode query, Pagination pagination);

    public PermissionCheckResults checkApplicationPermissions(List<PermissionCheck> list);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DIRECTORIES
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Directory> listDirectories();

    public ResultMap<Directory> listDirectories(Pagination pagination);

    public Directory readDirectory(String directoryId);

    public Directory createDirectory();

    public Directory createDirectory(ObjectNode object);

    public ResultMap<Directory> queryDirectories(ObjectNode query);

    public ResultMap<Directory> queryDirectories(ObjectNode query, Pagination pagination);

    public PermissionCheckResults checkDirectoryPermissions(List<PermissionCheck> list);





    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // LOGS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<LogEntry> listLogEntries();
    public ResultMap<LogEntry> listLogEntries(Pagination pagination);
    public ResultMap<LogEntry> queryLogEntries(ObjectNode query);
    public ResultMap<LogEntry> queryLogEntries(ObjectNode query, Pagination pagination);
    public LogEntry readLogEntry(String logEntryId);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // STACKS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Stack> listStacks();
    public ResultMap<Stack> listStacks(Pagination pagination);
    public ResultMap<Stack> queryStacks(ObjectNode query);
    public ResultMap<Stack> queryStacks(ObjectNode query, Pagination pagination);
    public Stack readStack(String stackId);
    public Stack createStack();
    public Stack createStack(ObjectNode object);
    public void updateStack(Stack stack);
    public void deleteStack(Stack stack);
    public void deleteStack(String stackId);
    public PermissionCheckResults checkStackPermissions(List<PermissionCheck> list);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CLIENTS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Client> listClients();
    public ResultMap<Client> listClients(Pagination pagination);
    public ResultMap<Client> queryClients(ObjectNode query);
    public ResultMap<Client> queryClients(ObjectNode query, Pagination pagination);
    public Client readClient(String clientId);
    public Client createClient();
    public Client createClient(ObjectNode object);
    public void updateClient(Client client);
    public void deleteClient(Client client);
    public void deleteClient(String clientId);

}
