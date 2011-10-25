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
import org.gitana.repo.authority.AuthorityGrant;
import org.gitana.repo.client.*;
import org.gitana.repo.client.nodes.*;
import org.gitana.repo.namespace.QName;
import org.gitana.repo.support.ResultMap;
import org.gitana.repo.support.ResultMapImpl;
import org.gitana.security.PrincipalType;
import org.gitana.util.JsonUtil;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class ObjectFactoryImpl implements ObjectFactory
{
    private Driver driver;

    private Map<QName, Class> registry = new LinkedHashMap<QName, Class>();

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
    public ResultMap<Repository> repositories(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Repository> map = new ResultMapImpl<Repository>(response.getListOffset(), response.getListTotalRows());
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
    public ResultMap<Branch> branches(Repository repository, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Branch> map = new ResultMapImpl<Branch>(response.getListOffset(), response.getListTotalRows());
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
    public ResultMap<Changeset> changesets(Repository repository, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Changeset> map = new ResultMapImpl<Changeset>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Changeset changeset = new ChangesetImpl(driver, repository, object, true);
            map.put(changeset.getId(), changeset);
        }

        return map;
    }

    @Override
    public BaseNode node(Branch branch, QName typeQName)
    {
        return node(branch, typeQName, JsonUtil.createObject());
    }

    @Override
    public BaseNode node(Branch branch, QName typeQName, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put(Node.FIELD_TYPE_QNAME, typeQName.toString());

        return produce(branch, object, false);
    }

    @Override
    public BaseNode node(Branch branch, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return produce(branch, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<BaseNode> nodes(Branch branch, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<BaseNode> map = new ResultMapImpl<BaseNode>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            BaseNode node = produce(branch, object, true);
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
    public ResultMap<Association> associations(Branch branch, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Association> map = new ResultMapImpl<Association>(response.getListOffset(), response.getListTotalRows());
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
    public ResultMap<SecurityUser> securityUsers(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<SecurityUser> map = new ResultMapImpl<SecurityUser>(response.getListOffset(), response.getListTotalRows());
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
    public ResultMap<SecurityGroup> securityGroups(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<SecurityGroup> map = new ResultMapImpl<SecurityGroup>(response.getListOffset(), response.getListTotalRows());
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
    public ResultMap<SecurityPrincipal> securityPrincipals(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<SecurityPrincipal> map = new ResultMapImpl<SecurityPrincipal>(response.getListOffset(), response.getListTotalRows());
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
    public ResultMap<Job> jobs(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Job> map = new ResultMapImpl<Job>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Job job = new JobImpl(driver, server, object, true);
            map.put(job.getId(), job);
        }

        return map;
    }

    @Override
    public ResultMap<Attachment> attachments(Attachable attachable, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Attachment> map = new ResultMapImpl<Attachment>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Attachment attachment = new AttachmentImpl(driver, attachable, object);
            map.put(attachment.getId(), attachment);
        }

        return map;
    }

    @Override
    public Archive archive(Server server, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new ArchiveImpl(driver, server, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<Archive> archives(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Archive> map = new ResultMapImpl<Archive>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Archive archive = new ArchiveImpl(driver, server, object, true);
            map.put(archive.getId(), archive);
        }

        return map;
    }

    @Override
    public Map<String, Map<String, AuthorityGrant>> principalAuthorityGrants(Response response)
    {
        Map<String, Map<String, AuthorityGrant>> principalAuthorityGrants = new LinkedHashMap<String, Map<String, AuthorityGrant>>();

        ObjectNode json = response.getObjectNode();
        Iterator<String> principalIds = json.getFieldNames();
        while (principalIds.hasNext())
        {
            String principalId = principalIds.next();

            Map<String, AuthorityGrant> authorityGrants = new LinkedHashMap<String, AuthorityGrant>();

            ObjectNode object = JsonUtil.objectGetObject(json, principalId);
            Iterator<String> grantIds = object.getFieldNames();
            while (grantIds.hasNext())
            {
                String grantId = grantIds.next();

                ObjectNode grantObject = JsonUtil.objectGetObject(object, grantId);
                AuthorityGrant authorityGrant = AuthorityGrant.fromJSON(grantObject);

                authorityGrants.put(grantId, authorityGrant);
            }

            principalAuthorityGrants.put(principalId, authorityGrants);
        }

        return principalAuthorityGrants;
    }

    @Override
    public LogEntry logEntry(Server server, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new LogEntryImpl(driver, server, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<LogEntry> logEntries(Server server, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<LogEntry> map = new ResultMapImpl<LogEntry>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            LogEntry logEntry = new LogEntryImpl(driver, server, object, true);
            map.put(logEntry.getId(), logEntry);
        }

        return map;
    }


}
