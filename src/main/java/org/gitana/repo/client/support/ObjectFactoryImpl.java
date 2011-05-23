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

package org.gitana.repo.client.support;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.*;
import org.gitana.repo.client.nodes.*;
import org.gitana.repo.namespace.QName;
import org.gitana.security.PrincipalType;
import org.gitana.util.JsonUtil;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class ObjectFactoryImpl implements ObjectFactory
{
    private Driver driver;

    private Map<QName, Class> registry = new HashMap<QName, Class>();

    public ObjectFactoryImpl(Driver driver)
    {
        this.driver = driver;
    }

    @Override
    public Repository repository(Server server)
    {
        return repository(server, JsonUtil.createObject());
    }

    @Override
    public Repository repository(Server server, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new RepositoryImpl(driver, server, object, false);
    }

    @Override
    public Repository repository(Server server, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new RepositoryImpl(driver, server, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Repository> repositories(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Repository> map = new HashMap<String, Repository>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Repository repository = new RepositoryImpl(driver, server, object, true);
            map.put(repository.getId(), repository);
        }

        return map;
    }

    @Override
    public Branch branch(Repository repository)
    {
        return branch(repository, JsonUtil.createObject());
    }

    @Override
    public Branch branch(Repository repository, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new BranchImpl(driver, repository, object, false);
    }

    @Override
    public Branch branch(Repository repository, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new BranchImpl(driver, repository, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Branch> branches(Repository repository, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Branch> map = new HashMap<String, Branch>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Branch branch = new BranchImpl(driver, repository, object, true);
            map.put(branch.getId(), branch);
        }

        return map;
    }

    @Override
    public Changeset changeset(Repository repository, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new ChangesetImpl(driver, repository, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Changeset> changesets(Repository repository, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Changeset> map = new HashMap<String, Changeset>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Changeset changeset = new ChangesetImpl(driver, repository, object, true);
            map.put(changeset.getId(), changeset);
        }

        return map;
    }

    @Override
    public Node node(Branch branch, QName typeQName)
    {
        return node(branch, typeQName, JsonUtil.createObject());
    }

    @Override
    public Node node(Branch branch, QName typeQName, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put(Node.FIELD_TYPE_QNAME, typeQName.toString());

        return (Node) produce(branch, object, false);
    }

    @Override
    public Node node(Branch branch, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return (Node) produce(branch, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Node> nodes(Branch branch, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Node> map = new HashMap<String, Node>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Node node = (Node) produce(branch, object, true);
            map.put(node.getId(), node);
        }

        return map;
    }

    @Override
    public Association association(Branch branch, QName typeQName)
    {
        return association(branch, typeQName, JsonUtil.createObject());
    }

    @Override
    public Association association(Branch branch, QName typeQName, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put(Node.FIELD_TYPE_QNAME, typeQName.toString());

        return (Association) produce(branch, object, false);
    }

    @Override
    public Association association(Branch branch, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return (Association) produce(branch, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Association> associations(Branch branch, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Association> map = new HashMap<String, Association>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Association association = (Association) produce(branch, object, true);
            map.put(association.getId(), association);
        }

        return map;
    }

    @Override
    public SecurityUser securityUser(Server server)
    {
        return securityUser(server, JsonUtil.createObject());
    }

    @Override
    public SecurityUser securityUser(Server server, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new SecurityUserImpl(driver, server, object, false);
    }

    @Override
    public SecurityUser securityUser(Server server, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new SecurityUserImpl(driver, server, response.getObjectNode(), true);
    }

    @Override
    public Map<String, SecurityUser> securityUsers(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, SecurityUser> map = new HashMap<String, SecurityUser>();
        for (ObjectNode object : response.getObjectNodes())
        {
            SecurityUser user = new SecurityUserImpl(driver, server, object, true);
            map.put(user.getId(), user);
        }

        return map;
    }

    @Override
    public SecurityGroup securityGroup(Server server)
    {
        return securityGroup(server, JsonUtil.createObject());
    }

    @Override
    public SecurityGroup securityGroup(Server server, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new SecurityGroupImpl(driver, server, object, false);
    }

    @Override
    public SecurityGroup securityGroup(Server server, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new SecurityGroupImpl(driver, server, response.getObjectNode(), true);
    }

    @Override
    public Map<String, SecurityGroup> securityGroups(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, SecurityGroup> map = new HashMap<String, SecurityGroup>();
        for (ObjectNode object : response.getObjectNodes())
        {
            SecurityGroup group = new SecurityGroupImpl(driver, server, object, true);
            map.put(group.getId(), group);
        }

        return map;
    }

    @Override
    public SecurityPrincipal securityPrincipal(Server server, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        ObjectNode object = response.getObjectNode();

        SecurityPrincipal principal = null;

        PrincipalType principalType = PrincipalType.valueOf(JsonUtil.objectGetString(object, SecurityPrincipal.FIELD_PRINCIPAL_TYPE));
        if (principalType.equals(PrincipalType.GROUP))
        {
            principal = new SecurityGroupImpl(driver, server, object, true);
        }
        else if (principalType.equals(PrincipalType.USER))
        {
            principal = new SecurityUserImpl(driver, server, object, true);
        }

        return principal;
    }

    @Override
    public Map<String, SecurityPrincipal> securityPrincipals(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, SecurityPrincipal> map = new HashMap<String, SecurityPrincipal>();
        for (ObjectNode object : response.getObjectNodes())
        {
            SecurityPrincipal principal = securityPrincipal(server, object);
            map.put(principal.getId(), principal);
        }

        return map;
    }

    private SecurityPrincipal securityPrincipal(Server server, ObjectNode object)
    {
        SecurityPrincipal principal = null;

        String principalTypeId = JsonUtil.objectGetString(object, SecurityPrincipal.FIELD_PRINCIPAL_TYPE);

        PrincipalType principalType = PrincipalType.valueOf(principalTypeId);
        if (principalType.equals(PrincipalType.GROUP))
        {
            principal = new SecurityGroupImpl(driver, server, object, true);
        }
        else if (principalType.equals(PrincipalType.USER))
        {
            principal = new SecurityUserImpl(driver, server, object, true);
        }

        return principal;
    }


    @Override
    public void register(QName typeQName, Class implementationClass)
    {
        registry.put(typeQName, implementationClass);
    }

    @Override
    public BaseNode produce(Branch branch, ObjectNode object, boolean isSaved)
    {
        BaseNode node = null;

        if (!object.has(Node.FIELD_ID))
        {
            throw new RuntimeException("Object is missing field: " + Node.FIELD_ID);
        }

        // type qname
        QName typeQName = QName.create(JsonUtil.objectGetString(object, Node.FIELD_TYPE_QNAME));

        // find implementation class
        Class c = registry.get(typeQName);
        if (c != null)
        {
            try
            {
                Class[] signature = new Class[] { Driver.class, Branch.class, ObjectNode.class, Boolean.TYPE };

                Constructor constructor = c.getConstructor(signature);
                if (constructor != null)
                {
                    Object[] args = new Object[] { driver, branch, object, isSaved };
                    node = (BaseNode) constructor.newInstance(args);
                }
                else
                {
                    throw new RuntimeException("No constructor found for signature: " + signature);
                }
            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }

        // if we didn't produce a node for a registered type, we can build a default kind of node
        // either a node impl or an association impl
        if (node == null)
        {
            boolean isAssociation = false;
            if (object.has("_is_association"))
            {
                isAssociation = object.get("_is_association").getBooleanValue();
            }

            if (isAssociation)
            {
                node = new AssociationImpl(driver, branch, object, isSaved);
            }
            else
            {
                node = new NodeImpl(driver, branch, object, isSaved);
            }

        }

        return node;
    }

    @Override
    public Job job(Server server, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new JobImpl(driver, server, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Job> jobs(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Job> map = new HashMap<String, Job>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Job job = new JobImpl(driver, server, object, true);
            map.put(job.getId(), job);
        }

        return map;
    }



}
