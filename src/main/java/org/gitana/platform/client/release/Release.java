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
 *   info@gitana.io
 */
package org.gitana.platform.client.release;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.repository.RepositoryDocument;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.Selfable;
import org.gitana.util.JsonUtil;

import java.util.Calendar;
import java.util.List;

/**
 * @author uzi
 */
public interface Release extends RepositoryDocument, AccessControllable, Selfable
{
    // properties: core
    public final static String FIELD_BRANCH_ID = "branchId";
    public final static String FIELD_REV = "rev";

    // properties: finalized
    public final static String FIELD_FINALIZED = "finalized";
    public final static String FIELD_FINALIZED_DATE = "finalizedDate";
    public final static String FIELD_FINALIZED_BY = "finalizedBy";

    // properties: scheduled
    public final static String FIELD_SCHEDULED = "scheduled";
    public final static String FIELD_SCHEDULED_DATE = "scheduledDate";
    public final static String FIELD_SCHEDULED_BY = "scheduledBy";

    // properties: release
    public final static String FIELD_RELEASED = "released";
    public final static String FIELD_RELEASE_DATE = "releaseDate";

    // properties: release scheduled work id
    public final static String FIELD_SCHEDULED_WORK_ID = "scheduledWorkId";

    // whether the release has outstanding merge conflicts that need to be resolved
    // before it can be finalized
    public final static String FIELD_FINALIZE_HAS_CONFLICTS = "finalizeHasConflicts";

    // whether the release is archived
    public final static String FIELD_ARCHIVED = "archived";

    public final static String FIELD_SNAPSHOT_ID = "snapshotId";

    public String getBranchId();
    public int getRev();

    public boolean getFinalized();
    public Calendar getFinalizedDate();
    public String getFinalizedBy();

    public boolean getScheduled();
    public Calendar getScheduledDate();
    public String getScheduledBy();
    public String getScheduledWorkId();

    public boolean getReleased();
    public Calendar getReleaseDate();

    public boolean getFinalizeHasConflicts();

    public boolean getArchived();

    public String getSnapshotId();

    public Branch getBranch();

    // operations

    public ObjectNode loadInfo();

    public ObjectNode finalize(boolean schedule);

    public void unfinalize();

    public void archive();

    public void unarchive();

    public void releaseImmediately();
}
