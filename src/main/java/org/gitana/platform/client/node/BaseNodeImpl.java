/**
 * Copyright 2017 Gitana Software, Inc.
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

package org.gitana.platform.client.node;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.http.HttpPayload;
import org.gitana.platform.client.Driver;
import org.gitana.platform.client.attachment.Attachment;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.client.repository.AbstractRepositoryDocumentImpl;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.*;
import org.gitana.platform.client.transfer.CopyJob;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.reference.Reference;
import org.gitana.platform.services.transfer.TransferImportConfiguration;
import org.gitana.platform.services.transfer.TransferImportStrategy;
import org.gitana.platform.services.transfer.TransferSchedule;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;

import java.util.*;

/**
 * Base class for nodes
 *
 * @author uzi
 */
public abstract class BaseNodeImpl extends AbstractRepositoryDocumentImpl implements BaseNode
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
        super(branch.getRepository(), obj, isSaved);

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
    public Reference ref()
    {
        return Reference.create(getTypeId(), getPlatformId(), getRepositoryId(), getBranchId(), getId());
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
    public Changeset getChangeset()
    {
        return getRepository().readChangeset(getChangesetId());
    }

    @Override
    public String getChangesetId()
    {
        return getSystemObject().get(SYSTEM_CHANGESET).textValue();
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // FEATURES
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<String> getFeatureIds()
    {
        List<String> featureIds = new ArrayList<String>();

        ObjectNode featuresObject = this.getObject("_features");
        if (featuresObject != null)
        {
            Iterator<String> fieldNames = featuresObject.fieldNames();
            while (fieldNames.hasNext())
            {
                featureIds.add(fieldNames.next());
            }
        }

        return featureIds;
    }

    @Override
    public ObjectNode getFeature(String featureId)
    {
        ObjectNode featureObject = JsonUtil.createObject();

        ObjectNode featuresObject = this.getObject("_features");
        if (featuresObject != null)
        {
            if (featuresObject.has(featureId))
            {
                featureObject = (ObjectNode) featuresObject.get(featureId);
            }
        }

        return featureObject;
    }

    @Override
    public void removeFeature(String featureId)
    {
        getRemote().delete(this.getResourceUri() + "/features/" + featureId);

        // reload object
        this.reload();
    }

    @Override
    public void addFeature(String featureId, ObjectNode featureConfigObject)
    {
        getRemote().post(this.getResourceUri() + "/features/" + featureId, featureConfigObject);

        // reload object
        this.reload();
    }

    @Override
    public boolean hasFeature(String featureId)
    {
        boolean hasFeature = false;

        ObjectNode featuresObject = this.getObject("_features");
        if (featuresObject != null)
        {
            hasFeature = featuresObject.has(featureId);
        }

        return hasFeature;
    }

    @Override
    public CopyJob copy(TypedID targetContainer, TransferImportStrategy strategy, Map<String, Object> additionalConfiguration)
    {
        if (additionalConfiguration == null)
        {
            additionalConfiguration = new HashMap<String, Object>();
        }

        // if we're copying to the same branch, we assume COPY_EVERYTHING and COPY_ON_EXISTING = false
        boolean sameBranch = false;
        if (targetContainer instanceof Branch)
        {
            sameBranch = this.getBranchId().equals((targetContainer).getId());
        }
        else if (targetContainer instanceof BaseNode)
        {
            sameBranch = this.getBranchId().equals(((BaseNode) targetContainer).getBranchId());
        }
        if (sameBranch)
        {
            if (strategy == null)
            {
                strategy = TransferImportStrategy.COPY_EVERYTHING;
            }

            if (TransferImportStrategy.COPY_EVERYTHING.equals(strategy))
            {
                if (additionalConfiguration.get(TransferImportConfiguration.FIELD_COPY_ON_EXISTING) == null)
                {
                    additionalConfiguration.put(TransferImportConfiguration.FIELD_COPY_ON_EXISTING, false);
                }
            }
        }

        return DriverUtil.copy(getCluster(), getRemote(), this, targetContainer, strategy, additionalConfiguration, TransferSchedule.SYNCHRONOUS);
    }

    @Override
    public CopyJob copyAsync(TypedID targetContainer, TransferImportStrategy strategy, Map<String, Object> additionalConfiguration)
    {
        // if we're copying to the same branch, we assume COPY_EVERYTHING and COPY_ON_EXISTING = false
        boolean sameBranch = false;
        if (targetContainer instanceof Branch)
        {
            sameBranch = this.getBranchId().equals((targetContainer).getId());
        }
        else if (targetContainer instanceof BaseNode)
        {
            sameBranch = this.getBranchId().equals(((BaseNode) targetContainer).getBranchId());
        }
        if (sameBranch)
        {
            if (strategy == null)
            {
                strategy = TransferImportStrategy.COPY_EVERYTHING;
            }

            if (TransferImportStrategy.COPY_EVERYTHING.equals(strategy))
            {
                if (additionalConfiguration == null)
                {
                    additionalConfiguration = new HashMap<String, Object>();
                }

                additionalConfiguration.put(TransferImportConfiguration.FIELD_COPY_ON_EXISTING, false);
            }
        }

        return DriverUtil.copy(getCluster(), getRemote(), this, targetContainer, strategy, additionalConfiguration, TransferSchedule.ASYNCHRONOUS);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ATTACHMENTS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void uploadAttachment(byte[] bytes, String contentType)
    {
        uploadAttachment(null, bytes, contentType);
    }

    @Override
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType)
    {
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = getResourceUri() + "/attachments/" + attachmentId;

        try
        {
            getRemote().upload(uri, bytes, contentType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            throw new RuntimeException(ex);
        }
    }

    @Override
    public void uploadAttachment(String attachmentId, byte[] bytes, String contentType, String fileName)
    {
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = getResourceUri() + "/attachments/" + attachmentId;

        try
        {
            getRemote().upload(uri, bytes, contentType, fileName);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void uploadAttachments(HttpPayload... payloads)
    {
        Map<String, String> params = new HashMap<String, String>();

        uploadAttachments(params, payloads);
    }

    @Override
    public void uploadAttachments(Map<String, String> params, HttpPayload... payloads)
    {
        // build the uri
        String uri = getResourceUri() + "/attachments";

        try
        {
            getRemote().upload(uri, params, payloads);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public byte[] downloadAttachment()
    {
        return downloadAttachment(null);
    }

    @Override
    public byte[] downloadAttachment(String attachmentId)
    {
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = getResourceUri() + "/attachments/" + attachmentId;

        byte[] bytes = null;
        try
        {
            bytes = getRemote().downloadBytes(uri);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }

    @Override
    public ResultMap<Attachment> listAttachments()
    {
        Response response = getRemote().get(getResourceUri() + "/attachments");

        return getFactory().attachments(this, response);
    }

    @Override
    public String getDownloadUri(String attachmentId)
    {
        return getResourceUri() + "/attachments/" + attachmentId;
    }

    @Override
    public void deleteAttachment()
    {
        deleteAttachment(null);
    }

    @Override
    public void deleteAttachment(String attachmentId)
    {
        if (attachmentId == null)
        {
            attachmentId = "default";
        }

        // build the uri
        String uri = getResourceUri() + "/attachments/" + attachmentId;

        try
        {
            getRemote().delete(uri);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            throw new RuntimeException(ex);
        }
    }

}
