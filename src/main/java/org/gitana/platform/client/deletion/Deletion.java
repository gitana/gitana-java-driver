/**
 * Copyright 2016 Gitana Software, Inc.
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

package org.gitana.platform.client.deletion;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.repository.RepositoryDocument;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.client.support.TypedID;

import java.util.Calendar;

/**
 * @author uzi
 */
public interface Deletion extends RepositoryDocument, Selfable, TypedID
{
    // properties
    public final static String FIELD_DELETION = "_deletion";
    public final static String FIELD_DELETION_NODE_ID = "nodeId";
    public final static String FIELD_DELETION_DELETED_ON = "deletedOn";
    public final static String FIELD_DELETION_DELETED_BY = "deletedBy";
    public final static String FIELD_DELETION_CHANGESET_ID = "changesetId";

    public final static String FIELD_DELETION_TRANSACTION = "transaction";
    public final static String FIELD_DELETION_ASSOCIATION = "association";

    public String getDeletionNodeId();
    public Calendar getDeletionDeletedOn();
    public String getDeletionDeletedBy();
    public String getDeletionChangesetId();

    public boolean getDeletionTransaction();
    public boolean getDeletionAssociation();

    public Branch getBranch();
    public String getBranchId();

    public void restore();
    public void restore(ObjectNode data);
}
