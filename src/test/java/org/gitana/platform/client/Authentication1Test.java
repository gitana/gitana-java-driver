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

import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.junit.Test;

/**
 * @author uzi
 */
public class Authentication1Test extends AbstractTestCase
{
    @Test
    public void testUsernamePasswordAuthentication()
    {
        // loads the test client (via gitana.properties in test)
        Gitana gitana = new Gitana();

        // tests authentication via username/password
        Platform platform = gitana.authenticate("admin", "admin");
        Repository repository = platform.createRepository();
        assertNotNull(repository);
    }

    @Test
    public void testCredentialsAuthentication()
    {
        // loads the test client (via gitana.properties in test)
        Gitana gitana = new Gitana();

        // tests authentication via authentication grant (custom credentials)
        String credentialsUsername = "db7f7538-bc53-410a-a347-f3ebff4b6b59";
        String credentialsPassword = "q8kUSxe+Nr7KF8A2yGYLibrqVcXcB6bktKHNzUGNiT6Gku1rklH0Djt7hsbzhk459IQ7XoW46BxVDfLSYgWo9yhxJUrZNsQG61noPiW3ovY=";
        Platform platform = gitana.authenticate(credentialsUsername, credentialsPassword);
        Repository repository = platform.createRepository();
        assertNotNull(repository);
    }
}
