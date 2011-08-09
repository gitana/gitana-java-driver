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
import org.gitana.mimetype.MimeTypeMap;
import org.gitana.repo.client.*;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.ResultMap;
import org.gitana.security.PrincipalType;
import org.gitana.util.JsonUtil;
import org.gitana.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class ServerImpl implements Server
{
    private Driver driver;

    public ServerImpl(Driver driver)
    {
        this.driver = driver;

        this.init();
    }

    protected void init()
    {
    }

    protected Remote getRemote()
    {
        return driver.getRemote();
    }

    public ObjectFactory getFactory()
    {
        return driver.getFactory();
    }

    @Override
    public Driver getDriver()
    {
        return driver;
    }

    /*
    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Server)
        {
            Server other = (Server) object;

            return (this.gitana.)
            equals = (this.getId().equals(other.getId())) && (this.getType().equals(other.getType()));
        }

        return equals;
    }
    */


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // REPOSITORIES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Repository> fetchRepositories()
    {
        return fetchRepositories(null);
    }

    @Override
    public ResultMap<Repository> fetchRepositories(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/repositories", params);
        return getFactory().repositories(this, response);
    }

    @Override
    public List<Repository> listRepositories()
    {
        return listRepositories(null);
    }

    @Override
    public List<Repository> listRepositories(Pagination pagination)
    {
        Map<String, Repository> map = fetchRepositories(pagination);

        List<Repository> list = new ArrayList<Repository>();
        for (Repository repository : map.values())
        {
            list.add(repository);
        }

        return list;
    }

    @Override
    public Repository readRepository(String repositoryId)
    {
        Repository repository = null;

        try
        {
            Response response = getRemote().get("/repositories/" + repositoryId);
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
        return createRepository(JsonUtil.createObject());
    }

    @Override
    public Repository createRepository(ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/repositories", object);

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

        Response response = getRemote().post("/repositories/query", params, query);
        return getFactory().repositories(this, response);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get("/acl");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/acl/" + principalId + "/grant/" + authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/acl/" + principalId + "/revoke/" + authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SECURITY GROUPS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<SecurityGroup> fetchGroups()
    {
        return fetchGroups(null);
    }

    @Override
    public ResultMap<SecurityGroup> fetchGroups(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/security/groups", params);
        return getFactory().securityGroups(this, response);
    }

    @Override
    public List<SecurityGroup> listGroups()
    {
        return listGroups(null);
    }

    @Override
    public List<SecurityGroup> listGroups(Pagination pagination)
    {
        Map<String, SecurityGroup> map = fetchGroups(pagination);

        List<SecurityGroup> list = new ArrayList<SecurityGroup>();
        for (SecurityGroup group : map.values())
        {
            list.add(group);
        }

        return list;
    }

    @Override
    public SecurityGroup readGroup(String groupId)
    {
        SecurityGroup group = null;

        try
        {
            Response response = getRemote().get("/security/groups/" + groupId);
            group = getFactory().securityGroup(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return group;
    }

    @Override
    public SecurityGroup createGroup(String groupId)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put(SecurityGroup.FIELD_PRINCIPAL_ID, groupId);

        return createGroup(object);
    }

    @Override
    public SecurityGroup createGroup(ObjectNode object)
    {
        if (object == null)
        {
            throw new RuntimeException("Object required");
        }

        // ensure object has principal id
        if (!object.has(SecurityGroup.FIELD_PRINCIPAL_ID))
        {
            throw new RuntimeException("Missing principal id");
        }

        object.put(SecurityGroup.FIELD_PRINCIPAL_TYPE, PrincipalType.GROUP.toString());

        Response response = getRemote().post("/security/groups", object);

        // NOTE: "_doc" doesn't come back in response?
        //String groupId = response.getId();
        String groupId = object.get(SecurityGroup.FIELD_PRINCIPAL_ID).getTextValue();
        return readGroup(groupId);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SECURITY USERS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<SecurityUser> fetchUsers()
    {
        return fetchUsers(null);
    }

    @Override
    public ResultMap<SecurityUser> fetchUsers(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/security/users", params);
        return getFactory().securityUsers(this, response);
    }

    @Override
    public List<SecurityUser> listUsers()
    {
        return listUsers(null);
    }

    @Override
    public List<SecurityUser> listUsers(Pagination pagination)
    {
        Map<String, SecurityUser> map = fetchUsers(pagination);

        List<SecurityUser> list = new ArrayList<SecurityUser>();
        for (SecurityUser user : map.values())
        {
            list.add(user);
        }

        return list;
    }

    @Override
    public SecurityUser readUser(String userId)
    {
        SecurityUser user = null;

        try
        {
            Response response = getRemote().get("/security/users/" + userId);
            user = getFactory().securityUser(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return user;
    }

    @Override
    public SecurityUser createUser(String userId, String password)
    {
        ObjectNode object = JsonUtil.createObject();
        object.put(SecurityUser.FIELD_PRINCIPAL_ID, userId);

        SecurityUser user = getFactory().securityUser(this, object);
        user.setPassword(password);

        return createUser(user.getObject());
    }

    @Override
    public SecurityUser createUser(ObjectNode object)
    {
        if (object == null)
        {
            throw new RuntimeException("Object required");
        }

        // ensure object has principal id and password
        if (!object.has(SecurityUser.FIELD_PRINCIPAL_ID))
        {
            throw new RuntimeException("Missing principal id");
        }
        if (!object.has(SecurityUser.FIELD_MD5_PASSWORD))
        {
            throw new RuntimeException("Missing MD5 password");
        }

        object.put(SecurityUser.FIELD_PRINCIPAL_TYPE, PrincipalType.USER.toString());

        Response response = getRemote().post("/security/users", object);

        // NOTE: "_doc" doesn't come back for the user...
        //String userId = response.getId();
        String userId = object.get(SecurityUser.FIELD_PRINCIPAL_ID).getTextValue();
        return readUser(userId);
    }

    @Override
    public void updateUser(SecurityUser user)
    {
        getRemote().put("/security/users/" + user.getId(), user.getObject());
    }

    @Override
    public void deleteUser(SecurityUser user)
    {
        deleteUser(user.getId());
    }

    @Override
    public void deleteUser(String userId)
    {
        getRemote().delete("/security/users/" + userId);
    }

    @Override
    public ResultMap<SecurityGroup> fetchMemberships(SecurityUser user)
    {
        return fetchMemberships(user.getId());
    }

    @Override
    public ResultMap<SecurityGroup> fetchMemberships(String userId)
    {
        return fetchMemberships(userId, false);
    }

    @Override
    public ResultMap<SecurityGroup> fetchMemberships(SecurityUser user, boolean includeIndirectMemberships)
    {
        return fetchMemberships(user.getId(), includeIndirectMemberships);
    }

    @Override
    public ResultMap<SecurityGroup> fetchMemberships(String userId, boolean includeIndirectMemberships)
    {
        String url = "/security/users/" + userId + "/memberships";
        if (includeIndirectMemberships)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        return getFactory().securityGroups(this, response);
    }

    @Override
    public List<SecurityGroup> listMemberships(SecurityUser user)
    {
        return listMemberships(user.getId());
    }

    @Override
    public List<SecurityGroup> listMemberships(String userId)
    {
        return listMemberships(userId, false);
    }

    @Override
    public List<SecurityGroup> listMemberships(SecurityUser user, boolean includeIndirectMemberships)
    {
        return listMemberships(user.getId(), includeIndirectMemberships);
    }

    @Override
    public List<SecurityGroup> listMemberships(String userId, boolean includeIndirectMemberships)
    {
        Map<String, SecurityGroup> map = fetchMemberships(userId);

        List<SecurityGroup> list = new ArrayList<SecurityGroup>();
        for (SecurityGroup group : map.values())
        {
            list.add(group);
        }

        return list;
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ARCHIVES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Archive> queryArchives(ObjectNode query)
    {
        return queryArchives(query, null);
    }

    @Override
    public ResultMap<Archive> queryArchives(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/archives/query", params, query);
        return getFactory().archives(this, response);
    }

    @Override
    public Archive readArchive(String groupId, String artifactId, String versionId)
    {
        Archive archive = null;

        Map<String, String> params = new HashMap<String, String>();
        params.put("group", groupId);
        params.put("artifact", artifactId);
        params.put("version", versionId);

        try
        {
            Response response = getRemote().get("/archives", params);
            archive = getFactory().archive(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return archive;
    }

    @Override
    public void deleteArchive(String groupId, String artifactId, String versionId)
    {
        try
        {
            Response response = getRemote().delete("/archives?group="+groupId+"&artifact="+artifactId+"&version="+versionId);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }
    }

    @Override
    public void uploadArchive(String groupId, String artifactId, String versionId, InputStream in)
        throws IOException
    {
        String contentType = MimeTypeMap.APPLICATION_ZIP;
        byte[] bytes = StreamUtil.getBytes(in);
        try
        {
            getRemote().upload("/archives/upload?group="+groupId+"&artifact="+artifactId+"&version="+versionId, bytes, contentType);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public byte[] downloadArchive(String groupId, String artifactId, String versionId)
        throws IOException
    {
        byte[] bytes = null;

        try
        {
            bytes = getRemote().downloadBytes("/archives/download?group="+groupId+"&artifact="+artifactId+"&version="+versionId);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }



}
