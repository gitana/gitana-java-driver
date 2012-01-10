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

package org.gitana.platform.client.datastore;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.document.DocumentImpl;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.team.Team;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public abstract class AbstractDataStoreImpl extends DocumentImpl implements DataStore
{
    protected AbstractDataStoreImpl(ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);
    }

    protected ObjectFactory getFactory()
    {
        return getDriver().getFactory();
    }

    protected Driver getDriver()
    {
        return DriverContext.getDriver();
    }

    protected Remote getRemote()
    {
        return getDriver().getRemote();
    }

    public abstract Cluster getCluster();

    @Override
    public String getId()
    {
        return getString(DataStore.FIELD_ID);
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof DataStore)
        {
            DataStore other = (DataStore) object;

            equals = (this.getId().equals(other.getId())) && (this.getType().equals(other.getType()));
        }

        return equals;
    }

    public abstract String getResourceUri();


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELF
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put(getResourceUri(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete(getResourceUri());
    }

    @Override
    public abstract void reload();





    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get(getResourceUri() + "/acl/list");

        return DriverUtil.toACL(response);
    }


    @Override
    public List<String> getACL(String principalId)
    {
        Response response = getRemote().get(getResourceUri() + "/acl?id=" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/grant?id=" + principalId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/revoke?id=" + principalId);
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

        Response response = getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/check?id=" + principalId);
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

        Response response = getRemote().post(getResourceUri() + "/authorities", object);
        return getFactory().principalAuthorityGrants(response);
    }

    @Override
    public boolean hasPermission(String principalId, String permissionId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/permissions/" + permissionId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
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
            Response response = getRemote().get(getResourceUri() + "/teams/" + teamKey);
            team = getFactory().team(getCluster(), this, teamKey, response);
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

        Response response = getRemote().get(getResourceUri() + "/teams", params);
        return getFactory().teams(getCluster(), this, response);
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

        getRemote().post(getResourceUri() + "/teams?key=" + teamKey, object);

        return readTeam(teamKey);
    }

    @Override
    public void deleteTeam(String teamKey)
    {
        getRemote().delete(getResourceUri() + "/teams/" + teamKey);
    }

    @Override
    public String getTeamableBaseUri()
    {
        return getResourceUri();
    }

    @Override
    public Team getOwnersTeam()
    {
        return this.readTeam("owners");
    }

}
