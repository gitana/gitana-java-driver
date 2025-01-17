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
 *   info@gitana.io
 */
package org.gitana.platform.client.application;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.support.Selfable;

/**
 * @author uzi
 */
public interface Settings extends ApplicationDocument, Selfable {
    public static String ROOT_KEY = "settings";

    public ObjectNode getSettings();

    public Object getSetting(String settingKey);

    public void setSetting(String settingKey, Object settingVal);

    public ObjectNode getSettingAsObject(String settingKey);

    public String getSettingAsString(String settingKey);

    public boolean getSettingAsBoolean(String settingKey);

    public ArrayNode getSettingAsArray(String settingKey);

    public double getSettingAsDouble(String settingKey);

    public long getSettingAsLong(String settingKey);

    public int getSettingAsInt(String settingKey);
}
