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
import org.gitana.repo.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class OrganizationImpl extends DocumentImpl implements Organization
{
    private Server server;

    protected OrganizationImpl(Server server, ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);

        this.server = server;
    }

    protected Remote getRemote()
    {
        return server.getDriver().getRemote();
    }

    protected ObjectFactory getFactory()
    {
        return server.getDriver().getFactory();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELF
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void reload()
    {
        Organization organization = server.readOrganization(this.getId());
        this.reload(organization.getObject());
    }

    @Override
    public void update()
    {
        getRemote().put("/organizations/" + getId(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete("/organizations/" + getId());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get("/organizations/" + getId() + "/acl");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/organizations/" + getId() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/organizations/" + getId() + "/acl/" + principalId + "/authorities/" + authorityId + "/grant");
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/organizations/" + getId() + "/acl/" + principalId + "/authorities/" + authorityId + "/revoke");
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

        Response response = getRemote().post("/organizations/" + getId() + "/authorities/" + authorityId + "/check/" + principalId);
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

        Response response = getRemote().post("/organizations/" + getId() + "/authorities", object);
        return getFactory().principalAuthorityGrants(response);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TEAMS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Team readTeam(String teamKey)
    {
        Team team = null;

        try
        {
            Response response = getRemote().get("/organizations/" + getId() + "/teams/" + teamKey);
            team = getFactory().team(server, this, teamKey, response);
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

        Response response = getRemote().get("/organizations/" + getId() + "/teams", params);
        return getFactory().teams(server, this, response);
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

        getRemote().post("/organizations/" + getId() + "/teams?key=" + teamKey, object);

        return readTeam(teamKey);
    }

    @Override
    public void deleteTeam(String teamKey)
    {
        getRemote().delete("/organizations/" + getId() + "/teams/" + teamKey);
    }

    @Override
    public String getTeamableBaseUri()
    {
        return "/organizations/" + getId();
    }

    @Override
    public List<String> getAssignedRepositoryIds()
    {
        List<String> list = new ArrayList<String>();

        ArrayNode array = getArray(FIELD_REPOSITORIES);
        if (array != null)
        {
            for (int i = 0; i < array.size(); i++)
            {
                list.add(array.get(i).getTextValue());
            }
        }

        return list;
    }

    @Override
    public Team getOwnersTeam()
    {
        return this.readTeam("owners");
    }

    @Override
    public void assignRepository(String repositoryId)
    {
        getRemote().post("/organizations/" + getId() + "/repositories/" + repositoryId + "/assign");
    }

    @Override
    public void unassignRepository(String repositoryId)
    {
        getRemote().post("/organizations/" + getId() + "/repositories/" + repositoryId + "/unassign");
    }

}
