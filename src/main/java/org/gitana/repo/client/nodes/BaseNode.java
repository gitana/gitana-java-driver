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

import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Document;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Selfable;
import org.gitana.repo.namespace.QName;

/**
 * @author uzi
 */
public interface BaseNode extends Document, Selfable
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
    public String getChangesetId();
    //public int getChangesetRev();
            
    // flags
    public boolean isDeleted();
}
