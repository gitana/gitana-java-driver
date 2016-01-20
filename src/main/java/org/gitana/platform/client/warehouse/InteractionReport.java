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

package org.gitana.platform.client.warehouse;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface InteractionReport extends WarehouseDocument, Selfable
{
    // key fields
    public final static String FIELD_OBJECT_TYPE_ID = "objectTypeId";
    public final static String FIELD_OBJECT_ID = "objectId";
    public final static String FIELD_KEY = "key";

    public String getObjectTypeId();
    public void setObjectTypeId(String objectTypeId);

    public String getObjectId();
    public void setObjectId(String objectId);

    public String getKey();
    public void setKey(String key);




    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // INTERACTION REPORT ENTRY
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<InteractionReportEntry> listInteractionReportEntries();
    public ResultMap<InteractionReportEntry> listInteractionReportEntries(Pagination pagination);

    public InteractionReportEntry readInteractionReportEntry(String interactionReportEntryId);

    public InteractionReportEntry createInteractionReportEntry(ObjectNode object);

    public ResultMap<InteractionReportEntry> queryInteractionReportEntries(ObjectNode query);
    public ResultMap<InteractionReportEntry> queryInteractionReportEntries(ObjectNode query, Pagination pagination);

    public void updateInteractionReportEntry(InteractionReportEntry interactionReportEntry);

    public void deleteInteractionReportEntry(InteractionReportEntry interactionReportEntry);
    public void deleteInteractionReportEntry(String interactionReportEntryId);

}
