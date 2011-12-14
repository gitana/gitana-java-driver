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

package org.gitana.platform.client;

import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.permission.PermissionCheck;
import org.gitana.platform.client.permission.PermissionCheckResults;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.repository.Repository;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public class RepositoryPermissionTest extends AbstractTestCase
{
    private String repositoryId1 = null;
    private String repositoryId2 = null;
    private String repositoryId3 = null;

    private String userId1 = null;
    private String userId2 = null;
    private String userId3 = null;

    private String userTestId = null;

    private void setupBulkPermissions()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Repository repository1 = platform.createRepository();
        Repository repository2 = platform.createRepository();
        Repository repository3 = platform.createRepository();

        // default domain
        Domain domain = platform.readDefaultDomain();

        DomainUser user1 = domain.createUser("user1-" + System.currentTimeMillis(), "password");
        DomainUser user2 = domain.createUser("user2-" + System.currentTimeMillis(), "password");
        DomainUser user3 = domain.createUser("user3-" + System.currentTimeMillis(), "password");
        DomainUser userTest = domain.createUser("usertest-" + System.currentTimeMillis(), "password");

        repository1.grant(user1.getName(), "MANAGER");
        repository1.grant(user2.getName(), "CONSUMER");
        repository2.grant(user2.getName(), "EDITOR");
        repository3.grant(user3.getName(), "CONSUMER");

        this.repositoryId1 = repository1.getId();
        this.repositoryId2 = repository2.getId();
        this.repositoryId3 = repository3.getId();
        this.userId1 = user1.getName();
        this.userId2 = user2.getName();
        this.userId3 = user3.getName();
        this.userTestId = userTest.getName();

        // simple checks
        assertTrue(repository1.hasPermission(user1.getName(), "READ"));
        assertTrue(repository1.hasPermission(user1.getName(), "UPDATE"));
        assertTrue(repository1.hasPermission(user1.getName(), "DELETE"));
        assertTrue(repository1.hasPermission(user2.getName(), "READ"));
        assertFalse(repository1.hasPermission(user2.getName(), "UPDATE"));
        assertFalse(repository1.hasPermission(user2.getName(), "DELETE"));
        assertFalse(repository1.hasPermission(user3.getName(), "READ"));
        assertFalse(repository1.hasPermission(user3.getName(), "UPDATE"));
        assertFalse(repository1.hasPermission(user3.getName(), "DELETE"));

    }

    @Test
    public void testBulkPermissions()
    {
        setupBulkPermissions();

        // authenticate as the test user
        // this is to ensure that a non-admin can check permissions
        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate(userTestId, "password");

        // define a bunch of permissions to check
        List<PermissionCheck> checks = new ArrayList<PermissionCheck>();
        checks.add(new PermissionCheck(repositoryId1, userId1, "READ"));
        checks.add(new PermissionCheck(repositoryId1, userId1, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId1, userId1, "DELETE"));
        checks.add(new PermissionCheck(repositoryId1, userId2, "READ"));
        checks.add(new PermissionCheck(repositoryId1, userId2, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId1, userId2, "DELETE"));
        checks.add(new PermissionCheck(repositoryId1, userId3, "READ"));
        checks.add(new PermissionCheck(repositoryId1, userId3, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId1, userId3, "DELETE"));
        checks.add(new PermissionCheck(repositoryId2, userId1, "READ"));
        checks.add(new PermissionCheck(repositoryId2, userId1, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId2, userId1, "DELETE"));
        checks.add(new PermissionCheck(repositoryId2, userId2, "READ"));
        checks.add(new PermissionCheck(repositoryId2, userId2, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId2, userId2, "DELETE"));
        checks.add(new PermissionCheck(repositoryId2, userId3, "READ"));
        checks.add(new PermissionCheck(repositoryId2, userId3, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId2, userId3, "DELETE"));
        checks.add(new PermissionCheck(repositoryId3, userId1, "READ"));
        checks.add(new PermissionCheck(repositoryId3, userId1, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId3, userId1, "DELETE"));
        checks.add(new PermissionCheck(repositoryId3, userId2, "READ"));
        checks.add(new PermissionCheck(repositoryId3, userId2, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId3, userId2, "DELETE"));
        checks.add(new PermissionCheck(repositoryId3, userId3, "READ"));
        checks.add(new PermissionCheck(repositoryId3, userId3, "UPDATE"));
        checks.add(new PermissionCheck(repositoryId3, userId3, "DELETE"));

        // check
        PermissionCheckResults results = platform.checkRepositoryPermissions(checks);

        // assert (repo1)
        assertTrue(results.check(repositoryId1, userId1, "READ"));
        assertTrue(results.check(repositoryId1, userId1, "UPDATE"));
        assertTrue(results.check(repositoryId1, userId1, "DELETE"));
        assertTrue(results.check(repositoryId1, userId2, "READ"));
        assertFalse(results.check(repositoryId1, userId2, "UPDATE"));
        assertFalse(results.check(repositoryId1, userId2, "DELETE"));
        assertFalse(results.check(repositoryId1, userId3, "READ"));
        assertFalse(results.check(repositoryId1, userId3, "UPDATE"));
        assertFalse(results.check(repositoryId1, userId3, "DELETE"));

        // assert (repo2)
        assertFalse(results.check(repositoryId2, userId1, "READ"));
        assertFalse(results.check(repositoryId2, userId1, "UPDATE"));
        assertFalse(results.check(repositoryId2, userId1, "DELETE"));
        assertTrue(results.check(repositoryId2, userId2, "READ"));
        assertTrue(results.check(repositoryId2, userId2, "UPDATE"));
        assertTrue(results.check(repositoryId2, userId2, "DELETE"));
        assertFalse(results.check(repositoryId2, userId3, "READ"));
        assertFalse(results.check(repositoryId2, userId3, "UPDATE"));
        assertFalse(results.check(repositoryId2, userId3, "DELETE"));

        // assert (repo3)
        assertFalse(results.check(repositoryId3, userId1, "READ"));
        assertFalse(results.check(repositoryId3, userId1, "UPDATE"));
        assertFalse(results.check(repositoryId3, userId1, "DELETE"));
        assertFalse(results.check(repositoryId3, userId2, "READ"));
        assertFalse(results.check(repositoryId3, userId2, "UPDATE"));
        assertFalse(results.check(repositoryId3, userId2, "DELETE"));
        assertTrue(results.check(repositoryId3, userId3, "READ"));
        assertFalse(results.check(repositoryId3, userId3, "UPDATE"));
        assertFalse(results.check(repositoryId3, userId3, "DELETE"));
    }
}
