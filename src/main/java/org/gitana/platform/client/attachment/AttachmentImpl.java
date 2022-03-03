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
package org.gitana.platform.client.attachment;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.support.GitanaObjectImpl;

import java.io.InputStream;

/**
 * @author uzi
 */
public class AttachmentImpl extends GitanaObjectImpl implements Attachment
{
    private Attachable attachable;

    public AttachmentImpl(Attachable attachable, ObjectNode objectNode)
    {
        super(objectNode);

        this.attachable = attachable;
    }

    protected ObjectFactory getFactory()
    {
        return getDriver().getFactory();
    }

    protected Driver getDriver()
    {
        return DriverContext.getDriver();
    }

    protected Remote getRemote()
    {
        return getDriver().getRemote();
    }

    @Override
    public String getId()
    {
        return getString(Attachment.FIELD_ATTACHMENT_ID);
    }

    @Override
    public String getObjectId()
    {
        return getString(Attachment.FIELD_ATTACHMENT_OBJECT_ID);
    }

    @Override
    public long getLength()
    {
        return getLong(Attachment.FIELD_ATTACHMENT_LENGTH);
    }

    @Override
    public String getContentType()
    {
        return getString(Attachment.FIELD_ATTACHMENT_CONTENT_TYPE);
    }
    
    @Override
    public String getFilename()
    {
        return getString(Attachment.FIELD_ATTACHMENT_FILENAME);
    }

    @Override
    public InputStream getInputStream()
    {
        InputStream in = null;

        try
        {
            HttpResponse httpResponse = getRemote().download(attachable.getDownloadUri(getId()));
            in = httpResponse.getEntity().getContent();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return in;
    }

}
