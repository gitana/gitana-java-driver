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

package org.gitana.repo.client.nodes;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.ObjectFactory;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.support.DocumentImpl;
import org.gitana.repo.client.support.Remote;
import org.gitana.repo.namespace.QName;

/**
 * Base class for nodes
 *
 * @author uzi
 */
public abstract class BaseNodeImpl extends DocumentImpl implements BaseNode
{
	private QName qname;
	private QName typeQName;

    private Gitana gitana;
    private Branch branch;

    /**
     * Existing node constructor.
     *
     * @param branch
     * @param obj
     * @param isSaved
     */
    public BaseNodeImpl(Gitana gitana, Branch branch, ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);

        this.gitana = gitana;
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

    protected ObjectFactory getFactory()
    {
        return this.gitana.getFactory();
    }


    protected Remote getRemote()
    {
        return gitana.getRemote();
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
        getRemote().put("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getId());
    }
}
