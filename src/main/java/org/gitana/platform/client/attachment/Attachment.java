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
package org.gitana.platform.client.attachment;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Describes an attachment to an object (document, node or otherwise)
 * 
 * @author uzi
 */
public interface Attachment extends Serializable
{
    // fields
    public final static String FIELD_ATTACHMENT_LENGTH = "length";
    public final static String FIELD_ATTACHMENT_CONTENT_TYPE = "contentType";
    public final static String FIELD_ATTACHMENT_FILENAME = "filename";
    public final static String FIELD_ATTACHMENT_OBJECT_ID = "objectId";
    public final static String FIELD_ATTACHMENT_ID = "attachmentId";

    // management
    public String getId();
    public String getObjectId();


    // binary things
    public long getLength();
    public String getFilename();
    public String getContentType();
    public InputStream getInputStream();
}
