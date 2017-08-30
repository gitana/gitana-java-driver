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

package org.gitana.platform.client.release;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.repository.AbstractRepositoryDocumentImpl;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.services.authority.AuthorityGrant;
import org.gitana.util.DateUtil;
import org.gitana.util.JsonUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class ReleaseImpl extends AbstractRepositoryDocumentImpl implements Release
{
    private boolean dirty;

    public ReleaseImpl(Repository repository, ObjectNode obj, boolean isSaved)
    {
        super(repository, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return "release";
    }

    @Override
    protected String getResourceUri()
    {
        return "/repositories/" + getRepositoryId() + "/releases/" + getId();
    }

    @Override
    public String getBranchId()
    {
        return getString(FIELD_BRANCH_ID);
    }

    @Override
    public int getRev()
    {
        return getInt(FIELD_REV);
    }

    @Override
    public boolean getFinalized()
    {
        return getBoolean(FIELD_FINALIZED);
    }

    @Override
    public Calendar getFinalizedDate()
    {
        Calendar calendar = null;

        if (has(FIELD_FINALIZED_DATE))
        {
            long ms = JsonUtil.objectGetLong(getObject(FIELD_FINALIZED_DATE), "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public String getFinalizedBy()
    {
        return getString(FIELD_FINALIZED_BY);
    }

    @Override
    public boolean getScheduled()
    {
        return getBoolean(FIELD_SCHEDULED);
    }

    @Override
    public Calendar getScheduledDate()
    {
        Calendar calendar = null;

        if (has(FIELD_SCHEDULED_DATE))
        {
            long ms = JsonUtil.objectGetLong(getObject(FIELD_SCHEDULED_DATE), "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public String getScheduledBy()
    {
        return getString(FIELD_SCHEDULED_BY);
    }

    @Override
    public String getScheduledWorkId()
    {
        return getString(FIELD_SCHEDULED_WORK_ID);
    }

    @Override
    public boolean getReleased()
    {
        return getBoolean(FIELD_RELEASED);
    }

    @Override
    public Calendar getReleaseDate()
    {
        Calendar calendar = null;

        if (has(FIELD_RELEASE_DATE))
        {
            long ms = JsonUtil.objectGetLong(getObject(FIELD_RELEASE_DATE), "ms");
            if (ms != -1)
            {
                calendar = DateUtil.convertTimestamp(ms);
            }
        }

        return calendar;
    }

    @Override
    public boolean getFinalizeHasConflicts()
    {
        return getBoolean(FIELD_FINALIZE_HAS_CONFLICTS);
    }

    @Override
    public boolean getArchived()
    {
        return getBoolean(FIELD_ARCHIVED);
    }

    @Override
    public String getSnapshotId()
    {
        return getString(FIELD_SNAPSHOT_ID);
    }

    @Override
    public Branch getBranch()
    {
        return getRepository().readBranch(getBranchId());
    }

    public ObjectNode loadInfo()
    {
        ObjectNode info = null;

        try
        {
            Response response = getRemote().get(getResourceUri() + "/info");
            info = JsonUtil.objectGetObject(response.getObjectNode(), "info");
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return info;
    }

    @Override
    public ObjectNode finalize(boolean schedule)
    {
        ObjectNode payload = JsonUtil.createObject();
        payload.put("schedule", schedule);

        Response response = getRemote().post(getResourceUri() + "/finalize", payload);
        return JsonUtil.objectGetObject(response.getObjectNode(), "result");
    }

    public void unfinalize()
    {
        getRemote().post(getResourceUri() + "/unfinalize");
    }

    public void archive()
    {
        getRemote().post(getResourceUri() + "/archive");
    }

    public void unarchive()
    {
        getRemote().post(getResourceUri() + "/unarchive");
    }

    public void releaseImmediately()
    {
        getRemote().post(getResourceUri() + "/releaseimmediately");
    }







    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELFABLE
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void reload()
    {
        Release r = getRepository().readRelease(this.getId());
        this.reload(r.getObject());
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


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // ACL
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get(getResourceUri() + "/acl/list");

        return DriverUtil.toACL(response);
    }

    @Override
    public List<String> getACL(String principalId)
    {
        Response response = getRemote().get(getResourceUri() + "/acl?id=" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/grant?id=" + principalId);
    }

    @Override
    public void grant(DomainPrincipal principal, String authorityId)
    {
        grant(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/revoke?id=" + principalId);
    }

    @Override
    public void revoke(DomainPrincipal principal, String authorityId)
    {
        revoke(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public void revokeAll(DomainPrincipal principal)
    {
        revokeAll(principal.getDomainQualifiedId());
    }

    @Override
    public boolean hasAuthority(String principalId, String authorityId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/authorities/" + authorityId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").booleanValue();
        }

        return has;
    }

    @Override
    public boolean hasAuthority(DomainPrincipal principal, String authorityId)
    {
        return hasAuthority(principal.getDomainQualifiedId(), authorityId);
    }

    @Override
    public Map<String, Map<String, AuthorityGrant>> getAuthorityGrants(List<String> principalIds)
    {
        ObjectNode object = JsonUtil.createObject();
        JsonUtil.objectPut(object, "principals", principalIds);

        Response response = getRemote().post(getResourceUri() + "/authorities", object);
        return getFactory().principalAuthorityGrants(response);
    }

    @Override
    public boolean hasPermission(String principalId, String permissionId)
    {
        boolean has = false;

        Response response = getRemote().post(getResourceUri() + "/permissions/" + permissionId + "/check?id=" + principalId);
        if (response.getObjectNode().has("check"))
        {
            has = response.getObjectNode().get("check").booleanValue();
        }

        return has;
    }

    @Override
    public boolean hasPermission(DomainPrincipal principal, String authorityId)
    {
        return hasPermission(principal.getDomainQualifiedId(), authorityId);
    }

}
