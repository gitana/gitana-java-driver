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

package org.gitana.repo.client;

import org.gitana.repo.client.support.PermissionCheck;
import org.gitana.repo.client.support.PermissionCheckResults;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uzi
 */
public class RepositoryPermissionTest extends AbstractTestCase
{
    @Test
    public void testBulkPermissions()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        Repository repository1 = server.createRepository();
        Repository repository2 = server.createRepository();
        Repository repository3 = server.createRepository();

        SecurityUser user1 = server.createUser("user1-" + System.currentTimeMillis(), "password");
        SecurityUser user2 = server.createUser("user2-" + System.currentTimeMillis(), "password");
        SecurityUser user3 = server.createUser("user3-" + System.currentTimeMillis(), "password");

        repository1.grant(user1.getName(), "MANAGER");
        repository1.grant(user2.getName(), "CONSUMER");
        repository2.grant(user2.getName(), "EDITOR");
        repository3.grant(user3.getName(), "CONSUMER");

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

        // define a bunch of permissions to check
        List<PermissionCheck> checks = new ArrayList<PermissionCheck>();
        checks.add(new PermissionCheck(repository1.getId(), user1.getName(), "READ"));
        checks.add(new PermissionCheck(repository1.getId(), user1.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository1.getId(), user1.getName(), "DELETE"));
        checks.add(new PermissionCheck(repository1.getId(), user2.getName(), "READ"));
        checks.add(new PermissionCheck(repository1.getId(), user2.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository1.getId(), user2.getName(), "DELETE"));
        checks.add(new PermissionCheck(repository1.getId(), user3.getName(), "READ"));
        checks.add(new PermissionCheck(repository1.getId(), user3.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository1.getId(), user3.getName(), "DELETE"));
        checks.add(new PermissionCheck(repository2.getId(), user1.getName(), "READ"));
        checks.add(new PermissionCheck(repository2.getId(), user1.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository2.getId(), user1.getName(), "DELETE"));
        checks.add(new PermissionCheck(repository2.getId(), user2.getName(), "READ"));
        checks.add(new PermissionCheck(repository2.getId(), user2.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository2.getId(), user2.getName(), "DELETE"));
        checks.add(new PermissionCheck(repository2.getId(), user3.getName(), "READ"));
        checks.add(new PermissionCheck(repository2.getId(), user3.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository2.getId(), user3.getName(), "DELETE"));
        checks.add(new PermissionCheck(repository3.getId(), user1.getName(), "READ"));
        checks.add(new PermissionCheck(repository3.getId(), user1.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository3.getId(), user1.getName(), "DELETE"));
        checks.add(new PermissionCheck(repository3.getId(), user2.getName(), "READ"));
        checks.add(new PermissionCheck(repository3.getId(), user2.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository3.getId(), user2.getName(), "DELETE"));
        checks.add(new PermissionCheck(repository3.getId(), user3.getName(), "READ"));
        checks.add(new PermissionCheck(repository3.getId(), user3.getName(), "UPDATE"));
        checks.add(new PermissionCheck(repository3.getId(), user3.getName(), "DELETE"));

        // check
        PermissionCheckResults results = server.checkRepositoryPermissions(checks);

        // assert (repo1)
        assertTrue(results.check(repository1.getId(), user1.getName(), "READ"));
        assertTrue(results.check(repository1.getId(), user1.getName(), "UPDATE"));
        assertTrue(results.check(repository1.getId(), user1.getName(), "DELETE"));
        assertTrue(results.check(repository1.getId(), user2.getName(), "READ"));
        assertFalse(results.check(repository1.getId(), user2.getName(), "UPDATE"));
        assertFalse(results.check(repository1.getId(), user2.getName(), "DELETE"));
        assertFalse(results.check(repository1.getId(), user3.getName(), "READ"));
        assertFalse(results.check(repository1.getId(), user3.getName(), "UPDATE"));
        assertFalse(results.check(repository1.getId(), user3.getName(), "DELETE"));

        // assert (repo2)
        assertFalse(results.check(repository2.getId(), user1.getName(), "READ"));
        assertFalse(results.check(repository2.getId(), user1.getName(), "UPDATE"));
        assertFalse(results.check(repository2.getId(), user1.getName(), "DELETE"));
        assertTrue(results.check(repository2.getId(), user2.getName(), "READ"));
        assertTrue(results.check(repository2.getId(), user2.getName(), "UPDATE"));
        assertTrue(results.check(repository2.getId(), user2.getName(), "DELETE"));
        assertFalse(results.check(repository2.getId(), user3.getName(), "READ"));
        assertFalse(results.check(repository2.getId(), user3.getName(), "UPDATE"));
        assertFalse(results.check(repository2.getId(), user3.getName(), "DELETE"));

        // assert (repo3)
        assertFalse(results.check(repository3.getId(), user1.getName(), "READ"));
        assertFalse(results.check(repository3.getId(), user1.getName(), "UPDATE"));
        assertFalse(results.check(repository3.getId(), user1.getName(), "DELETE"));
        assertFalse(results.check(repository3.getId(), user2.getName(), "READ"));
        assertFalse(results.check(repository3.getId(), user2.getName(), "UPDATE"));
        assertFalse(results.check(repository3.getId(), user2.getName(), "DELETE"));
        assertTrue(results.check(repository3.getId(), user3.getName(), "READ"));
        assertFalse(results.check(repository3.getId(), user3.getName(), "UPDATE"));
        assertFalse(results.check(repository3.getId(), user3.getName(), "DELETE"));
    }
}
