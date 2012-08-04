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

package org.gitana.platform.client.application;

import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.support.Selfable;

import java.util.Calendar;

/**
 * @author uzi
 */
public interface Email extends ApplicationDocument, Selfable
{
    // recipient
    public final static String FIELD_TO = "to";

    // specify the email body as a string on the confirmation object
    public final static String FIELD_BODY = "body";

    // other email fields
    public final static String FIELD_FROM = "from";
    public final static String FIELD_SUBJECT = "subject";
    public final static String FIELD_CC = "cc";
    public final static String FIELD_BCC = "bcc";
    public final static String FIELD_REPLY_TO = "replyTo";

    // recipient (domain user)
    public final static String FIELD_TO_DOMAIN_ID = "toDomainId";
    public final static String FIELD_TO_PRINCIPAL_ID = "toPrincipalId";

    // body (node attachment)
    public final static String FIELD_BODY_REPOSITORY_ID = "bodyRepositoryId";
    public final static String FIELD_BODY_BRANCH_ID = "bodyBranchId";
    public final static String FIELD_BODY_NODE_ID = "bodyNodeId";
    public final static String FIELD_BODY_ATTACHMENT_ID = "bodyAttachmenTId";

    public final static String FIELD_SENT = "sent";
    public final static String FIELD_SENT_ON = "sentOn";
    public final static String FIELD_SENT_BY = "sentBy";

    // TO

    public String getTo();
    public void setTo(String to);

    public void setToDomainUser(DomainUser user);
    public DomainUser getToDomainUser();


    // BODY

    public String getBody();
    public void setBody(String body);

    public Attachment getBodyAttachment();
    public void setBodyAttachment(Node node, String attachmentId);

    public String getFrom();
    public void setFrom(String from);

    public String getSubject();
    public void setSubject(String subject);

    public String getCC();
    public void setCC(String cc);

    public String getBCC();
    public void setBCC(String bcc);

    public String getReplyTo();
    public void setReplyTo(String replyTo);


    // SENT

    public boolean getSent();
    public Calendar dateSent();
    public String getSentBy();
}
