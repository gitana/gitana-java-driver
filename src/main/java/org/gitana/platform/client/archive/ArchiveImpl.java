/**
 * Copyright 2013 Gitana Software, Inc.
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

package org.gitana.platform.client.archive;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.http.HttpPayload;
import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.client.vault.AbstractVaultDocumentImpl;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class ArchiveImpl extends AbstractVaultDocumentImpl implements Archive
{
    public ArchiveImpl(Vault vault, ObjectNode obj, boolean isSaved)
    {
        super(vault, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_ARCHIVE;
    }

    @Override
    public String getResourceUri()
    {
        return "/vaults/" + getVaultId() + "/archives/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Archive)
        {
            Archive other = (Archive) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    @Override
    public InputStream download()
        throws IOException
    {
        InputStream in = null;

        HttpResponse response = null;
        try
        {
            response = getRemote().download(getResourceUri() + "/download");

            in = response.getEntity().getContent();
        }
        catch (Exception ex)
        {
            try { EntityUtils.consume(response.getEntity()); } catch (Exception ex2) { }

            throw new RuntimeException(ex);
        }

        return in;
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
        Archive archive = getVault().readArchive(getId());

        this.reload(archive.getObject());
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
        Response response = getRemote().get(getResourceUri() + "/acl?id=" + principalId);

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
        grant(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public void revokeAll(DomainPrincipal principal)
    {
        revokeAll(principal.getDomainQualifiedId());
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").booleanValue();
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
            has = response.getObjectNode().get("check").booleanValue();
        }

        return has;
    }

    @Override
    public boolean hasPermission(DomainPrincipal principal, String authorityId)
    {
        return hasPermission(principal.getDomainQualifiedId(), authorityId);
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
    // ACCESSORS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getGroupId()
    {
        return getString(FIELD_GROUP_ID);
    }

    @Override
    public String getArtifactId()
    {
        return getString(FIELD_ARTIFACT_ID);
    }

    @Override
    public String getVersionId()
    {
        return getString(FIELD_VERSION_ID);
    }

    @Override
    public ObjectNode getContents()
    {
        return getObject(FIELD_CONTENTS);
    }

    @Override
    public ObjectNode getDependencies()
    {
        return getObject(FIELD_ARTIFACT_DEPENDENCIES);
    }

    @Override
    public ObjectNode getIncludes()
    {
        return getObject(FIELD_ARTIFACT_INCLUDES);
    }

    @Override
    public String getType()
    {
        return getString(FIELD_TYPE);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TEMPLATE PROPERTIES
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setTemplate(boolean template)
    {
        set(FIELD_TEMPLATE, template);
    }

    @Override
    public String getTemplate()
    {
        return getString(FIELD_TEMPLATE);
    }

    @Override
    public void setTemplateType(String templateType)
    {
        set(FIELD_TEMPLATE_TYPE, templateType);
    }

    @Override
    public String getTemplateType()
    {
        return getString(FIELD_TEMPLATE_TYPE);
    }

    @Override
    public void setTemplateKey(String templateKey)
    {
        set(FIELD_TEMPLATE_KEY, templateKey);
    }

    @Override
    public String getTemplateKey()
    {
        return getString(FIELD_TEMPLATE_KEY);
    }

}
