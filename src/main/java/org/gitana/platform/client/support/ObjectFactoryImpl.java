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

package org.gitana.platform.client.support;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.archive.ArchiveImpl;
import org.gitana.platform.client.attachment.Attachable;
import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.attachment.AttachmentImpl;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.branch.BranchImpl;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.client.changeset.ChangesetImpl;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.domain.DomainImpl;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.job.JobImpl;
import org.gitana.platform.client.log.LogEntry;
import org.gitana.platform.client.log.LogEntryImpl;
import org.gitana.platform.client.nodes.*;
import org.gitana.platform.client.organization.Organization;
import org.gitana.platform.client.organization.OrganizationImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.platform.PlatformImpl;
import org.gitana.platform.client.principal.DomainGroupImpl;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.principal.DomainUserImpl;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.repository.RepositoryImpl;
import org.gitana.platform.client.team.Team;
import org.gitana.platform.client.team.TeamImpl;
import org.gitana.platform.client.team.Teamable;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.client.vault.VaultImpl;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.services.principals.PrincipalType;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
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
    private Map<QName, Class> registry = new LinkedHashMap<QName, Class>();

    @Override
    public Platform platform(Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new PlatformImpl(response.getObjectNode(), true);
    }

    @Override
    public Repository repository(Platform platform)
    {
        return repository(platform, JsonUtil.createObject());
    }

    @Override
    public Repository repository(Platform platform, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new RepositoryImpl(platform, object, false);
    }

    @Override
    public Repository repository(Platform platform, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new RepositoryImpl(platform, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<Repository> repositories(Platform platform, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Repository> map = new ResultMapImpl<Repository>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Repository repository = new RepositoryImpl(platform, object, true);
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

        return new BranchImpl(repository, object, false);
    }

    @Override
    public Branch branch(Repository repository, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new BranchImpl(repository, response.getObjectNode(), true);
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
            Branch branch = new BranchImpl(repository, object, true);
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

        return new ChangesetImpl(repository, response.getObjectNode(), true);
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
            Changeset changeset = new ChangesetImpl(repository, object, true);
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
    public DomainPrincipal domainPrincipal(Platform platform, ObjectNode object)
    {
        DomainPrincipal principal = null;

        String domainId = JsonUtil.objectGetString(object, "domainId");
        Domain domain = platform.readDomain(domainId);
        if (domain == null)
        {
            throw new RuntimeException("Cannot find domain: " + domainId);
        }

        PrincipalType principalType = PrincipalType.valueOf(JsonUtil.objectGetString(object, DomainPrincipal.FIELD_TYPE));
        if (principalType.equals(PrincipalType.GROUP))
        {
            principal = new DomainGroupImpl(domain, object, true);
        }
        else if (principalType.equals(PrincipalType.USER))
        {
            principal = new DomainUserImpl(domain, object, true);
        }

        return principal;
    }

    @Override
    public DomainPrincipal domainPrincipal(Platform platform, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        DomainPrincipal principal = null;

        ObjectNode object = response.getObjectNode();

        String domainId = JsonUtil.objectGetString(object, "domainId");
        Domain domain = platform.readDomain(domainId);
        if (domain == null)
        {
            throw new RuntimeException("Cannot find domain: " + domainId);
        }

        PrincipalType principalType = PrincipalType.valueOf(JsonUtil.objectGetString(object, DomainPrincipal.FIELD_TYPE));
        if (principalType.equals(PrincipalType.GROUP))
        {
            principal = new DomainGroupImpl(domain, object, true);
        }
        else if (principalType.equals(PrincipalType.USER))
        {
            principal = new DomainUserImpl(domain, object, true);
        }

        return principal;
    }

    @Override
    public ResultMap<DomainPrincipal> domainPrincipals(Platform platform, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<DomainPrincipal> map = new ResultMapImpl<DomainPrincipal>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            String domainId = JsonUtil.objectGetString(object, "domainId");

            Domain domain = platform.readDomain(domainId);
            if (domain == null)
            {
                throw new RuntimeException("Cannot find domain: " + domainId);
            }

            DomainPrincipal principal = domainPrincipal(platform, object);
            map.put(principal.getId(), principal);
        }

        return map;
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
                Class[] signature = new Class[] { Branch.class, ObjectNode.class, Boolean.TYPE };

                Constructor constructor = c.getConstructor(signature);
                if (constructor != null)
                {
                    Object[] args = new Object[] { branch, object, isSaved };
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
                node = new AssociationImpl(branch, object, isSaved);
            }
            else
            {
                node = new NodeImpl(branch, object, isSaved);
            }

        }

        return node;
    }

    @Override
    public Domain domain(Platform platform)
    {
        return domain(platform, JsonUtil.createObject());
    }

    @Override
    public Domain domain(Platform platform, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new DomainImpl(platform, object, false);
    }

    @Override
    public Domain domain(Platform platform, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new DomainImpl(platform, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<Domain> domains(Platform platform, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Domain> map = new ResultMapImpl<Domain>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Domain domain = new DomainImpl(platform, object, true);
            map.put(domain.getId(), domain);
        }

        return map;
    }

    @Override
    public Job job(Platform platform, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new JobImpl(platform, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<Job> jobs(Platform platform, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Job> map = new ResultMapImpl<Job>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Job job = new JobImpl(platform, object, true);
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
            Attachment attachment = new AttachmentImpl(attachable, object);
            map.put(attachment.getId(), attachment);
        }

        return map;
    }

    @Override
    public Archive archive(Vault vault, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new ArchiveImpl(vault, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<Archive> archives(Vault vault, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Archive> map = new ResultMapImpl<Archive>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Archive archive = new ArchiveImpl(vault, object, true);
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
    public LogEntry logEntry(Platform platform, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new LogEntryImpl(platform, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<LogEntry> logEntries(Platform platform, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<LogEntry> map = new ResultMapImpl<LogEntry>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            LogEntry logEntry = new LogEntryImpl(platform, object, true);
            map.put(logEntry.getId(), logEntry);
        }

        return map;
    }

    @Override
    public Team team(Platform platform, Teamable teamable, String teamKey, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new TeamImpl(platform, teamable, teamKey, response.getObjectNode());
    }

    @Override
    public ResultMap<Team> teams(Platform platform, Teamable teamable, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Team> map = new ResultMapImpl<Team>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            String teamKey = JsonUtil.objectGetString(object, "_doc");

            Team team = new TeamImpl(platform, teamable, teamKey, object);
            map.put(team.getKey(), team);
        }

        return map;
    }

    @Override
    public Organization organization(Platform platform, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new OrganizationImpl(platform, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<Organization> organizations(Platform platform, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Organization> map = new ResultMapImpl<Organization>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Organization organization = new OrganizationImpl(platform, object, true);
            map.put(organization.getId(), organization);
        }

        return map;
    }

    @Override
    public Vault vault(Platform platform)
    {
        return vault(platform, JsonUtil.createObject());
    }

    @Override
    public Vault vault(Platform platform, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new VaultImpl(platform, object, false);
    }

    @Override
    public Vault vault(Platform platform, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new VaultImpl(platform, response.getObjectNode(), true);
    }

    @Override
    public ResultMap<Vault> vaults(Platform platform, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        ResultMap<Vault> map = new ResultMapImpl<Vault>(response.getListOffset(), response.getListTotalRows());
        for (ObjectNode object : response.getObjectNodes())
        {
            Vault vault = new VaultImpl(platform, object, true);
            map.put(vault.getId(), vault);
        }

        return map;
    }

}
