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
public interface Changeset extends Document
{
    // default collection location
    public final static String DEFAULT_COLLECTION_ID = "changesets";
        
    // change document properties
    public final static String FIELD_REV = "revision";
    public final static String FIELD_TAGS = "tags";
    public final static String FIELD_SUMMARY = "summary";
    public final static String FIELD_ACTIVE = "active";    
    public final static String FIELD_PARENTS = "parents";
    public final static String FIELD_BRANCH = "branch";
    
    public String getId();
    public int getRev();

    public Repository getRepository();
    public String getRepositoryId();
    
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

}
