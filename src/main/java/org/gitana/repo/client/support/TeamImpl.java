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
import org.gitana.repo.client.*;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.support.GitanaObjectImpl;
import org.gitana.repo.support.Pagination;
import org.gitana.repo.support.ResultMap;
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
    private Server server;
    private Teamable teamable;

    public TeamImpl(Server server, Teamable teamable, String teamKey, ObjectNode objectNode)
    {
        super(objectNode);

        this.teamKey = teamKey;
        this.server = server;
        this.teamable = teamable;
    }

    protected Remote getRemote()
    {
        return server.getDriver().getRemote();
    }

    protected ObjectFactory getFactory()
    {
        return server.getDriver().getFactory();
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
            String roleKey = (String) array.get(i).getTextValue();

            roleKeys.add(roleKey);
        }

        return roleKeys;
    }

    @Override
    public void addMember(String principalId)
    {
        getRemote().post(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/members/" + principalId + "/add");
    }

    @Override
    public void removeMember(String principalId)
    {
        getRemote().post(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/members/" + principalId + "/remove");
    }

    @Override
    public ResultMap<SecurityPrincipal> listMembers()
    {
        return listMembers(null);
    }

    @Override
    public ResultMap<SecurityPrincipal> listMembers(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get(getTeamable().getTeamableBaseUri() + "/teams/" + this.getKey() + "/members", params);
        return getFactory().securityPrincipals(server, response);
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
