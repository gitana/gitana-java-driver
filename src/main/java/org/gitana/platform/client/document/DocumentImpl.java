/**
 * Copyright 2022 Gitana Software, Inc.
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
package org.gitana.platform.client.document;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.support.GitanaObjectImpl;
import org.gitana.util.DateUtil;
import org.gitana.util.JsonUtil;

import java.util.Calendar;

/**
 * Wraps a JSON response from the Gitana server.
 * 
 * @author uzi
 */
public class DocumentImpl extends GitanaObjectImpl implements Document
{
    private boolean isSaved;

	protected DocumentImpl(ObjectNode obj, boolean isSaved)
	{
        super(obj);

		// properties
        this.isSaved = isSaved;
	}

    @Override
    public String getId()
    {
    	return getString(FIELD_ID);
    }

    @Override
    public void setId(String id)
    {
    	set(FIELD_ID, id);
    }

    @Override
    public String getTitle()
    {
        return getString(FIELD_TITLE);
    }

    @Override
    public void setTitle(String title)
    {
        set(FIELD_TITLE, title);
    }

    @Override
    public String getDescription()
    {
        return getString(FIELD_DESCRIPTION);
    }

    @Override
    public void setDescription(String description)
    {
        set(FIELD_DESCRIPTION, description);
    }

    @Override
    public void reload(Document document)
    {
        this.reload(document.getObject());

        // set saved
        this.isSaved = true;
    }

    @Override
	public void reload(ObjectNode source)
	{
        boolean merge = true;
        if (source.has(Document.FIELD_ID))
        {
            if (source.get(Document.FIELD_ID).textValue().equals(getId()))
            {
                merge = false;
            }
        }

        ObjectNode replacement = null;
        if (merge)
        {
            // create a copy of our properties
            ObjectNode original = JsonUtil.copyObject(getObject());

            // create a copy of the incoming object
            ObjectNode incoming = JsonUtil.copyObject(source);

            // merge properties
            replacement = mergeProperties(original, incoming);
        }
        else
        {
            replacement = JsonUtil.copyObject(source);
        }

        // if the replacement doesn't have "_system", copy from original
        if (!replacement.has(Document.SYSTEM))
        {
            mergePropertyIfExists(this.getObject(), replacement, Document.SYSTEM);
        }

        // only hard condition - make sure _id copies over
        if (this.has("_id"))
        {
            replacement.put("_id", this.getString("_id"));
        }

        // clear our own object and push new properties
        this.putObject(replacement);
	}

    protected ObjectNode mergeProperties(ObjectNode original, ObjectNode incoming)
    {
        ObjectNode merged = JsonUtil.createObject();

        // copy everything in from incoming
        merged.putAll(incoming);

        // things we always retain
        //mergePropertyIfExists(original, merged, "_id");
        mergePropertyIfExists(original, merged, Document.SYSTEM);
        mergePropertyIfExists(original, merged, Document.FIELD_ID);

        return merged;
    }

    protected void mergePropertyIfExists(ObjectNode source, ObjectNode target, String propertyName)
    {
        if (source.has(propertyName))
        {
            target.put(propertyName, source.get(propertyName));
        }
    }

    @Override
    public boolean isSaved()
    {
        return isSaved;
    }

    @Override
    public void setSaved(boolean isSaved)
    {
        this.isSaved = isSaved;
    }

    @Override
    public ObjectNode getSystemObject()
    {
        return getObject(SYSTEM);
    }

    @Override
    public Calendar dateModified()
    {
        Calendar calendar = null;

        if (getSystemObject() != null && getSystemObject().has(SYSTEM_MODIFIED_ON))
        {
            ObjectNode timestamp = JsonUtil.objectGetObject(getSystemObject(), SYSTEM_MODIFIED_ON);

            long ms = JsonUtil.objectGetLong(timestamp, "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public Calendar dateCreated()
    {
        Calendar calendar = null;

        if (getSystemObject() != null && getSystemObject().has(SYSTEM_CREATED_ON))
        {
            ObjectNode timestamp = JsonUtil.objectGetObject(getSystemObject(), SYSTEM_CREATED_ON);

            long ms = JsonUtil.objectGetLong(timestamp, "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public String getCreatedBy()
    {
        return getSystemObject().get(SYSTEM_CREATED_BY).textValue();
    }

    @Override
    public String getModifiedBy()
    {
        return getSystemObject().get(SYSTEM_MODIFIED_BY).textValue();
    }

}
