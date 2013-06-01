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

package org.gitana.platform.client.document;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.GitanaObject;
import org.gitana.platform.client.support.TitleDescription;

import java.util.Calendar;

/**
 * @author uzi
 */
public interface Document extends GitanaObject, TitleDescription
{	
    // holder: metadata object
    public final static String SYSTEM = "_system";
    
    // fields
    public final static String FIELD_ID = "_doc";
    
    // timestamp properties
    public final static String SYSTEM_MODIFIED_ON = "modified_on";
    public final static String SYSTEM_MODIFIED_BY = "modified_by";
    public final static String SYSTEM_CREATED_ON = "created_on";
    public final static String SYSTEM_CREATED_BY = "created_by";
    
    /**
     * Gets the id of the document.
     * 
     * @return
     */
    public String getId();

    /**
     * Sets the id of the document.
     *
     * @param id
     */
    public void setId(String id);

    /**
     * Reloads the node from a persisted document (which represents the node).
     *
     * @param document
     */
    public void reload(Document document);
    
    /**
     * Reloads the contents of this document with that of the given json
     * 
     * @param object
     */
    public void reload(ObjectNode object);
    
    /**
     * @return whether the document has been persisted
     */
    public boolean isSaved();

    /**
     * Marks the object as having been persisted (written to disk).
     *
     * True indicates the object has been written into the data store.
     * False indicates that the object is in-memory only.
     *
     * @param saved
     */
    public void setSaved(boolean saved);
    
    // READONLY timestamp and user information
    public Calendar dateModified();
    public Calendar dateCreated();
    public String getCreatedBy();
    public String getModifiedBy();
    
    // metadata support
    public ObjectNode getSystemObject();
        
    //public String toJSONString(boolean includeSystem, boolean pretty);
}
