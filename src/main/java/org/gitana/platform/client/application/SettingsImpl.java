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

package org.gitana.platform.client.application;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.*;
import org.gitana.util.JsonUtil;

/**
 * @author uzi
 */
public class SettingsImpl extends AbstractApplicationDocumentImpl implements Settings
{
    public SettingsImpl(Application application, ObjectNode obj, boolean isSaved)
    {
        super(application, obj, isSaved);
    }

    public String getResourceUri()
    {
        return "/applications/" + getApplicationId() + "/settings/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Settings)
        {
            Settings other = (Settings) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    public ObjectNode getSettings()
    {
        ObjectNode settings = this.getObject(Settings.ROOT_KEY);
        if (settings == null)
        {
            settings = this.getObject().putObject(Settings.ROOT_KEY);
        }
        return settings;
    }

    public Object getSetting(String settingKey)
    {
        return this.getSettings().get(settingKey);
    }

	public ObjectNode getSettingAsObject(String settingKey)
	{
		Object o = this.getSetting(settingKey);
		ObjectNode obj = null;
        if (o != null && o instanceof ObjectNode)
		{
			obj = (ObjectNode) o;
		}

		return obj;
	}

	public boolean getSettingAsBoolean(String settingKey)
	{
		boolean b = false;

		Object value = this.getSetting(settingKey);
		if (value != null && value instanceof BooleanNode)
		{
			b = ((BooleanNode)value).getBooleanValue();
		}

		return b;
	}

	public String getSettingAsString(String settingKey)
	{
		String s = null;

		Object value = this.getSetting(settingKey);
		if (value != null && value instanceof TextNode)
		{
			s = ((TextNode)value).getTextValue();
		}

		return this.getSettings().get(settingKey).getTextValue();
	}

	public ArrayNode getSettingAsArray(String settingKey)
	{
		ArrayNode array = null;

		Object o = this.getSetting(settingKey);
		if (o != null && o instanceof ArrayNode)
		{
			array = (ArrayNode) o;
		}

		return array;
	}

	public int getSettingAsInt(String settingKey)
	{
		Integer integer = null;

		Object value = this.getSetting(settingKey);
		if (value != null && value instanceof IntNode)
		{
			integer = ((IntNode)value).getIntValue();
		}
        else if (value != null && value instanceof DoubleNode)
        {
            integer = ((DoubleNode) value).getIntValue();
        }
        else
        {
            integer = -1;
        }

		return integer;
	}

    public long getSettingAsLong(String settingKey)
    {
        Long l = new Long(-1);

        Object value = this.getSetting(settingKey);
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

    public double getSettingAsDouble(String settingKey)
    {
        Double d = new Double(-1);

        Object value = this.getSetting(settingKey);
        if (value != null)
        {
            if (value instanceof DoubleNode)
            {
                d = ((DoubleNode) value).getDoubleValue();
            }
        }

        return d;
    }

    public void setSetting(String settingKey, Object settingVal)
    {
        ObjectNode settings = this.getSettings();
        JsonUtil.objectPut(settings, settingKey,settingVal);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELFABLE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put(getResourceUri(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete(getResourceUri());
    }

    @Override
    public void reload()
    {
        Settings settings = getApplication().readSettings(getId());

        this.reload(settings.getObject());
    }

}
