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
import org.gitana.http.HttpPayload;
import org.gitana.repo.authority.AuthorityGrant;
import org.gitana.repo.client.*;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.support.ResultMap;
import org.gitana.security.PrincipalType;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType, String fileName)
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
            getRemote().upload(uri, bytes, contentType, fileName);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void uploadAttachments(HttpPayload... payloads)
    {
        Map<String, String> params = new HashMap<String, String>();

        uploadAttachments(params, payloads);
    }

    @Override
    public void uploadAttachments(Map<String, String> params, HttpPayload... payloads)
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
        uri += "/" + this.getId() + "/attachment";

        try
        {
            getRemote().upload(uri, params, payloads);
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

    @Override
    public List<Attachment> listAttachments()
    {
        Map<String, Attachment> map = fetchAttachments();

        List<Attachment> list = new ArrayList<Attachment>();
        for (Attachment attachment : map.values())
        {
            list.add(attachment);
        }

        return list;
    }

    @Override
    public ResultMap<Attachment> fetchAttachments()
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
        uri += "/" + this.getId() + "/attachment";

        Response response = getRemote().get(uri);

        return getFactory().attachments(this, response);
    }

    @Override
    public String getDownloadUri(String attachmentId)
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

        return uri;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get("/security/principals/" + getName() + "/acl");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/security/principals/" + getName() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/security/principals/" + getName() + "/acl/" + principalId + "/grant/" + authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/security/principals/" + getName() + "/acl/" + principalId + "/revoke/" + authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post("/security/principals/" + getName() + "/acl/" + principalId + "/check/" + authorityId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
    }

    @Override
    public Map<String, Map<String, AuthorityGrant>> getAuthorityGrants(List<String> principalIds)
    {
        ObjectNode object = JsonUtil.createObject();
        JsonUtil.objectPut(object, "principals", principalIds);

        Response response = getRemote().post("/security/principals/" + getName() + "/authorities", object);
        return getFactory().principalAuthorityGrants(response);
    }

}
