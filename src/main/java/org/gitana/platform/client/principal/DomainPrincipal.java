/**
 * Copyright 2025 Gitana Software, Inc.
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
 *   info@gitana.io
 */
package org.gitana.platform.client.principal;

import org.gitana.platform.client.attachment.Attachable;
import org.gitana.platform.client.domain.DomainDocument;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.AccessPolicyHolder;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.services.principals.PrincipalType;
import org.gitana.platform.support.ResultMap;

import java.security.Principal;
import java.util.List;

/**
 * @author uzi
 */
public interface DomainPrincipal extends DomainDocument, Principal, Attachable, Selfable, AccessControllable, AccessPolicyHolder
{
    // fields
    public final static String FIELD_NAME = "name";
    public final static String FIELD_TYPE = "type";

    // fields
    public final static String FIELD_AUTHORITIES = "authorities";

    public String getName();
    public void setName(String name);

    public PrincipalType getType();
    public void setType(PrincipalType type);

    public String getDomainId();

    public String getDomainQualifiedName();

    public String getDomainQualifiedId();

    /**
     * @return granted authorities for this user
     */
    public List<String> getAuthorities();

    // PARENTS

    public ResultMap<DomainGroup> listParentGroups();

    public ResultMap<DomainGroup> listParentGroups(boolean includeAncestors);
}