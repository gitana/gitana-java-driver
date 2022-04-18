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
package org.gitana.platform.client.archive;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.attachment.Attachable;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.client.vault.VaultDocument;
import org.gitana.platform.services.archive.Manifest;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author uzi
 */
public interface Archive extends VaultDocument, Attachable, AccessControllable, Selfable, Manifest
{	
    // fields
    public final static String FIELD_TEMPLATE = "template";
    public final static String FIELD_TEMPLATE_TYPE = "templateType";
    public final static String FIELD_TEMPLATE_KEY = "templateKey";

    public String getGroupId();
    public String getArtifactId();
    public String getVersionId();

    public InputStream download()
        throws IOException;

    public ObjectNode getContents();
    public ObjectNode getIncludes();
    public ObjectNode getParts();
    public String getType();

    public void setTemplate(boolean template);
    public String getTemplate();

    public void setTemplateType(String templateType);
    public String getTemplateType();

    public void setTemplateKey(String templateKey);
    public String getTemplateKey();

    public void publish();
    public void unpublish();
}
