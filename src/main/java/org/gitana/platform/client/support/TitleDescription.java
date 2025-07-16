/**
 * Copyright 2025 Gitana Software, Inc.
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
package org.gitana.platform.client.support;

/**
 * @author uzi
 */
public interface TitleDescription
{
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";

    /**
     * @return the title
     */
    public String getTitle();

    /**
     * Sets the title
     *
     * @param title
     */
    public void setTitle(String title);

    /**
     * @return the description
     */
    public String getDescription();

    /**
     * Sets the description
     *
     * @param description
     */
    public void setDescription(String description);
}