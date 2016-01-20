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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Teamable
{
    public Team readTeam(String teamKey);

    public ResultMap<Team> listTeams();

    public Team createTeam(String teamKey);

    public Team createTeam(String teamKey, ObjectNode object);

    public void deleteTeam(String teamKey);

    public String getTeamableBaseUri();

    public Team getOwnersTeam();
}