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

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.client.document.Document;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.client.support.TypedID;
import org.gitana.platform.support.QName;

import java.util.List;

/**
 * @author uzi
 */
public interface BaseNode extends Document, Selfable, TypedID
{
	// additional metadata fields
    public final static String FIELD_FEATURES = "_features";
    public final static String FIELD_QNAME = "_qname";
    public final static String FIELD_TYPE_QNAME = "_type";

    // system metadata
    public final static String SYSTEM_CHANGESET = "changeset";
    public final static String SYSTEM_DELETED = "deleted";

    public Repository getRepository();
    public String getRepositoryId();

    public Branch getBranch();
    public String getBranchId();

    // qualified name
    public QName getQName();

    // type
    public QName getTypeQName();

    // changeset information
    public Changeset getChangeset();
    public String getChangesetId();
    //public int getChangesetRev();
            
    // flags
    public boolean isDeleted();

    /**
     * Touches the node.
     */
    public void touch();


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // FEATURES
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<String> getFeatureIds();

    public ObjectNode getFeature(String featureId);

    public void removeFeature(String featureId);

    public void addFeature(String featureId, ObjectNode featureConfigObject);

    public boolean hasFeature(String featureId);
}
