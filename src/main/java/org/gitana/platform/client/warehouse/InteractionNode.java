/**
 * Copyright 2013 Gitana Software, Inc.
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

package org.gitana.platform.client.warehouse;

import org.gitana.platform.client.support.Selfable;

import java.util.List;

/**
 * @author uzi
 */
public interface InteractionNode extends WarehouseDocument, Selfable
{
    public final static String FIELD_REPOSITORY_ID = "repositoryId";
    public final static String FIELD_BRANCH_ID = "branchId";
    public final static String FIELD_NODE_ID = "nodeId";

    public final static String FIELD_TAGS = "tags";
    public final static String FIELD_TAG_TITLE = "title";

    public String getRepositoryId();
    public void setRepositoryId(String repositoryId);

    public String getBranchId();
    public void setBranchId(String branchId);

    public String getNodeId();
    public void setNodeId(String nodeId);

    public List<String> getTags();
    public String getTagTitle(String tag);

    public void addTag(String tag, String title);

}
