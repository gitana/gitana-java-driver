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

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Driver;
import org.gitana.repo.client.ObjectFactory;
import org.gitana.repo.client.SecurityPrincipal;
import org.gitana.repo.client.Server;
import org.gitana.security.PrincipalType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public abstract class AbstractSecurityPrincipalImpl extends DocumentImpl implements SecurityPrincipal
{
    private Driver driver;
    private Server server;

    public AbstractSecurityPrincipalImpl(Driver driver, Server server, ObjectNode obj, boolean isSaved)
    {
    	super(obj, isSaved);

        this.driver = driver;
        this.server = server;
    }

    protected Remote getRemote()
    {
        return driver.getRemote();
    }

    protected ObjectFactory getFactory()
    {
        return driver.getFactory();
    }

    protected Server getServer()
    {
        return server;
    }

    @Override
    public String getName()
    {
        return getString(FIELD_PRINCIPAL_ID);
    }

    @Override
    public PrincipalType getPrincipalType()
    {
        return PrincipalType.valueOf(getString(FIELD_PRINCIPAL_TYPE));
    }

    @Override
    public void setPrincipalType(PrincipalType principalType)
    {
        set(FIELD_PRINCIPAL_TYPE, principalType.toString());
    }

    @Override
    public List<String> getAuthorities()
    {
        List<String> authorities = new ArrayList<String>();

        ArrayNode array = this.getArray(FIELD_AUTHORITIES);
        if (array != null)
        {
            for (int i = 0; i < array.size(); i++)
            {
                String authorityId = array.get(i).getTextValue();
                authorities.add(authorityId);
            }
        }

        return authorities;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ATTACHMENTS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void uploadAttachment(byte[] bytes, String contentType)
    {
        uploadAttachment("default", bytes, contentType);
    }

    @Override
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType)
    {
        // build the uri
        String uri = "/security";
        if (this.getPrincipalType().equals(PrincipalType.USER))
        {
            uri += "/users";
        }
        else if (this.getPrincipalType().equals(PrincipalType.GROUP))
        {
            uri += "/groups";
        }
        uri += "/" + this.getId() + "/attachment/" + attachmentId;

        try
        {
            getRemote().upload(uri, bytes, contentType);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public byte[] downloadAttachment()
    {
        return downloadAttachment("default");
    }

    @Override
    public byte[] downloadAttachment(String attachmentId)
    {
        // build the uri
        String uri = "/security";
        if (this.getPrincipalType().equals(PrincipalType.USER))
        {
            uri += "/users";
        }
        else if (this.getPrincipalType().equals(PrincipalType.GROUP))
        {
            uri += "/groups";
        }
        uri += "/" + this.getId() + "/attachment/" + attachmentId;

        byte[] bytes = null;
        try
        {
            bytes = getRemote().downloadBytes(uri);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }

}
