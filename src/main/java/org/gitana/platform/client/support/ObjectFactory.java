/**
 * Copyright 2017 Gitana Software, Inc.
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

package org.gitana.platform.client.support;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.api.AuthenticationGrant;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.application.*;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.attachment.Attachable;
import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.billing.BillingProviderConfiguration;
import org.gitana.platform.client.billing.BillingTransaction;
import org.gitana.platform.client.billing.PaymentMethod;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.deletion.Deletion;
import org.gitana.platform.client.directory.Directory;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.identity.Identity;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.log.LogEntry;
import org.gitana.platform.client.meter.Meter;
import org.gitana.platform.client.node.Association;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.plan.Plan;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.project.Project;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.release.Release;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.team.Team;
import org.gitana.platform.client.team.Teamable;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.client.warehouse.*;
import org.gitana.platform.client.webhost.AutoClientMapping;
import org.gitana.platform.client.webhost.DeployedApplication;
import org.gitana.platform.client.webhost.WebHost;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;

import java.util.Map;

/**
 * @author uzi
 */
public interface ObjectFactory
{
    public Platform platform(Cluster cluster, Response response);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // REPOSITORIES
    //

    public Repository repository(Platform platform);
    public Repository repository(Platform platform, ObjectNode object);
    public Repository repository(Platform platform, Response response);
    public ResultMap<Repository> repositories(Platform platform, Response response);

    public Branch branch(Repository repository);
    public Branch branch(Repository repository, ObjectNode object);
    public Branch branch(Repository repository, Response response);
    public ResultMap<Branch> branches(Repository repository, Response response);

    public Release release(Repository repository);
    public Release release(Repository repository, ObjectNode object);
    public Release release(Repository repository, Response response);
    public ResultMap<Release> releases(Repository repository, Response response);

    public Changeset changeset(Repository repository, Response response);
    public ResultMap<Changeset> changesets(Repository repository, Response response);

    // in-memory
    public BaseNode node(Branch branch, QName typeQName);
    public BaseNode node(Branch branch, QName typeQName, ObjectNode object);
    public BaseNode node(Branch branch, Response response);
    public ResultMap<BaseNode> nodes(Branch branch, Response response);

    public Association association(Branch branch, QName typeQName);
    public Association association(Branch branch, QName typeQName, ObjectNode object);
    public Association association(Branch branch, Response response);
    public ResultMap<Association> associations(Branch branch, Response response);

    // dynamic node registry
    public void register(QName typeQName, Class implementationClass);

    // produces a node (raw function)
    public BaseNode produce(Branch branch, ObjectNode object, boolean isSaved);

    public Deletion deletion(Branch branch, ObjectNode object);
    public Deletion deletion(Branch branch, Response response);
    public ResultMap<Deletion> deletions(Branch branch, Response response);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DOMAINS
    //

    public Domain domain(Platform platform);
    public Domain domain(Platform platform, ObjectNode object);
    public Domain domain(Platform platform, Response response);
    public ResultMap<Domain> domains(Platform platform, Response response);

    public DomainPrincipal domainPrincipal(Platform platform, ObjectNode object);
    public DomainPrincipal domainPrincipal(Platform platform, Response response);
    public ResultMap<DomainPrincipal> domainPrincipals(Platform platform, Response response);
    public DomainPrincipal domainPrincipal(Domain domain, ObjectNode object);
    public DomainPrincipal domainPrincipal(Domain domain, Response response);
    public ResultMap<DomainPrincipal> domainPrincipals(Domain domain, Response response);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // DIRECTORIES
    //

    public Directory directory(Platform platform);
    public Directory directory(Platform platform, ObjectNode object);
    public Directory directory(Platform platform, Response response);
    public ResultMap<Directory> directories(Platform platform, Response response);

    public Identity identity(Directory directory, ObjectNode object);
    public Identity identity(Directory directory, Response response);
    public ResultMap<Identity> identities(Directory directory, Response response);



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // PLATFORM
    //

    // stacks
    public Stack stack(Platform platform, Response response);
    public ResultMap<Stack> stacks(Platform platform, Response response);

    // clients
    public Client client(Platform platform, ObjectNode object);
    public Client client(Platform platform, Response response);
    public ResultMap<Client> clients(Platform platform, Response response);

    // authentication grants
    public AuthenticationGrant authenticationGrant(Platform platform, ObjectNode object);
    public AuthenticationGrant authenticationGrant(Platform platform, Response response);
    public ResultMap<AuthenticationGrant> authenticationGrants(Platform platform, Response response);

    // projects
    public Project project(Platform platform, Response response);
    public ResultMap<Project> projects(Platform platform, Response response);



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CLUSTER
    //

    // jobs
    public Job job(Cluster cluster, Response response);
    public ResultMap<Job> jobs(Cluster cluster, Response response);

    // log entries
    public LogEntry logEntry(Cluster cluster, Response response);
    public ResultMap<LogEntry> logEntries(Cluster cluster, Response response);




    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // VAULT
    //

    public Vault vault(Platform platform);
    public Vault vault(Platform platform, ObjectNode object);
    public Vault vault(Platform platform, Response response);
    public ResultMap<Vault> vaults(Platform platform, Response response);

    // archives
    public Archive archive(Vault vault, Response response);
    public ResultMap<Archive> archives(Vault vault, Response response);



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // APPLICATION
    //

    public Application application(Platform platform);
    public Application application(Platform platform, ObjectNode object);
    public Application application(Platform platform, Response response);
    public ResultMap<Application> applications(Platform platform, Response response);

    // settings
    public Settings settings(Application application, Response response);
    public ResultMap<Settings> settingsMap(Application application, Response response);

    // registrations
    public Registration registration(Application application, Response response);
    public ResultMap<Registration> registrations(Application application, Response response);

    // emails
    public Email email(Application application, Response response);
    public ResultMap<Email> emails(Application application, Response response);

    // email providers
    public EmailProvider emailProvider(Application application, Response response);
    public ResultMap<EmailProvider> emailProviders(Application application, Response response);



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // VAULT
    //

    public Registrar registrar(Platform platform);
    public Registrar registrar(Platform platform, ObjectNode object);
    public Registrar registrar(Platform platform, Response response);
    public ResultMap<Registrar> registrars(Platform platform, Response response);

    // tenants
    public Tenant tenant(Registrar registrar, ObjectNode object);
    public Tenant tenant(Registrar registrar, Response response);
    public ResultMap<Tenant> tenants(Registrar registrar, Response response);

    // plans
    public Plan plan(Registrar registrar, ObjectNode object);
    public Plan plan(Registrar registrar, Response response);
    public ResultMap<Plan> plans(Registrar registrar, Response response);

    // meters
    public Meter meter(Registrar registrar, ObjectNode object);
    public Meter meter(Registrar registrar, Response response);
    public ResultMap<Meter> meters(Registrar registrar, Response response);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // WEB HOST
    //

    public WebHost webhost(Platform platform);
    public WebHost webhost(Platform platform, ObjectNode object);
    public WebHost webhost(Platform platform, Response response);
    public ResultMap<WebHost> webhosts(Platform platform, Response response);

    // auto client mappings
    public AutoClientMapping autoClientMapping(WebHost webhost, Response response);
    public ResultMap<AutoClientMapping> autoClientMappings(WebHost webhost, Response response);

    // deployed applications
    public DeployedApplication deployedApplication(WebHost webhost, Response response);
    public ResultMap<DeployedApplication> deployedApplications(WebHost webhost, Response response);


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // WARE HOUSE
    //

    public Warehouse warehouse(Platform platform);
    public Warehouse warehouse(Platform platform, ObjectNode object);
    public Warehouse warehouse(Platform platform, Response response);
    public ResultMap<Warehouse> warehouses(Platform platform, Response response);

    // interactions
    public Interaction interaction(Warehouse warehouse, Response response);
    public ResultMap<Interaction> interactions(Warehouse warehouse, Response response);

    // interaction applications
    public InteractionApplication interactionApplication(Warehouse warehouse, Response response);
    public ResultMap<InteractionApplication> interactionApplications(Warehouse warehouse, Response response);

    // interaction nodes
    public InteractionNode interactionNode(Warehouse warehouse, Response response);
    public ResultMap<InteractionNode> interactionNodes(Warehouse warehouse, Response response);

    // interaction pages
    public InteractionPage interactionPage(Warehouse warehouse, Response response);
    public ResultMap<InteractionPage> interactionPages(Warehouse warehouse, Response response);

    // interaction reports
    public InteractionReport interactionReport(Warehouse warehouse, Response response);
    public ResultMap<InteractionReport> interactionReports(Warehouse warehouse, Response response);

    // interaction report entries
    public InteractionReportEntry interactionReportEntry(Warehouse warehouse, Response response);
    public ResultMap<InteractionReportEntry> interactionReportEntries(Warehouse warehouse, Response response);

    // interaction session
    public InteractionSession interactionSession(Warehouse warehouse, Response response);
    public ResultMap<InteractionSession> interactionSessions(Warehouse warehouse, Response response);

    // interaction user
    public InteractionUser interactionUser(Warehouse warehouse, Response response);
    public ResultMap<InteractionUser> interactionUsers(Warehouse warehouse, Response response);

    public PlatformDataStore platformDataStore(Platform platform, ObjectNode object);
    public ResultMap<PlatformDataStore> platformDataStores(Platform platform, Response response);




    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BILLING
    //

    // payment methods
    public PaymentMethod paymentMethod(Tenant tenant, Response response);
    public ResultMap<PaymentMethod> paymentMethods(Tenant tenant, Response response);

    // billing transaction
    public BillingTransaction billingTransaction(Tenant tenant, Response response);
    public ResultMap<BillingTransaction> billingTransactions(Tenant tenant, Response response);

    // billing provider configuration
    public BillingProviderConfiguration billingProviderConfiguration(Platform platform, Response response);
    public ResultMap<BillingProviderConfiguration> billingProviderConfigurations(Platform platform, Response response);



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MISCELLANEOUS
    //

    // attachments
    public ResultMap<Attachment> attachments(Attachable attachable, Response response);
    public Map<String, Map<String, AuthorityGrant>> principalAuthorityGrants(Response response);

    // teams
    public Team team(Cluster cluster, Teamable teamable, String teamKey, Response response);
    public ResultMap<Team> teams(Cluster cluster, Teamable teamable, Response response);
    

}
