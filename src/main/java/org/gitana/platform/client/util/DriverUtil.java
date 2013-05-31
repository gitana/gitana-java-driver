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

package org.gitana.platform.client.util;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.beans.ACLEntry;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.client.platform.PlatformDocument;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.support.TypedID;
import org.gitana.platform.client.transfer.CopyJob;
import org.gitana.platform.services.job.JobState;
import org.gitana.platform.services.transfer.TransferSchedule;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class DriverUtil
{
    public static ACL toACL(Response response)
    {
        ACL acl = new ACL();

        ObjectNode objectNode = response.getObjectNode();
        ArrayNode rows = (ArrayNode) objectNode.get("rows");
        for (int x = 0; x < rows.size(); x++)
        {
            ObjectNode binding = (ObjectNode) rows.get(x);

            String principalId = binding.get(DomainPrincipal.FIELD_ID).getTextValue();
            String principalType = binding.get(DomainPrincipal.FIELD_TYPE).getTextValue();

            List<String> roles = new ArrayList<String>();
            ArrayNode array = (ArrayNode) binding.get("authorities");
            for (int i = 0; i < array.size(); i++)
            {
                roles.add(array.get(i).getTextValue());
            }

            ACLEntry entry = new ACLEntry();
            entry.setPrincipal(principalId);
            entry.setAuthorities(roles);

            acl.add(principalId, entry);
        }

        return acl;
    }

    public static List<String> toStringList(Response response)
    {
        List<String> list = new ArrayList<String>();

        ObjectNode objectNode = response.getObjectNode();
        ArrayNode rows = (ArrayNode) objectNode.get("rows");
        for (int i = 0; i < rows.size(); i++)
        {
            list.add(rows.get(i).getTextValue());
        }

        return list;
    }

    public static Map<String, String> params()
    {
        return new LinkedHashMap<String, String>();
    }

    public static Map<String, String> params(Pagination pagination)
    {
        Map<String, String> params = params();

        if (pagination != null)
        {
            // sorting
            if (pagination.getSorting().size() > 0)
            {
                String sort = JsonUtil.stringify((ObjectNode) pagination.getSorting().toJSON(), false);
                //sort = sort.replace("\"", "");
                params.put("sort", sort);
            }

            // skip
            if (pagination.hasSkip())
            {
                params.put("skip", String.valueOf(pagination.getSkip()));
            }

            // limit
            if (pagination.hasLimit())
            {
                params.put("limit", String.valueOf(pagination.getLimit()));
            }
        }

        return params;
    }

    public static ResultMap<Node> toNodes(ResultMap<BaseNode> baseNodes)
    {
        ResultMap nodes = new ResultMapImpl(baseNodes.offset(), baseNodes.totalRows());

        for (BaseNode baseNode: baseNodes.values())
        {
            nodes.put(baseNode.getId(), (Node) baseNode);
        }

        return nodes;
    }

    /**
     * Generic helper method to copy the source object into the target container.
     *
     * @param source
     * @param target
     */
    public static CopyJob copy(Cluster cluster, Remote remote, TypedID source, TypedID target, TransferSchedule schedule)
    {
        boolean synchronous = TransferSchedule.SYNCHRONOUS.equals(schedule);

        ArrayNode sourceDependencies = toCopyDependencyChain(source);
        ArrayNode targetDependencies = toCopyDependencyChain(target);

        // execute
        ObjectNode payload = JsonUtil.createObject();
        payload.put("sources", sourceDependencies);
        payload.put("targets", targetDependencies);
        Response response1 = remote.post("/tools/copy?schedule=" + TransferSchedule.ASYNCHRONOUS.toString(), payload);
        String jobId = response1.getId();

        Job job = DriverUtil.retrieveOrPollJob(cluster, jobId, synchronous);
        return new CopyJob(cluster, job.getObject(), job.isSaved());
    }

    public static ArrayNode toCopyDependencyChain(TypedID typedID)
    {
        ArrayNode array = JsonUtil.createArray();

        if (typedID instanceof Node)
        {
            array.addAll(toCopyDependencyChain(((Node) typedID).getBranch()));
        }
        else if (typedID instanceof Branch)
        {
            array.addAll(toCopyDependencyChain(((Branch) typedID).getRepository()));
        }
        else if (typedID instanceof PlatformDocument)
        {
            array.addAll(toCopyDependencyChain(((PlatformDocument) typedID).getPlatform()));
        }
        else if (typedID instanceof PlatformDataStore)
        {
            array.addAll(toCopyDependencyChain(((PlatformDataStore) typedID).getPlatform()));
        }

        array.add(toDependencyObject(typedID));

        return array;
    }

    public static ObjectNode toDependencyObject(TypedID typedID)
    {
        ObjectNode obj = JsonUtil.createObject();
        obj.put("typeId", typedID.getTypeId());
        obj.put("id", typedID.getId());

        return obj;
    }

    /**
     * Retrieves a job that was just posted for execution.  If synchronous, then this will poll until the job
     * either finishes or errors out before handing back.
     *
     * @param cluster
     * @param jobId
     * @param synchronous
     * @return
     */
    public static Job retrieveOrPollJob(Cluster cluster, String jobId, boolean synchronous)
    {
        Job job = null;
        if (!synchronous)
        {
            job = cluster.readJob(jobId);
        }
        else
        {
            boolean completed = false;

            do
            {
                job = cluster.readJob(jobId);
                if (job != null)
                {
                    if (JobState.FINISHED.equals(job.getState()) || JobState.ERROR.equals(job.getState()))
                    {
                        completed = true;
                    }
                }

                if (!completed)
                {
                    try { Thread.sleep(250); } catch (Exception ex) { completed = true; }
                }
            }
            while (!completed);
        }

        return job;
    }

}
