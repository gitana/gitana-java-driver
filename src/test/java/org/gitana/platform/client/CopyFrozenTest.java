/**
 * Copyright (C) 2021 Gitana Software, Inc.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Gitana Software, Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Gitana Software, Inc. and its suppliers
 * and may be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 *
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Gitana Software, Inc.
 */
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.project.Project;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.transfer.CopyJob;
import org.gitana.platform.services.job.JobState;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author mwhitman
 */
public class CopyFrozenTest extends AbstractTestCase
{
    @Test
    public void testCopyFrozen() throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate as "admin"
        Platform platform = gitana.authenticate("admin", "admin");


//        // create a tenant for this user
//        Tenant tenant = platform.readRegistrar("default").createTenant(user, "unlimited");
//        ObjectNode defaultClientObject = tenant.readDefaultAllocatedClientObject();
//        String clientKey = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_KEY);
//        String clientSecret = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_SECRET);

        Project project1 = platform.createProject(QueryBuilder.start("title").is("Test Project " + System.currentTimeMillis()).get());
        Repository repository = (Repository) project1.getStack().readDataStore("content");
        JsonUtil.setByPath(repository.getObject(), "releases/blockMaster", JsonUtil.toJsonNode(true), false);
        repository.update();

//        // AUTHENTICATE AS THE TENANT USER
//        gitana = new Gitana(clientKey, clientSecret);
//        platform = gitana.authenticate(user.getName(), TestConstants.TEST_PASSWORD);


        /////////////////////////////////////////////////////////////////////////////

        Branch master = repository.readBranch("master");
        Branch branch = repository.createBranch(master.getId(), master.getTipChangesetId(), JsonUtil.createObject());

        BaseNode myNode = branch.createNode(JSONBuilder.start("title").is("copy time").get());

        Job job1 = null;
        List<String> nodeIds = Arrays.asList(myNode.getId());
        job1 = master.startCopyFrom(branch.getRepositoryId(), branch.getId(), nodeIds, JsonUtil.createObject());
        job1.waitForCompletion();
//        try
//        {
//            job1 = master.startCopyFrom(branch.getRepositoryId(), branch.getId(), nodeIds, JsonUtil.createObject());
//            job1.waitForCompletion();
//        }
//        catch (Exception ex)
//        {
//
//        }

//        job1 = job1.getCluster().readJob(job1.getId());
        assertEquals(JobState.ERROR, job1.getState());


        Job job2 = master.startCopyFrom(branch.getRepositoryId(), branch.getId(), nodeIds, JSONBuilder.start("allowWriteToFrozenBranches").is(true).get());
        job2.waitForCompletion();


        assertEquals(JobState.FINISHED, job2.getState());

        BaseNode myNodeMaster = master.readNode(myNode.getId());
        assertNotNull(myNodeMaster);
    }
}
