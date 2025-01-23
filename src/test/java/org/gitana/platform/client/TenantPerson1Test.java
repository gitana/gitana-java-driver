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
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.type.Person;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.project.Project;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class TenantPerson1Test extends AbstractTestCase
{
    @Test
    public void test()
    {
        long now1 = System.currentTimeMillis();

        /////////////////////////////////////////////////////////////////////////////////////////////
        //
        // authenticate as ADMIN
        //

        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate("admin", "admin");

        // create a user on default domain
        DomainUser defaultUser = platform.readDomain("default").createUser("testuser-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);

        // create a new tenant
        Registrar registrar = platform.readRegistrar("default");
        Tenant tenant1 = registrar.createTenant(defaultUser, "unlimited");

        /////////////////////////////////////////////////////////////////////////////////////////////
        //
        // authenticate as defaultUser (to tenant1)
        //

        // now sign into the tenant
        ObjectNode defaultClientObject1 = tenant1.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject1);
        String clientKey1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_KEY);
        String clientSecret1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_SECRET);
        gitana = new Gitana(clientKey1, clientSecret1);
        platform = gitana.authenticateOnTenant(defaultUser, TestConstants.TEST_PASSWORD, tenant1.getId());

        // we are now authenticated as "defaultUser"

        // on the tenant domain, create three users
        Domain tenantPrimaryDomain = platform.readDomain("primary");

        // create user #1
        DomainUser user1 = tenantPrimaryDomain.createUser("user1-" + now1, TestConstants.TEST_PASSWORD);
        // create user #2
        DomainUser user2 = tenantPrimaryDomain.createUser("user2-" + now1, TestConstants.TEST_PASSWORD);
        // create user #3
        DomainUser user3 = tenantPrimaryDomain.createUser("user3-" + now1, TestConstants.TEST_PASSWORD);

        // create a new project
        ObjectNode projectObject = JsonUtil.createObject();
        projectObject.put("title", "project-" + now1);
        Project project = platform.createProject(projectObject);
        Stack stack = project.getStack();

        // project repository and domain
        Repository projectRepository = (Repository) stack.readDataStore("content");
        Branch masterBranch = projectRepository.readBranch("master");
        //Domain projectDomain = (Domain) stack.readDataStore("principals");

        // list users in the primary domain
        ResultMap<DomainUser> users = tenantPrimaryDomain.listUsers();
        for (DomainUser user: users.values())
        {
            Person person = masterBranch.readPerson(user.getDomainQualifiedId(), true);
            if (person == null)
            {
                System.out.println("Failed to find person for user: " + user.getDomainQualifiedId());
            }
            else
            {
                System.out.println("Found person: " + JsonUtil.stringify(person.getObject(), true));
            }
        }
    }
}