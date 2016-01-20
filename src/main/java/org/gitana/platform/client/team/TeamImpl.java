/**
 * Copyright 2016 Gitana Software, Inc.
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

package org.gitana.platform.client.team;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.Driver;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.GitanaObjectImpl;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class TeamImpl extends GitanaObjectImpl implements Team
{
    private String teamKey;
    private Teamable teamable;

    private Cluster cluster;

    public TeamImpl(Cluster cluster, Teamable teamable, String teamKey, ObjectNode objectNode)
    {
        super(objectNode);

        this.teamable = teamable;
        this.teamKey = teamKey;

        this.cluster = cluster;
    }

    protected Cluster getCluster()
    {
        return this.cluster;
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

    @Override
    public Teamable getTeamable()
    {
        return this.teamable;
    }

    @Override
    public String getKey()
    {
        return this.teamKey;
    }

    @Override
    public String getGroupId()
    {
        return getString(FIELD_GROUP_ID);
    }

    @Override
    public List<String> getRoleKeys()
    {
        List<String> roleKeys = new ArrayList<String>();

        ArrayNode array = getArray(FIELD_ROLE_KEYS);
        for (int i = 0; i < array.size(); i++)
        {
            String roleKey = (String) array.get(i).textValue();

            roleKeys.add(roleKey);
        }

        return roleKeys;
    }

    @Override
    public void addMember(String principalId)
    {
        getRemote().post(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/members/add?id=" + principalId);
    }

    @Override
    public void removeMember(String principalId)
    {
        getRemote().post(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/members/remove?id=" + principalId);
    }

    @Override
    public boolean hasMember(String principalId)
    {
        boolean found = false;

        ResultMap<DomainPrincipal> members = listMembers();
        for (DomainPrincipal member: members.values())
        {
            if (member.getId().equals(principalId))
            {
                found = true;
                break;
            }
            else if (member.getDomainQualifiedId().equals(principalId))
            {
                found = true;
                break;
            }
            else if (member.getDomainQualifiedName().equals(principalId))
            {
                found = true;
                break;
            }
        }

        return found;
    }

    @Override
    public ResultMap<DomainPrincipal> listMembers()
    {
        return listMembers(null);
    }

    @Override
    public ResultMap<DomainPrincipal> listMembers(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        // TODO - what do we do it the principals in the group are in domains that are NOT part of this platform?
        Platform platform = DriverContext.getDriver().getPlatform();

        Response response = getRemote().get(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/members", params);
        return getFactory().domainPrincipals(platform, response);
    }

    @Override
    public void grant(String authorityId)
    {
        getRemote().post(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/authorities/" + authorityId + "/grant");
    }

    @Override
    public void revoke(String authorityId)
    {
        getRemote().post(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/authorities/" + authorityId + "/revoke");
    }

    @Override
    public List<String> listAuthorities()
    {
        Map<String, String> params = DriverUtil.params();

        Response response = getRemote().get(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/authorities", params);
        ArrayNode arrayNode = (ArrayNode) response.getObjectNode().get("authorities");

        return Arrays.asList(JsonUtil.toStringArray(arrayNode));
    }
}
