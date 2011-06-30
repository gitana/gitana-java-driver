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

import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Attachable;
import org.gitana.repo.client.Attachment;
import org.gitana.repo.client.Driver;

import java.io.InputStream;

/**
 * @author uzi
 */
public class AttachmentImpl implements Attachment
{
    private Driver driver;
    private Attachable attachable;

    private String id;

    private ObjectNode objectNode;

    public AttachmentImpl(Driver driver, Attachable attachable, ObjectNode objectNode)
    {
        this.driver = driver;
        this.attachable = attachable;

        this.objectNode = objectNode;
    }

    @Override
    public String getId()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_ID).getTextValue();
    }

    @Override
    public String getObjectId()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_OBJECT_ID).getTextValue();
    }

    @Override
    public long getLength()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_LENGTH).getLongValue();
    }

    @Override
    public String getContentType()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_CONTENT_TYPE).getTextValue();
    }
    
    @Override
    public String getFilename()
    {
        return objectNode.get(Attachment.FIELD_ATTACHMENT_FILENAME).getTextValue();
    }

    @Override
    public InputStream getInputStream()
    {
        InputStream in = null;

        try
        {
            GetMethod method = driver.getRemote().download(attachable.getDownloadUri(getId()));
            in = method.getResponseBodyAsStream();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return in;
    }

}
