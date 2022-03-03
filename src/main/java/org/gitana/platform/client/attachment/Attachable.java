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

import org.gitana.http.HttpPayload;
import org.gitana.platform.support.ResultMap;

import java.util.Map;

/**
 * @author uzi
 */
public interface Attachable
{
    /**
     * Uploads the default attachment.
     *
     * @param bytes
     * @param contentType
     */
    public void uploadAttachment(byte[] bytes, String contentType);

    /**
     * Uploads an attachment.
     *
     * @param attachmentId
     * @param bytes
     * @param contentType
     */
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType);

    /**
     * Uploads an attachment.
     *
     * @param attachmentId
     * @param attachmentId
     * @param bytes
     * @param contentType
     * @param fileName
     */
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType, String fileName);

    /**
     * Uploads a group of attachments.
     *
     * @param payloads
     */
    public void uploadAttachments(HttpPayload... payloads );

    /**
     * Uploads a group of attachments.
     *
     * @param payloads
     */
    public void uploadAttachments(Map<String, String> params, HttpPayload... payloads );

    /**
     * Downloads the default attachment.
     *
     * @return attachment
     */
    public byte[] downloadAttachment();

    /**
     * Downloads an attachment.
     *
     * @param attachmentId
     *
     * @return attachment
     */
    public byte[] downloadAttachment(String attachmentId);

    /**
     * @return map of attachments
     */
    public ResultMap<Attachment> listAttachments();

    /**
     * Acquires the download URI for the attachment
     *
     * @param attachmentId
     *
     * @return uri
     */
    public String getDownloadUri(String attachmentId);

    /**
     * Deletes the default attachment.
     */
    public void deleteAttachment();

    /**
     * Deletes an attachment.
     *
     * @param attachmentId
     */
    public void deleteAttachment(String attachmentId);
}