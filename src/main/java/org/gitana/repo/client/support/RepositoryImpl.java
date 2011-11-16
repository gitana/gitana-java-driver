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

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.authority.AuthorityGrant;
import org.gitana.repo.client.*;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.RepositoryType;
import org.gitana.repo.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class RepositoryImpl extends DocumentImpl implements Repository
{
    private Driver driver;
    private Server server;

    protected RepositoryImpl(Driver driver, Server server, ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);

        this.driver = driver;
        this.server = server;
    }

    protected Remote getRemote()
    {
        return driver.getRemote();
    }

    protected ObjectFactory getFactory()
    {
        return driver.getFactory();
    }

    @Override
    public Server getServer()
    {
        return this.server;
    }

    @Override
    public RepositoryType getType()
    {
        return RepositoryType.valueOf(getString(Repository.FIELD_REPOSITORY_TYPE));
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Repository)
        {
            Repository other = (Repository) object;

            equals = (this.getId().equals(other.getId())) && (this.getType().equals(other.getType()));
        }

        return equals;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELF
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put("/repositories/" + getId(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete("/repositories/" + getId());
    }

    @Override
    public void reload()
    {
        Repository repository = getServer().readRepository(getId());
        this.reload(repository.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get("/repositories/" + getId() + "/acl");

        return DriverUtil.toACL(response);
    }


    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/repositories/" + getId() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getId() + "/acl/" + principalId + "/authorities/" + authorityId + "/grant");
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getId() + "/acl/" + principalId + "/authorities/" + authorityId + "/revoke");
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post("/repositories/" + getId() + "/authorities/" + authorityId + "/check/" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
    }

    @Override
    public Map<String, Map<String, AuthorityGrant>> getAuthorityGrants(List<String> principalIds)
    {
        ObjectNode object = JsonUtil.createObject();
        JsonUtil.objectPut(object, "principals", principalIds);

        Response response = getRemote().post("/repositories/" + getId() + "/authorities", object);
        return getFactory().principalAuthorityGrants(response);
    }

    @Override
    public boolean hasPermission(String principalId, String permissionId)
    {
        boolean has = false;

        Response response = getRemote().post("/repositories/" + getId() + "/permissions/" + permissionId + "/check/" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BRANCHES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Branch> fetchBranches()
    {
        return fetchBranches(null);
    }

    @Override
    public ResultMap<Branch> fetchBranches(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/repositories/" + getId() + "/branches", params);
        return getFactory().branches(this, response);
    }

    @Override
    public List<Branch> listBranches()
    {
        return listBranches(null);
    }

    @Override
    public List<Branch> listBranches(Pagination pagination)
    {
        Map<String, Branch> map = fetchBranches(pagination);

        List<Branch> list = new ArrayList<Branch>();
        for (Branch branch : map.values())
        {
            list.add(branch);
        }

        return list;
    }

    @Override
    public Branch readBranch(String branchId)
    {
        Branch branch = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getId() + "/branches/" + branchId);
            branch = getFactory().branch(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return branch;
    }

    @Override
    public Branch createBranch(String changesetId)
    {
        return createBranch(changesetId, JsonUtil.createObject());
    }

    @Override
    public Branch createBranch(String changesetId, ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/repositories/" + getId() + "/branches?changeset=" + changesetId, object);

        String branchId = response.getId();
        return readBranch(branchId);
    }

    @Override
    public ResultMap<Branch> queryBranches(ObjectNode query)
    {
        return queryBranches(query, null);
    }

    @Override
    public ResultMap<Branch> queryBranches(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/repositories/" + getId() + "/branches/query", params, query);
        return getFactory().branches(this, response);
    }

    @Override
    public PermissionCheckResults checkBranchPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post("/repositories/" + getId() + "/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // FILES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void uploadFile(String filename, byte[] bytes, String contentType)
    {
        try
        {
            getRemote().upload("/repositories/" + this.getId() + "/files/" + filename, bytes, contentType);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteFile(String filename)
    {
        getRemote().delete("/repositories/" + this.getId() + "/files/" + filename);
    }

    @Override
    public byte[] downloadFile(String filename)
    {
        byte[] bytes = null;

        try
        {
            bytes = getRemote().downloadBytes("/repositories/" + this.getId() + "/files/" + filename);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }

    @Override
    public ResultMap<Changeset> fetchChangesets()
    {
        return fetchChangesets(null);
    }

    @Override
    public ResultMap<Changeset> fetchChangesets(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/repositories/" + getId() + "/changesets", params);
        return getFactory().changesets(this, response);
    }

    @Override
    public List<Changeset> listChangesets()
    {
        return listChangesets(null);
    }

    @Override
    public List<Changeset> listChangesets(Pagination pagination)
    {
        Map<String, Changeset> map = fetchChangesets(pagination);

        List<Changeset> list = new ArrayList<Changeset>();
        for (Changeset changeset : map.values())
        {
            list.add(changeset);
        }

        return list;
    }

    @Override
    public Changeset readChangeset(String changesetId)
    {
        Changeset changeset = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getId() + "/changesets/" + changesetId);
            changeset = getFactory().changeset(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return changeset;
    }

    @Override
    public ResultMap<Changeset> queryChangesets(ObjectNode query)
    {
        return queryChangesets(query, null);
    }

    @Override
    public ResultMap<Changeset> queryChangesets(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/repositories/" + getId() + "/changesets/query", params, query);
        return getFactory().changesets(this, response);
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

        Response response = getRemote().get("/repositories/" + getId() + "/logs", params);
        return getFactory().logEntries(getServer(), response);
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

        Response response = getRemote().post("/repositories/" + getId() + "/logs/query", params, query);
        return getFactory().logEntries(getServer(), response);
    }

    @Override
    public LogEntry readLogEntry(String logEntryId)
    {
        LogEntry logEntry = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getId() + "/logs/" + logEntryId);
            logEntry = getFactory().logEntry(getServer(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return logEntry;
    }

    @Override
    public Team readTeam(String teamKey)
    {
        Team team = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getId() + "/teams/" + teamKey);
            team = getFactory().team(getServer(), this, teamKey, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return team;
    }

    @Override
    public ResultMap<Team> listTeams()
    {
        Map<String, String> params = DriverUtil.params();

        Response response = getRemote().get("/repositories/" + getId() + "/teams", params);
        return getFactory().teams(getServer(), this, response);
    }

    @Override
    public Team createTeam(String teamKey)
    {
        return createTeam(teamKey, null);
    }

    @Override
    public Team createTeam(String teamKey, ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        getRemote().post("/repositories/" + getId() + "/teams?key=" + teamKey, object);

        return readTeam(teamKey);
    }

    @Override
    public void deleteTeam(String teamKey)
    {
        getRemote().delete("/repositories/" + getId() + "/teams/" + teamKey);
    }

    @Override
    public String getTeamableBaseUri()
    {
        return "/repositories/" + getId();
    }
}
