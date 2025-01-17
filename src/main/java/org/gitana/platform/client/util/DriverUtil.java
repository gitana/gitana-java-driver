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
package org.gitana.platform.client.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.beans.ACL;
import org.gitana.platform.client.beans.ACLEntry;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.client.platform.PlatformDocument;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.support.*;
import org.gitana.platform.client.transfer.CopyJob;
import org.gitana.platform.client.transfer.CopyJobData;
import org.gitana.platform.client.transfer.CopyJobResult;
import org.gitana.platform.services.transfer.TransferDependency;
import org.gitana.platform.services.transfer.TransferImportStrategy;
import org.gitana.platform.services.transfer.TransferSchedule;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.support.ResultMapImpl;
import org.gitana.util.JsonUtil;

import java.util.*;

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

            String principalId = binding.get(DomainPrincipal.FIELD_ID).textValue();
            String principalType = binding.get(DomainPrincipal.FIELD_TYPE).textValue();

            List<String> roles = new ArrayList<String>();
            ArrayNode array = (ArrayNode) binding.get("authorities");
            for (int i = 0; i < array.size(); i++)
            {
                roles.add(array.get(i).textValue());
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
            list.add(rows.get(i).textValue());
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
            else if (pagination.getLimit() == -1)
            {
                params.put("limit", String.valueOf(-1));
            }

            // options
            if (pagination.getOptions() != null)
            {
                ObjectNode json = pagination.getOptions().toJSON();
                if (json.size() > 0)
                {
                    params.put("options", JsonUtil.stringify(json, false));
                }
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
     * @param cluster
     * @param remote
     * @param factory
     * @param source
     * @param target
     * @param strategy
     * @param additionalConfiguration
     * @param schedule
     *
     * @return
     */
    public static CopyJob copy(Cluster cluster, Remote remote, ObjectFactory factory, TypedID source, TypedID target, TransferImportStrategy strategy, Map<String, Object> additionalConfiguration, TransferSchedule schedule)
    {
        boolean synchronous = TransferSchedule.SYNCHRONOUS.equals(schedule);

        if (strategy == null)
        {
            strategy = TransferImportStrategy.COPY_EVERYTHING;
        }

        ArrayNode sourceDependencies = toCopyDependencyChain(source);
        ArrayNode targetDependencies = toCopyDependencyChain(target);

        // build payload
        ObjectNode payload = JsonUtil.createObject();
        payload.put("sources", sourceDependencies);
        payload.put("targets", targetDependencies);

        if (additionalConfiguration != null)
        {
            ObjectNode configuration = (ObjectNode) JsonUtil.toJsonNode(additionalConfiguration);
            payload.put("configuration", configuration);
        }

        // execute
        Response response1 = remote.post("/tools/copy?schedule=" + TransferSchedule.ASYNCHRONOUS.toString() + "&strategy=" + strategy.toString(), payload);
        String jobId = response1.getId();

        CopyJob job = (CopyJob) cluster.pollForJobCompletion(jobId, CopyJob.class, CopyJobData.class, CopyJobResult.class);

        return job;
    }

    public static ArrayNode toCopyDependencyChain(TypedID typedID)
    {
        ArrayNode array = JsonUtil.createArray();

        if (typedID instanceof Node)
        {
            array.addAll(toCopyDependencyChain(((Node) typedID).getChangeset()));
        }
        else if (typedID instanceof Changeset)
        {
            array.addAll(toCopyDependencyChain(((Changeset) typedID).getBranch()));
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
        obj.put(TransferDependency.TYPE_ID, typedID.getTypeId());
        obj.put(TransferDependency.ID, typedID.getId());

        if (typedID instanceof Referenceable)
        {
            obj.put(TransferDependency.REF, ((Referenceable) typedID).ref().getReference());
        }

        return obj;
    }

    public static String readKey(ResourceBundle bundle, String key)
    {
        String value = null;

        if (bundle.containsKey(key))
        {
            value = bundle.getString(key);
        }

        return value;
    }

    public static int acquireIntFromSystemOrBundle(ResourceBundle bundle, String key, String systemKey, int defaultValue)
    {
        int value = defaultValue;

        // check gitana properties file (legacy)
        try
        {
            String text = bundle.getString(key);
            if (text != null)
            {
                value = Integer.parseInt(text, 10);
            }
        } catch (Exception ex) {
            // swallow
        }

        // if we have a system properties value, use that instead
        if (systemKey != null)
        {
            int systemValue = acquireInt(System.getProperties(), systemKey, -1);
            if (systemValue != -1)
            {
                value = systemValue;
            }
        }

        return value;
    }

    public static boolean acquireBooleanFromSystemOrBundle(ResourceBundle bundle, String key, String systemKey, boolean defaultValue)
    {
        boolean value = defaultValue;

        // check gitana properties file (legacy)
        try
        {
            String text = bundle.getString(key);
            if (text != null)
            {
                value = Boolean.parseBoolean(text);
            }
        } catch (Exception ex) {
            // swallow
        }

        // if we have a system properties value, use that instead
        if (systemKey != null)
        {
            boolean systemValue = acquireBoolean(System.getProperties(), systemKey, false);
            value |= systemValue;
        }

        return value;
    }

    public static int acquireInt(Properties properties, String key, int defaultValue)
    {
        int value = defaultValue;

        try
        {
            String text = properties.getProperty(key);
            if (text != null) {
                value = Integer.parseInt(text, 10);
            }
        } catch (Exception ex) {
            // swallow
        }

        return value;
    }

    public static boolean acquireBoolean(Properties properties, String key, boolean defaultValue)
    {
        boolean value = defaultValue;

        try
        {
            String text = properties.getProperty(key);
            if (text != null)
            {
                value = Boolean.parseBoolean(text);
            }
        } catch (Exception ex) {
            // swallow
        }

        return value;
    }
}