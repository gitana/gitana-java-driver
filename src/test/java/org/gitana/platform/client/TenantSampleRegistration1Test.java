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
import org.gitana.JSONBuilder;
import org.gitana.platform.client.accesspolicy.AccessPolicy;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.project.Project;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.team.Team;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.services.principals.PrincipalType;
import org.gitana.platform.services.reference.Reference;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QName;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests out a registration scenario for a user.
 *
 * @author uzi
 */
public class TenantSampleRegistration1Test extends AbstractTestCase
{
    private static QName QNAME_A_CHILD = QName.create("a:child");

    @Test
    public void test()
    {
        long now1 = System.currentTimeMillis();

        // log in as admin and create a new tenant
        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate("admin", "admin");

        // create the tenant owner user
        DomainUser tenantOwnerUser = platform.readDomain("default").createUser("testuser-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);

        // create a new tenant
        Registrar registrar = platform.readRegistrar("default");
        Tenant tenant = registrar.createTenant(tenantOwnerUser, "unlimited");

        // authenticate to the tenant as the owner
        ObjectNode defaultClientObject1 = tenant.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject1);
        String clientKey1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_KEY);
        String clientSecret1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_SECRET);
        gitana = new Gitana(clientKey1, clientSecret1);
        platform = gitana.authenticateOnTenant(tenantOwnerUser, TestConstants.TEST_PASSWORD, tenant.getId());

        // we are now authenticated as "ownerUser"

        // retain the authenticated driver (as owner user)
        Driver ownerUserDriver = DriverContext.getDriver();





        // create a new project and set up the project to two teams: "pending-users-team" and "registered-users-team"
        // set up pending-users-team to use an access policy that restricts access

        // the primary domain of the new tenant
        Domain tenantPrimaryDomain = platform.readDomain("primary");

        // create a new project
        ObjectNode projectObject = JsonUtil.createObject();
        projectObject.put("title", "project-" + now1);
        Project project = platform.createProject(projectObject);
        Stack stack = project.getStack();

        // now let's set up policies for the new project

        // when users are invited to join the project, they are automatically added to the "project-users-team" team
        Team projectUsersTeam = stack.readTeam("project-users-team");
        // this team has 2 access policies
        // these are both system defined policies - "project-user" and "project-connector"
        Map<String, AccessPolicy> projectUsersTeamAccessPolicies = projectUsersTeam.getAccessPolicies();
        assertEquals(2, projectUsersTeamAccessPolicies.size());

        // let's create a "my-pending-user" policy that limits rights for pending users
        AccessPolicy myProjectPendingUsersPolicy = defineMyProjectPendingUserAccessPolicy(platform);

        // now create a "pending-users-team" team and have it use the "my-pending-user" policy
        Team pendingUsersTeam = stack.createTeam("pending-users-team");
        pendingUsersTeam.assignPolicy(myProjectPendingUsersPolicy.getId());

        // now create a "registered-users-team" team
        // this is simply to keep track of which users completed the registration process
        stack.createTeam("registered-users-team");




        // now let's create content for the new project

        // get the repository and master branch for the new project
        Repository projectRepository = (Repository) stack.readDataStore("content");
        Branch master = projectRepository.readBranch("master");

        /**
         *    /
         *    /welcome
         *    /welcome/welcome.md
         *    /registered
         *    /registered/registered.md
         */
        Node root = master.rootNode();
        Node welcomeFolder = createFolder(master, root, "welcome");
        Node welcomeFile = createFile(master, welcomeFolder, "welcome.md");
        Node registeredFolder = createFolder(master, root, "registered");
        Node registeredFile = createFile(master, registeredFolder, "registered.md");






        // now let's register a new user
        // this user is automatically added to the "pending-users" team
        // this restricts them to only seeing the "/welcome" folder
        DomainUser user1 = createPendingUser(tenantPrimaryDomain, project, "joesmith", "joe", "smith", "joesmith@test.com", "test", TestConstants.TEST_PASSWORD);

        // check the access policies that describe user1's access to welcomeFolder and registeredFolder
        Map<Reference, List<AccessPolicy>> refPoliciesMap1 = platform.collectAccessPolicies(Set.of(welcomeFolder.ref(), registeredFolder.ref()), user1.getDomainQualifiedId());
        List<AccessPolicy> welcomePolicies1 = refPoliciesMap1.get(welcomeFolder.ref());
        assertEquals(4, welcomePolicies1.size()); // 2 default policies + 1 inline policy relating back to root node + pending-user
        List<AccessPolicy> registeredPolicies1 = refPoliciesMap1.get(registeredFolder.ref());
        assertEquals(4, registeredPolicies1.size()); // 2 default policies + 1 inline policy relating back to root node + pending-user

        // verify that "user1" can only access content in welcome folder
        gitana = new Gitana(clientKey1, clientSecret1);
        gitana.authenticate(user1.getName(), TestConstants.TEST_PASSWORD);
        Driver user1Driver = DriverContext.getDriver();
        ResultMap<BaseNode> nodes1 = master.queryNodes(JsonUtil.createObject(), Pagination.limit(1000));
        assertNotNull(nodes1.get(welcomeFolder.getId()));
        assertNotNull(nodes1.get(welcomeFile.getId()));
        assertNull(nodes1.get(registeredFolder.getId()));
        assertNull(nodes1.get(registeredFile.getId()));

        // now as the owner, complete the user's registration
        DriverContext.setDriver(ownerUserDriver);
        completeUserRegistration(project, tenantPrimaryDomain, user1.getName());

        // again - check the access policies that describe user1's access to welcomeFolder and registeredFolder
        Map<Reference, List<AccessPolicy>> refPoliciesMap2 = platform.collectAccessPolicies(Set.of(welcomeFolder.ref(), registeredFolder.ref()), user1.getDomainQualifiedId());
        List<AccessPolicy> welcomePolicies2 = refPoliciesMap2.get(welcomeFolder.ref());
        assertEquals(3, welcomePolicies2.size()); // 2 default policies + 1 inline policy relating back to root node
        List<AccessPolicy> registeredPolicies2 = refPoliciesMap2.get(registeredFolder.ref());
        assertEquals(3, registeredPolicies2.size()); // 2 default policies + 1 inline policy relating back to root node

        // verify that "user1" can now see everything
        DriverContext.setDriver(user1Driver);
        ResultMap<BaseNode> nodes2 = master.queryNodes(JsonUtil.createObject(), Pagination.limit(1000));
        assertNotNull(nodes2.get(welcomeFolder.getId()));
        assertNotNull(nodes2.get(welcomeFile.getId()));
        assertNotNull(nodes2.get(registeredFolder.getId()));
        assertNotNull(nodes2.get(registeredFile.getId()));
    }

    private DomainUser createPendingUser(Domain primaryDomain, Project project, String name, String firstName, String lastName, String email, String companyName, String password)
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

        DomainUser primaryDomainUser = (DomainUser) primaryDomain.createPrincipal(PrincipalType.USER, data);

        // invite user to project
        project.inviteUser(primaryDomainUser.getDomainQualifiedId());

        // add user to pending user team
        Team pendingUsersTeam = project.getStack().readTeam("pending-users-team");
        pendingUsersTeam.addMember(primaryDomainUser.getDomainQualifiedId());

        return primaryDomainUser;
    }

    private void completeUserRegistration(Project project, Domain primaryDomain, String name)
    {
        DomainUser primaryDomainUser = (DomainUser) primaryDomain.readPrincipal(name);

        // add user to the "registered-users-team" team
        Team registeredUsersTeam = project.getStack().readTeam("registered-users-team");
        registeredUsersTeam.addMember(primaryDomainUser.getDomainQualifiedId());

        // remove user from the "pending-users-team" team
        Team pendingUsersTeam = project.getStack().readTeam("pending-users-team");
        pendingUsersTeam.removeMember(primaryDomainUser.getDomainQualifiedId());
    }

    /**
     * Define an access policy ("pending-user") that revokes CONSUMER rights from all folder paths except /welcome.*
     *
     * @param platform
     * @return
     */
    public AccessPolicy defineMyProjectPendingUserAccessPolicy(Platform platform)
    {
        String text = "{"
                +     "    'title': 'Pending User',"
                +     "    'order': 100000,"
                +     "    'key': 'pending-user',"
                +     "    'statements': ["
                +     "        {"
                +     "            'description': 'Revoke access to all sub-folders except root',"
                +     "            'roles': ['consumer'],"
                +     "            'action': 'revoke',"
                +     "            'conditions': ["
                +     "                {"
                +     "                    'type': 'path',"
                +     "                    'config': {"
                +     "                        'path': '^/.*'"
                +     "                    }"
                +     "                }"
                +     "            ]"
                +     "        },"
                +     "        {"
                +     "            'description': 'Grant access to welcome folder',"
                +     "            'roles': ['consumer'],"
                +     "            'action': 'grant',"
                +     "            'conditions': ["
                +     "                {"
                +     "                    'type': 'path',"
                +     "                    'config': {"
                +     "                        'path': '^/welcome.*'"
                +     "                    }"
                +     "                }"
                +     "            ]"
                +     "        }"
                +     "    ]"
                +     "}";

        ObjectNode obj = (ObjectNode) JsonUtil.parse(text);

        return platform.createAccessPolicy(obj);
    }

    private Node createFolder(Branch branch, Node parent, String name)
    {
        Node node = (Node) branch.createNode();
        node.addFeature("f:filename", JsonUtil.createObject());
        node.addFeature("f:container", JsonUtil.createObject());
        node.setTitle(name);
        node.update();

        parent.associate(node, QNAME_A_CHILD, Directionality.DIRECTED);

        return node;
    }

    private Node createFile(Branch branch, Node parent, String name)
    {
        Node node = (Node) branch.createNode();
        node.addFeature("f:filename", JSONBuilder.start("filename").is(name).get());

        parent.associate(node, QNAME_A_CHILD, Directionality.DIRECTED);

        return node;
    }

}