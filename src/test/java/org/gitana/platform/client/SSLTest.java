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

package org.gitana.platform.client;

import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.ResultMap;
import org.gitana.platform.util.TestConstants;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author uzi
 */
@Ignore
public class SSLTest extends AbstractTestCase
{
    @Test
    public void test()
    {
        // connect to the "demo" account on cloudcms in order to establish that SSL works
        String environmentId = "cloudcms";
        String clientId = TestConstants.TEST_CLOUDCMS_CLIENT_KEY;
        String clientSecret = TestConstants.TEST_CLOUDCMS_CLIENT_SECRET;

        Gitana gitana = new Gitana(environmentId, clientId, clientSecret);
        Platform platform = gitana.authenticate(TestConstants.TEST_CLOUDCMS_AUTHGRANT_KEY, TestConstants.TEST_CLOUDCMS_AUTHGRANT_SECRET);
        ResultMap<Repository> repositories = platform.listRepositories();

        assertTrue("SSL Authentication passed", (repositories.size() >= 0));
    }
}
