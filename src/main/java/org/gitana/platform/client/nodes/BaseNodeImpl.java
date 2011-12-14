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

package org.gitana.platform.client.nodes;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.document.DocumentImpl;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.support.QName;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.Remote;

/**
 * Base class for nodes
 *
 * @author uzi
 */
public abstract class BaseNodeImpl extends DocumentImpl implements BaseNode
{
	private QName qname;
	private QName typeQName;

    private Branch branch;

    /**
     * Existing node constructor.
     *
     * @param branch
     * @param obj
     * @param isSaved
     */
    public BaseNodeImpl(Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);

        this.branch = branch;

        this.init();
    }

    private void init()
    {
        String _qname = getString(FIELD_QNAME);
        this.qname = QName.create(_qname);

        String _type = getString(FIELD_TYPE_QNAME);
        this.typeQName = QName.create(_type);
    }

    protected String getResourceUri()
    {
        return "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId();
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
    public Repository getRepository()
    {
        return getBranch().getRepository();
    }

    @Override
    public String getRepositoryId()
    {
        return getRepository().getId();
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

    @Override
    public QName getQName()
    {
    	return this.qname;
    }
    
    @Override
    public QName getTypeQName()
    {
    	return this.typeQName;
    }
    
    @Override
    public String getChangesetId()
    {
        return getSystemObject().get(SYSTEM_CHANGESET).getTextValue();
    }
    
    @Override
    public boolean isDeleted()
    {
        return getSystemObject().has(SYSTEM_DELETED);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean equals = false;

        if (obj instanceof Node)
        {
            Node _node = (Node) obj;

            equals = getId().equals(_node.getId()) &&
                     getChangesetId().equals(_node.getChangesetId()) &&
                     getBranchId().equals(_node.getBranchId());
        }
        else
        {
            equals = super.equals(obj);
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
        BaseNode baseNode = getBranch().readNode(getId());
        this.reload(baseNode);
    }

    @Override
    public void touch()
    {
        getRemote().post(getResourceUri() + "/touch");
    }
}
