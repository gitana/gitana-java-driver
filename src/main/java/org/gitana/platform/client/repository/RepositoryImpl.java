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
package org.gitana.platform.client.repository;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.platform.AbstractPlatformDataStoreImpl;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.release.Release;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class RepositoryImpl extends AbstractPlatformDataStoreImpl implements Repository
{
    public RepositoryImpl(Platform platform, ObjectNode obj, boolean isSaved)
    {
        super(platform, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_REPOSITORY;
    }

    @Override
    public String getResourceUri()
    {
        return "/repositories/" + getId();
    }

    @Override
    public void reload()
    {
        Repository repository = getPlatform().readRepository(getId());
        this.reload(repository.getObject());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // BRANCHES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Branch> listBranches()
    {
        return listBranches(null);
    }

    @Override
    public ResultMap<Branch> listBranches(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/repositories/" + getId() + "/branches", params);
        return getFactory().branches(this, response);
    }

    @Override
    public Branch readBranch(String branchId)
    {
        Branch branch = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getId() + "/branches/" + branchId);
            branch = getFactory().branch(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return branch;
    }

    @Override
    public Branch createBranch(String changesetId)
    {
        return createBranch(changesetId, JsonUtil.createObject());
    }

    @Override
    public Branch createBranch(String changesetId, ObjectNode object)
    {
        Changeset changeset = readChangeset(changesetId);
        String rootBranchId = changeset.getBranchId();

        return createBranch(rootBranchId, changesetId, object);
    }

    @Override
    public Branch createBranch(String rootBranchId, String changesetId, ObjectNode object)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Response response = getRemote().post("/repositories/" + getId() + "/branches/create/start?changeset=" + changesetId + "&branch=" + rootBranchId, object);
        String jobId = response.getId();
        Job job = getCluster().waitForJobCompletion(jobId);
        String branchId = job.getString("createdBranchId");
        return readBranch(branchId);
    }

    @Override
    public ResultMap<Branch> queryBranches(ObjectNode query)
    {
        return queryBranches(query, null);
    }

    @Override
    public ResultMap<Branch> queryBranches(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/repositories/" + getId() + "/branches/query", params, query);
        return getFactory().branches(this, response);
    }

    @Override
    public PermissionCheckResults checkBranchPermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post("/repositories/" + getId() + "/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // FILES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void uploadFile(String filename, byte[] bytes, String contentType)
    {
        try
        {
            getRemote().upload("/repositories/" + this.getId() + "/files/" + filename, bytes, contentType);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteFile(String filename)
    {
        getRemote().delete("/repositories/" + this.getId() + "/files/" + filename);
    }

    @Override
    public byte[] downloadFile(String filename)
    {
        byte[] bytes = null;

        try
        {
            bytes = getRemote().downloadBytes("/repositories/" + this.getId() + "/files/" + filename);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CHANGESETS
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Changeset> listChangesets()
    {
        return listChangesets(null);
    }

    @Override
    public ResultMap<Changeset> listChangesets(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/repositories/" + getId() + "/changesets", params);
        return getFactory().changesets(this, response);
    }

    @Override
    public Changeset readChangeset(String changesetId)
    {
        Changeset changeset = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getId() + "/changesets/" + changesetId);
            changeset = getFactory().changeset(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return changeset;
    }

    @Override
    public ResultMap<Changeset> queryChangesets(ObjectNode query)
    {
        return queryChangesets(query, null);
    }

    @Override
    public ResultMap<Changeset> queryChangesets(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/repositories/" + getId() + "/changesets/query", params, query);
        return getFactory().changesets(this, response);
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // RELEASES
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ResultMap<Release> listReleases()
    {
        return listReleases(null);
    }

    @Override
    public ResultMap<Release> listReleases(Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().get("/repositories/" + getId() + "/releases", params);
        return getFactory().releases(this, response);
    }

    @Override
    public Release readRelease(String releaseId)
    {
        Release release = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getId() + "/releases/" + releaseId);
            release = getFactory().release(this, response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting
            // TODO: information so that we can detect a proper 404
        }

        return release;
    }

    @Override
    public Release createRelease(ObjectNode object)
    {
        return createRelease(object, null);
    }

    @Override
    public Release createRelease(ObjectNode object, String sourceReleaseId)
    {
        // allow for null object
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        Map<String, String> params = new HashMap<String, String>();
        if (sourceReleaseId != null)
        {
            params.put("sourceId", sourceReleaseId);
        }

        Response response = getRemote().post("/repositories/" + getId() + "/releases/create/start", params, object);
        String jobId = response.getId();
        Job job = getCluster().waitForJobCompletion(jobId);
        String releaseId = job.getString("created-release-id");

        return readRelease(releaseId);
    }

    @Override
    public ResultMap<Release> queryReleases(ObjectNode query)
    {
        return queryReleases(query, null);
    }

    @Override
    public ResultMap<Release> queryReleases(ObjectNode query, Pagination pagination)
    {
        Map<String, String> params = DriverUtil.params(pagination);

        Response response = getRemote().post("/repositories/" + getId() + "/releases/query", params, query);
        return getFactory().releases(this, response);
    }

    @Override
    public PermissionCheckResults checkReleasePermissions(List<PermissionCheck> list)
    {
        ArrayNode array = JsonUtil.createArray();
        for (PermissionCheck check: list)
        {
            array.add(check.getObject());
        }

        ObjectNode object = JsonUtil.createObject();
        object.put("checks", array);

        Response response = getRemote().post("/repositories/" + getId() + "/permissions/check", object);
        return new PermissionCheckResults(response.getObjectNode());
    }
}
