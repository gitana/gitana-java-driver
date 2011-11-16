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

package org.gitana.repo.client;

import java.util.List;

/**
 * @author uzi
 */
public interface Organization extends Document, AccessControllable, Selfable, Teamable, Attachable
{
    public final static String FIELD_REPOSITORIES = "repositories";

    public List<String> getAssignedRepositoryIds();

    /**
     * @return the organization owners team
     */
    public Team getOwnersTeam();

    public void assignRepository(String repositoryId);

    public void unassignRepository(String repositoryId);
}
