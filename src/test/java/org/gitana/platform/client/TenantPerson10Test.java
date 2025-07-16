/**
 * Copyright 2025 Gitana Software, Inc.
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
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.project.Project;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.services.principals.PrincipalType;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.List;

/**
 * @author uzi
 */
public class TenantPerson10Test extends AbstractTestCase
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
        DomainUser user1 =tenantPrimaryDomain.createUser("user1-" + now1, TestConstants.TEST_PASSWORD);
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

        // invite user 1,2,3 to the project
        project.inviteUser(user1.getId());
        project.inviteUser(user2.getId());
        project.inviteUser(user3.getId());

        // test register a new user
        register(tenantPrimaryDomain, project, "r1_name", "r1_firstName", "r1_lastName", "r1_email@test.com", "r1_company", TestConstants.TEST_PASSWORD);
    }

    private void register(Domain primaryDomain, Project project, String name, String firstName, String lastName, String email, String companyName, String password)
    {
        var data = JsonUtil.createObject();
        data.put(DomainUser.FIELD_NAME, name);
        data.put(DomainUser.FIELD_EMAIL, email);
        data.put("password", password);

        if (firstName != null) {
            data.put(DomainUser.FIELD_FIRST_NAME, firstName);
        }

        if (lastName != null) {
            data.put(DomainUser.FIELD_LAST_NAME, lastName);
        }

        if (companyName != null) {
            data.put(DomainUser.FIELD_COMPANY_NAME, companyName);
        }

        DomainUser domainUser = (DomainUser) primaryDomain.createPrincipal(PrincipalType.USER, data);
        String userId = domainUser.getId();

        // invite user to project
        project.inviteUser(userId);

        // remove user from all primary domain groups?
//        for (DomainGroup group : primaryDomain.listMemberships(domainUser).values()) {
//            group.removePrincipal(domainUser);
//        }

        //

        // read back team titles
        List<String> teams = primaryDomain
            .listMemberships(domainUser)
            .values()
            .stream()
            .map(DomainPrincipal::getTitle)
            .toList();

        System.out.println("Created user with id: " + userId + " and teams: " + teams);
    }
}