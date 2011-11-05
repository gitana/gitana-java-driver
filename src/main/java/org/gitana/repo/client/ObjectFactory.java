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

package org.gitana.repo.client;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.authority.AuthorityGrant;
import org.gitana.repo.client.nodes.Association;
import org.gitana.repo.client.nodes.BaseNode;
import org.gitana.repo.namespace.QName;
import org.gitana.repo.support.ResultMap;

import java.util.Map;

/**
 * @author uzi
 */
public interface ObjectFactory
{
    public Repository repository(Server server);
    public Repository repository(Server server, ObjectNode object);
    public Repository repository(Server server, Response response);
    public ResultMap<Repository> repositories(Server server, Response response);

    public Branch branch(Repository repository);
    public Branch branch(Repository repository, ObjectNode object);
    public Branch branch(Repository repository, Response response);
    public ResultMap<Branch> branches(Repository repository, Response response);

    // NOTE: you can't create in-memory changesets, makes no sense
    //public Changeset changeset();
    //public Changeset changeset(ObjectNode object);
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

    public SecurityUser securityUser(Server server);
    public SecurityUser securityUser(Server server, ObjectNode object);
    public SecurityUser securityUser(Server server, Response response);
    public ResultMap<SecurityUser> securityUsers(Server server, Response response);

    public SecurityGroup securityGroup(Server server);
    public SecurityGroup securityGroup(Server server, ObjectNode object);
    public SecurityGroup securityGroup(Server server, Response response);
    public ResultMap<SecurityGroup> securityGroups(Server server, Response response);

    public SecurityPrincipal securityPrincipal(Server server, Response response);
    public ResultMap<SecurityPrincipal> securityPrincipals(Server server, Response response);


    // dynamic node registry
    public void register(QName typeQName, Class implementationClass);

    // produces a node (raw function)
    public BaseNode produce(Branch branch, ObjectNode object, boolean isSaved);

    // jobs
    public Job job(Server server, Response response);
    public ResultMap<Job> jobs(Server server, Response response);

    // attachments
    public ResultMap<Attachment> attachments(Attachable attachable, Response response);

    // archives
    public Archive archive(Server server, Response response);
    public ResultMap<Archive> archives(Server server, Response response);

    public Map<String, Map<String, AuthorityGrant>> principalAuthorityGrants(Response response);

    // log entries
    public LogEntry logEntry(Server server, Response response);
    public ResultMap<LogEntry> logEntries(Server server, Response response);

    // teams
    public Team team(Server server, Teamable teamable, String teamKey, Response response);
    public ResultMap<Team> teams(Server server, Teamable teamable, Response response);

    // organizations
    public Organization organization(Server server, Response response);
    public ResultMap<Organization> organizations(Server server, Response response);

}
