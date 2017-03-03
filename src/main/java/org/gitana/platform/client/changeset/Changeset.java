/**
 * Copyright 2017 Gitana Software, Inc.
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
 *   info@cloudcms.com
 */

package org.gitana.platform.client.changeset;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.repository.RepositoryDocument;
import org.gitana.platform.support.ResultMap;

import java.util.List;

/**
 * @author uzi
 */
public interface Changeset extends RepositoryDocument
{
    // change document properties
    public final static String FIELD_REV = "revision";
    public final static String FIELD_TAGS = "tags";
    public final static String FIELD_SUMMARY = "summary";
    public final static String FIELD_ACTIVE = "active";    
    public final static String FIELD_PARENTS = "parents";
    public final static String FIELD_BRANCH = "branch";

    public String getId();
    public int getRev();

    public Branch getBranch();
    public String getBranchId();

    public String getSummary();    
    public void setSummary(String summary);

    public boolean isActive();
    public void setActive(boolean active);
    
    public List<String> getParentChangesetIds();    
    public void setParentChangesetIds(List<String> parentChangesetIds);
    
    public String[] getTags();
    public boolean hasTag(String tag);    
    public void addTag(String tag);    
    public void removeTag(String tag);

    public ResultMap<BaseNode> listNodes();

}
