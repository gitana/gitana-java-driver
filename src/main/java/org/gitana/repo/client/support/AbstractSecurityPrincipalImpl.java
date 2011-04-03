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
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.ObjectFactory;
import org.gitana.repo.client.SecurityPrincipal;
import org.gitana.security.PrincipalType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public abstract class AbstractSecurityPrincipalImpl extends DocumentImpl implements SecurityPrincipal
{
    private Gitana gitana;

    public AbstractSecurityPrincipalImpl(Gitana gitana, ObjectNode obj, boolean isSaved)
    {
    	super(obj, isSaved);

        this.gitana = gitana;
    }

    protected Remote getRemote()
    {
        return gitana.getRemote();
    }

    protected ObjectFactory getFactory()
    {
        return gitana.getFactory();
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
}
