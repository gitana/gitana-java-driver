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

package org.gitana.platform.client.principal;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.http.HttpPayload;
import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.domain.AbstractDomainDocumentImpl;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.services.principals.PrincipalType;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public abstract class AbstractDomainPrincipalImpl extends AbstractDomainDocumentImpl implements DomainPrincipal
{
    public AbstractDomainPrincipalImpl(Domain domain, ObjectNode obj, boolean isSaved)
    {
    	super(domain, obj, isSaved);
    }

    @Override
    public String getResourceUri()
    {
        return "/domains/" + this.getDomainId() + "/principals/" + getId();
    }

    @Override
    public String getDomainQualifiedName()
    {
        return getDomainId() + "/" + getName();
    }

    @Override
    public String getDomainQualifiedId()
    {
        return getDomainId() + "/" + getId();
    }

    @Override
    public String getName()
    {
        return getString(FIELD_NAME);
    }

    @Override
    public void setName(String name)
    {
        set(FIELD_NAME, name);
    }

    @Override
    public PrincipalType getType()
    {
        return PrincipalType.valueOf(getString(FIELD_TYPE));
    }

    @Override
    public void setType(PrincipalType type)
    {
        set(FIELD_TYPE, type.toString());
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



    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // UPDATE AND DELETE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

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
        DomainPrincipal principal = getDomain().readPrincipal(getId());

        this.reload(principal.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // MEMBERSHIPS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<DomainGroup> listParentGroups()
    {
        return listParentGroups(false);
    }

    @Override
    public ResultMap<DomainGroup> listParentGroups(boolean includeAncestors)
    {
        String url = getResourceUri() + "/memberships";
        if (includeAncestors)
        {
            url += "?indirect=true";
        }

        Response response = getRemote().get(url);

        ResultMap<DomainGroup> groups = new ResultMapImpl<DomainGroup>();

        ResultMap<DomainPrincipal> principals = getFactory().domainPrincipals(getPlatform(), response);
        for (DomainPrincipal principal: principals.values())
        {
            DomainGroup group = (DomainGroup) principal;

            groups.put(group.getId(), group);
        }

        return groups;
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
        String uri = getResourceUri() + "/attachments/" + attachmentId;
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
        String uri = getResourceUri() + "/attachments/" + attachmentId;
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
        String uri = getResourceUri() + "/attachments/";
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
        byte[] bytes = null;

        String uri = getResourceUri() + "/attachments/" + attachmentId;
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
    public ResultMap<Attachment> listAttachments()
    {
        String uri = getResourceUri() + "/attachments";

        Response response = getRemote().get(uri);

        return getFactory().attachments(this, response);
    }

    @Override
    public String getDownloadUri(String attachmentId)
    {
        return getResourceUri() + "/attachments/" + attachmentId;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get(getResourceUri() + "/acl/list");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getACL(String principalId)
    {
        Response response = getRemote().get(getResourceUri() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/grant?id=" + principalId);
    }

    @Override
    public void grant(DomainPrincipal principal, String authorityId)
    {
        grant(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/revoke?id=" + principalId);
    }

    @Override
    public void revoke(DomainPrincipal principal, String authorityId)
    {
        revoke(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public void revokeAll(DomainPrincipal principal)
    {
        revoke(principal.getDomainQualifiedId(), "all");
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
    }

    @Override
    public boolean hasAuthority(DomainPrincipal principal, String authorityId)
    {
        return hasAuthority(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public Map<String, Map<String, AuthorityGrant>> getAuthorityGrants(List<String> principalIds)
    {
        ObjectNode object = JsonUtil.createObject();
        JsonUtil.objectPut(object, "principals", principalIds);

        Response response = getRemote().post(getResourceUri() + "/authorities", object);
        return getFactory().principalAuthorityGrants(response);
    }

    @Override
    public boolean hasPermission(String principalId, String permissionId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/permissions/" + permissionId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").getBooleanValue();
        }

        return has;
    }

    @Override
    public boolean hasPermission(DomainPrincipal principal, String permissionId)
    {
        return hasPermission(principal.getDomainQualifiedId(), permissionId);
    }

}
