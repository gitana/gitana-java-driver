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

package org.gitana.platform.client.transfer;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.job.JobImpl;
import org.gitana.platform.services.reference.Reference;
import org.gitana.platform.services.transfer.TransferDependency;
import org.gitana.platform.services.transfer.TransferDependencyChain;
import org.gitana.platform.services.transfer.TransferImport;
import org.gitana.platform.services.transfer.TransferImportConfiguration;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public class TransferImportJob extends JobImpl
{
    // manifest properties
    public final static String FIELD_ARCHIVE_GROUP = "archiveGroup";
    public final static String FIELD_ARCHIVE_ARTIFACT = "archiveArtifact";
    public final static String FIELD_ARCHIVE_VERSION = "archiveVersion";

    // vault
    public final static String FIELD_VAULT_ID = "vaultId";

    // configuration
    public final static String FIELD_CONFIGURATION = "configuration";

    // target element
    public final static String FIELD_TARGETS = "targets";

    // imports element
    public final static String FIELD_IMPORTS = "imports";

    public TransferImportJob(Cluster cluster, ObjectNode obj, boolean isSaved)
    {
        super(cluster, obj, isSaved);
    }

    public String getArchiveGroup()
    {
        return getString(FIELD_ARCHIVE_GROUP);
    }

    public String getArchiveArtifact()
    {
        return getString(FIELD_ARCHIVE_ARTIFACT);
    }

    public String getArchiveVersion()
    {
        return getString(FIELD_ARCHIVE_VERSION);
    }

    public String getVaultId()
    {
        return getString(FIELD_VAULT_ID);
    }

    public TransferImportConfiguration getConfiguration()
    {
        return new TransferImportConfiguration(getObject(FIELD_CONFIGURATION));
    }

    public List<TransferDependency> getTargets()
    {
        List<TransferDependency> targets = new ArrayList<TransferDependency>();

        ArrayNode array = getArray(FIELD_TARGETS);
        for (int i = 0; i < array.size(); i++)
        {
            ObjectNode object = (ObjectNode) array.get(i);

            String typeId = JsonUtil.objectGetString(object, "typeId");
            String id = JsonUtil.objectGetString(object, "id");
            String ref = JsonUtil.objectGetString(object, "ref");

            Reference reference = null;
            if (ref != null)
            {
                reference = Reference.create(ref);
            }

            TransferDependency dependency = new TransferDependency(typeId, id, reference);
            targets.add(dependency);
        }

        return targets;
    }

    public List<TransferImport> getImports()
    {
        List<TransferImport> imports = new ArrayList<TransferImport>();

        ArrayNode array = getArray(FIELD_IMPORTS);
        for (int i = 0; i < array.size(); i++)
        {
            ObjectNode object = (ObjectNode) array.get(i);

            TransferDependencyChain sources = TransferDependencyChain.create((ArrayNode) object.get("sources"));
            TransferDependencyChain targets = TransferDependencyChain.create((ArrayNode) object.get("targets"));

            TransferImport transferImport = new TransferImport(sources, targets);
            imports.add(transferImport);
        }

        return imports;
    }

    public TransferDependencyChain getImportSources(String sourceId)
    {
        TransferDependencyChain chain = null;

        List<TransferImport> transferImports = getImports();
        for (TransferImport transferImport: transferImports)
        {
            if (transferImport.getSourceId().equals(sourceId))
            {
                chain = transferImport.getSources();
                break;
            }
        }

        return chain;
    }

    public TransferDependencyChain getImportTargets(String targetId)
    {
        TransferDependencyChain chain = null;

        List<TransferImport> transferImports = getImports();
        for (TransferImport transferImport: transferImports)
        {
            if (transferImport.getTargetId().equals(targetId))
            {
                chain = transferImport.getTargets();
                break;
            }
        }

        return chain;
    }

    public String getSingleImportTargetId()
    {
        String targetId = null;

        List<TransferImport> imports = getImports();
        if (imports != null && imports.size() > 0)
        {
            targetId = getImports().get(0).getTargetId();
        }

        return targetId;
    }
}
