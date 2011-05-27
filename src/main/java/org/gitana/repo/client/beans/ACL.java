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

package org.gitana.repo.client.beans;

import java.util.*;

/**
 * @author uzi
 */
public class ACL
{
    private Map<String, ACLEntry> map;

    public ACL()
    {
        map = new LinkedHashMap<String, ACLEntry>();
    }

    public void add(String principal, ACLEntry entry)
    {
        map.put(principal, entry);
    }

    public List<ACLEntry> getEntries()
    {
        List<ACLEntry> entries = new ArrayList<ACLEntry>();

        // create a sorted list
        List<String> principals = new ArrayList<String>(map.keySet());
        Collections.sort(principals);
        for (String principal: principals)
        {
            ACLEntry entry = map.get(principal);
            entries.add(entry);
        }

        return entries;
    }
}