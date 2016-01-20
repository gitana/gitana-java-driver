/**
 * Copyright 2016 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.DateUtil;
import org.gitana.util.JsonUtil;

import java.util.Calendar;

/**
 * @author uzi
 */
public class EmailImpl extends AbstractApplicationDocumentImpl implements Email
{
    public EmailImpl(Application application, ObjectNode obj, boolean isSaved)
    {
        super(application, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_EMAIL;
    }

    @Override
    public String getResourceUri()
    {
        return "/applications/" + getApplicationId() + "/emails/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Email)
        {
            Email other = (Email) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
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
        Email reset = getApplication().readEmail(getId());

        this.reload(reset.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // API
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setTo(String to)
    {
        set(FIELD_TO, to);
    }

    @Override
    public String getTo()
    {
        return getString(FIELD_TO);
    }

    @Override
    public void setToDomainUser(DomainUser user)
    {
        set(FIELD_TO_DOMAIN_ID, user.getDomainId());
        set(FIELD_TO_PRINCIPAL_ID, user.getId());
    }

    @Override
    public DomainUser getToDomainUser()
    {
        Platform platform = getApplication().getPlatform();

        String domainId = getString(FIELD_TO_DOMAIN_ID);
        String principalId = getString(FIELD_TO_PRINCIPAL_ID);

        DomainUser user = null;

        if (domainId != null)
        {
            Domain domain = platform.readDomain(domainId);
            if (domain == null)
            {
                throw new RuntimeException("Missing domain: " + domainId);
            }

            user = (DomainUser) domain.readPrincipal(principalId);
        }

        return user;
    }

    @Override
    public String getBody()
    {
        return getString(FIELD_BODY);
    }

    @Override
    public void setBody(String body)
    {
        set(FIELD_BODY, body);
    }

    @Override
    public Attachment getBodyAttachment()
    {
        String repositoryId = getString(FIELD_BODY_REPOSITORY_ID);
        if (repositoryId == null)
        {
            return null;
        }

        String branchId = getString(FIELD_BODY_BRANCH_ID);
        if (branchId == null)
        {
            return null;
        }

        String nodeId = getString(FIELD_BODY_NODE_ID);
        if (nodeId == null)
        {
            return null;
        }

        String attachmentId = getString(FIELD_BODY_ATTACHMENT_ID);
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        Platform platform = this.getApplication().getPlatform();

        Repository repository = platform.readRepository(repositoryId);
        if (repository == null)
        {
            throw new RuntimeException("No repository: " + repositoryId);
        }

        Node node = (Node) repository.readBranch(branchId).readNode(nodeId);
        return node.listAttachments().get(attachmentId);
    }

    @Override
    public void setBodyAttachment(Node node, String attachmentId)
    {
        set(FIELD_BODY_REPOSITORY_ID, node.getRepositoryId());
        set(FIELD_BODY_BRANCH_ID, node.getBranchId());
        set(FIELD_BODY_NODE_ID, node.getId());
        set(FIELD_BODY_ATTACHMENT_ID, attachmentId);
    }

    @Override
    public String getFrom()
    {
        return getString(FIELD_FROM);
    }

    @Override
    public void setFrom(String from)
    {
        set(FIELD_FROM, from);
    }

    @Override
    public String getSubject()
    {
        return getString(FIELD_SUBJECT);
    }

    @Override
    public void setSubject(String subject)
    {
        set(FIELD_SUBJECT, subject);
    }

    @Override
    public String getCC()
    {
        return getString(FIELD_CC);
    }

    @Override
    public void setCC(String cc)
    {
        set(FIELD_CC, cc);
    }

    @Override
    public String getBCC()
    {
        return getString(FIELD_BCC);
    }

    @Override
    public void setBCC(String bcc)
    {
        set(FIELD_BCC, bcc);
    }

    @Override
    public String getReplyTo()
    {
        return getString(FIELD_REPLY_TO);
    }

    @Override
    public void setReplyTo(String replyTo)
    {
        set(FIELD_REPLY_TO, replyTo);
    }

    @Override
    public boolean getSent()
    {
        return getBoolean(FIELD_SENT);
    }

    @Override
    public Calendar dateSent()
    {
        Calendar calendar = null;

        if (has(FIELD_SENT_ON))
        {
            ObjectNode timestamp = getObject(FIELD_SENT_ON);

            long ms = JsonUtil.objectGetLong(timestamp, "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public String getSentBy()
    {
        return getString(FIELD_SENT_BY);
    }
}
