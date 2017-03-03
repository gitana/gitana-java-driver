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

package org.gitana.platform.client.stack;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.attachment.Attachable;
import org.gitana.platform.client.datastore.DataStore;
import org.gitana.platform.client.log.LogEntry;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.client.platform.PlatformDocument;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.client.team.Teamable;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Stack extends PlatformDocument, AccessControllable, Selfable, Teamable, Attachable
{
    public final static String FIELD_KEY = "key";

    public String getKey();
    public void setKey(String key);


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // LOGS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<LogEntry> listLogEntries();
    public ResultMap<LogEntry> listLogEntries(Pagination pagination);
    public ResultMap<LogEntry> queryLogEntries(ObjectNode query);
    public ResultMap<LogEntry> queryLogEntries(ObjectNode query, Pagination pagination);
    public LogEntry readLogEntry(String logEntryId);

    public void assignDataStore(PlatformDataStore datastore);
    public void assignDataStore(PlatformDataStore datastore, String key);
    public void unassignDataStore(String key);

    public ResultMap<PlatformDataStore> listDataStores();
    public ResultMap<PlatformDataStore> listDataStores(Pagination pagination);

    public ResultMap<PlatformDataStore> queryDataStores(ObjectNode query);
    public ResultMap<PlatformDataStore> queryDataStores(ObjectNode query, Pagination pagination);

    public boolean existsDataStore(String key);
    
    public DataStore readDataStore(String key);
}
