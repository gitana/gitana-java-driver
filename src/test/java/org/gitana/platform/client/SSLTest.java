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

import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class SSLTest extends AbstractTestCase
{
    @Test
    public void test()
    {
        // connect to the "demo" account on cloudcms in order to establish that SSL works
        String environmentId = "cloudcms";
        String clientId = "676e3450-6131-46c2-99cc-496aa2ad80fa";
        String clientSecret = "5fGkvesH/tWEMX6SpevL54rY6iJK5ADzLH963sif2ljrWvFOhV2zXv6rSpLF2uMWlJ9SG0uEO9uQO4JZac0i7DZquA/5W8ixJwhj76g0Ksk=";

        Gitana gitana = new Gitana(environmentId, clientId, clientSecret);
        Platform platform = gitana.authenticate("demo", "demo");
        ResultMap<Repository> repositories = platform.listRepositories();

        assertTrue("SSL Authentication passed", (repositories.size() >= 0));
    }
}
