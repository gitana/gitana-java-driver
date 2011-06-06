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

package org.gitana.repo.client.support;

import org.codehaus.jackson.node.*;
import org.gitana.repo.client.Document;
import org.gitana.util.DateUtil;
import org.gitana.util.JsonUtil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

/**
 * Wraps a JSON response from the Gitana server.
 * 
 * @author uzi
 */
public class DocumentImpl implements Document
{
    private transient ObjectNode object;

    private boolean isSaved;

	protected DocumentImpl(ObjectNode obj, boolean isSaved)
	{
		if (obj == null)
		{
			obj = JsonUtil.createObject();
		}
        else
        {
            // make a copy so that we don't end up modifying the original
            obj = JsonUtil.copyObject(obj);
        }

		// properties
		this.object = obj;
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
    public ObjectNode getObject()
    {
    	return this.object;
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
            if (source.get(Document.FIELD_ID).getTextValue().equals(getId()))
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
        this.object.removeAll();
        this.object.putAll(replacement);
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
        String timestamp = getSystemObject().get(SYSTEM_MODIFIED_ON).getTextValue();
        return DateUtil.convertTimestamp(timestamp);
    }

    @Override
    public Calendar dateCreated()
    {
        String timestamp = getSystemObject().get(SYSTEM_CREATED_ON).getTextValue();
        return DateUtil.convertTimestamp(timestamp);
    }

    @Override
    public String getCreatedBy()
    {
        return getSystemObject().get(SYSTEM_CREATED_BY).getTextValue();
    }

    @Override
    public String getModifiedBy()
    {
        return getSystemObject().get(SYSTEM_MODIFIED_BY).getTextValue();
    }

    @Override
    public ObjectNode toJSON()
    {
        return JsonUtil.copyObject(getObject());
    }

    @Override
    public String toJSONString(boolean pretty)
    {
    	return toJSONString(true, pretty);
    }

    @Override
    public String toJSONString(boolean includeSystem, boolean pretty)
    {
    	// create a copy
    	ObjectNode candidate = JsonUtil.copyObject(this.object);

    	if (!includeSystem && candidate.has(SYSTEM))
    	{
    		candidate.remove(SYSTEM);
    	}

        return JsonUtil.stringify(candidate, pretty);
    }

	@Override
	public Object get(String fieldId)
	{
		Object value = this.object.get(fieldId);
		if (value instanceof NullNode)
		{
			value = null;
		}

		return value;
	}

	@Override
	public ObjectNode getObject(String fieldId)
	{
		ObjectNode subObject = null;

		Object o = get(fieldId);
		if (o instanceof ObjectNode)
		{
			subObject = (ObjectNode) o;
		}

		return subObject;
	}

	@Override
	public ArrayNode getArray(String fieldId)
	{
		ArrayNode array = null;

		Object o = get(fieldId);
		if (o instanceof ArrayNode)
		{
			array = (ArrayNode) o;
		}

		return array;
	}

	@Override
	public boolean has(String fieldId)
	{
		boolean has = false;

		Object value = get(fieldId);
		if (value != null && !(value instanceof NullNode))
		{
			has = true;
		}

		return has;
	}

	@Override
	public void remove(String fieldId)
	{
		this.object.remove(fieldId);
	}

	@Override
	public void set(String fieldId, Object value)
	{
		JsonUtil.objectPut(this.object, fieldId, value);
	}

	@Override
	public boolean getBoolean(String fieldId)
	{
		boolean b = false;

		Object value = get(fieldId);
		if (value != null && value instanceof BooleanNode)
		{
			b = ((BooleanNode)value).getBooleanValue();
		}

		return b;
	}

	@Override
	public String getString(String fieldId)
	{
		String text = null;

		Object value = get(fieldId);
		if (value != null && value instanceof TextNode)
		{
			text = ((TextNode)value).getTextValue();
		}

		return text;
	}

	@Override
	public int getInt(String fieldId)
	{
		Integer integer = null;

		Object value = get(fieldId);
		if (value != null && value instanceof IntNode)
		{
			integer = ((IntNode)value).getIntValue();
		}
        else
        {
            integer = -1;
        }

		return integer;
	}

    @Override
    public long getLong(String fieldId)
    {
        Long l = new Long(-1);

        Object value = get(fieldId);
        if (value != null)
        {
            if (value instanceof LongNode)
            {
                l = ((LongNode)value).getLongValue();
            }
            else if (value instanceof IntNode)
            {
                l = ((IntNode)value).getLongValue();
            }
        }

        return l;
    }

    @Override
    public String toString()
    {
        return JsonUtil.stringify(getObject(), true);
    }

    /**
     * Custom serializer
     *
     * @param oos
     * @throws java.io.IOException
     */
    private void writeObject(ObjectOutputStream oos) throws IOException
    {
        // write default object
        oos.defaultWriteObject();

        // write the object node
        String objectText = JsonUtil.stringify(getObject(), false);
        oos.writeBytes(objectText);
    }

    /**
     * Custom deserializer
     *
     * @param ois
     * @throws Exception
     */
    private void readObject(ObjectInputStream ois) throws Exception
    {
        // read default object
        ois.defaultReadObject();

        // read object node
        byte[] array = new byte[ois.available()];
        ois.readFully(array);

        this.object = JsonUtil.createObject(new String(array));
    }

}
