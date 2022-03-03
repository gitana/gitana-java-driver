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
package org.gitana.platform.client.deletion;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.repository.AbstractRepositoryDocumentImpl;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.Remote;
import org.gitana.util.DateUtil;
import org.gitana.util.JsonUtil;

import java.util.Calendar;

/**
 * Deletion
 *
 * @author uzi
 */
public class DeletionImpl extends AbstractRepositoryDocumentImpl implements Deletion
{
    private Branch branch;

    /**
     * Existing node constructor.
     *
     * @param branch
     * @param obj
     * @param isSaved
     */
    public DeletionImpl(Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(branch.getRepository(), obj, isSaved);

        this.branch = branch;

        this.init();
    }

    private void init()
    {
        acquireDeletion();
    }

    protected String getResourceUri()
    {
        return "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/deletions/" + getId();
    }

    protected ObjectFactory getFactory()
    {
        return getDriver().getFactory();
    }

    protected Driver getDriver()
    {
        return DriverContext.getDriver();
    }

    protected Remote getRemote()
    {
        return getDriver().getRemote();
    }

    @Override
    public String getTypeId()
    {
        return "deletion";
    }


    @Override
    public boolean equals(Object obj)
    {
        boolean equals = false;

        if (obj instanceof Deletion)
        {
            Deletion _deletion = (Deletion) obj;

            equals = getId().equals(_deletion.getId());
        }

        return equals;
    }

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
        Deletion existing = getBranch().readDeletion(getId());
        this.reload(existing);
    }

    @Override
    public Branch getBranch()
    {
        return branch;
    }

    @Override
    public String getBranchId()
    {
        return getBranch().getId();
    }

    ///////////////

    protected ObjectNode acquireDeletion()
    {
        ObjectNode _deletion = getObject(FIELD_DELETION);
        if (_deletion == null)
        {
            _deletion = JsonUtil.createObject();

            set(FIELD_DELETION, _deletion);
        }

        return _deletion;
    }

    @Override
    public String getDeletionNodeId()
    {
        return JsonUtil.objectGetString(acquireDeletion(), FIELD_DELETION_NODE_ID);
    }

    @Override
    public Calendar getDeletionDeletedOn()
    {
        Calendar calendar = null;

        if (acquireDeletion().has(FIELD_DELETION_DELETED_ON))
        {
            long ms = JsonUtil.objectGetLong((ObjectNode) acquireDeletion().get(FIELD_DELETION_DELETED_ON), "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public String getDeletionDeletedBy()
    {
        return JsonUtil.objectGetString(acquireDeletion(), FIELD_DELETION_DELETED_BY);
    }

    @Override
    public String getDeletionChangesetId()
    {
        return JsonUtil.objectGetString(acquireDeletion(), FIELD_DELETION_CHANGESET_ID);
    }

    @Override
    public boolean getDeletionTransaction()
    {
        return JsonUtil.objectGetBoolean(acquireDeletion(), FIELD_DELETION_TRANSACTION);
    }

    @Override
    public boolean getDeletionAssociation()
    {
        return JsonUtil.objectGetBoolean(acquireDeletion(), FIELD_DELETION_ASSOCIATION);
    }

    ///

    @Override
    public void restore()
    {
        restore(null);
    }

    @Override
    public void restore(ObjectNode data)
    {
        if (data == null)
        {
            data = JsonUtil.createObject();
        }

        getRemote().post(getResourceUri() + "/restore", data);
    }

}
