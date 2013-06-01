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

package org.gitana.platform.client.node.type;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.NodeImpl;
import org.gitana.platform.client.principal.DomainUser;

/**
 * @author uzi
 */
public class PersonImpl extends NodeImpl implements Person
{
    public PersonImpl(Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(branch, obj, isSaved);
    }

    @Override
    public String getPrincipalId()
    {
        return getString(FIELD_PRINCIPAL_ID);
    }

    @Override
    public String getPrincipalDomainId()
    {
        return getString(FIELD_PRINCIPAL_DOMAIN_ID);
    }

    @Override
    public String getPrincipalName()
    {
        return getString(FIELD_PRINCIPAL_NAME);
    }

    @Override
    public String getPrincipalEmail()
    {
        return getString(FIELD_PRINCIPAL_EMAIL);
    }

    @Override
    public DomainUser getDomainUser()
    {
        Domain domain = getBranch().getPlatform().readDomain(getPrincipalDomainId());
        if (domain == null)
        {
            throw new RuntimeException("Unable to read domain: " + getPrincipalDomainId());
        }

        return (DomainUser) domain.readPrincipal(getPrincipalId());
    }
}
