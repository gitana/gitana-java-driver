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
import org.gitana.repo.client.nodes.Association;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.namespace.QName;

import java.util.Map;

/**
 * @author uzi
 */
public interface ObjectFactory
{
    public Repository repository();
    public Repository repository(ObjectNode object);
    public Repository repository(Response response);
    public Map<String, Repository> repositories(Response response);

    public Branch branch(Repository repository);
    public Branch branch(Repository repository, ObjectNode object);
    public Branch branch(Repository repository, Response response);
    public Map<String, Branch> branches(Repository repository, Response response);

    // NOTE: you can't create in-memory changesets, makes no sense
    //public Changeset changeset();
    //public Changeset changeset(ObjectNode object);
    public Changeset changeset(Repository repository, Response response);
    public Map<String, Changeset> changesets(Repository repository, Response response);

    // in-memory
    public Node node(Branch branch, QName typeQName);
    public Node node(Branch branch, QName typeQName, ObjectNode object);
    public Node node(Branch branch, Response response);
    public Map<String, Node> nodes(Branch branch, Response response);

    public Association association(Branch branch, QName typeQName);
    public Association association(Branch branch, QName typeQName, ObjectNode object);
    public Association association(Branch branch, Response response);
    public Map<String, Association> associations(Branch branch, Response response);

    public SecurityUser securityUser();
    public SecurityUser securityUser(ObjectNode object);
    public SecurityUser securityUser(Response response);
    public Map<String, SecurityUser> securityUsers(Response response);

    public SecurityGroup securityGroup();
    public SecurityGroup securityGroup(ObjectNode object);
    public SecurityGroup securityGroup(Response response);
    public Map<String, SecurityGroup> securityGroups(Response response);

    public SecurityPrincipal securityPrincipal(Response response);
    public Map<String, SecurityPrincipal> securityPrincipals(Response response);

}
