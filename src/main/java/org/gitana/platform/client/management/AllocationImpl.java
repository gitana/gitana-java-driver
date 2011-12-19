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

package org.gitana.platform.client.management;

import org.codehaus.jackson.node.ObjectNode;

/**
 * @author uzi
 */
public class AllocationImpl extends AbstractManagementDocumentImpl implements Allocation
{
    public AllocationImpl(Management management, ObjectNode obj, boolean isSaved)
    {
        super(management, obj, isSaved);
    }

    @Override
    protected String getResourceUri()
    {
        return "/plans/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Plan)
        {
            Plan other = (Plan) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }

    @Override
    public String getTenantId()
    {
        return getString(FIELD_TENANT_ID);
    }

    @Override
    public void setTenantId(String tenantId)
    {
        set(FIELD_TENANT_ID, tenantId);
    }

    @Override
    public String getObjectType()
    {
        return getString(FIELD_OBJECT_TYPE);
    }

    @Override
    public void setObjectType(String objectType)
    {
        set(FIELD_OBJECT_TYPE, objectType);
    }

    @Override
    public String getObjectId()
    {
        return getString(FIELD_OBJECT_ID);
    }

    @Override
    public void setObjectId(String objectId)
    {
        set(FIELD_OBJECT_ID, objectId);
    }
}
