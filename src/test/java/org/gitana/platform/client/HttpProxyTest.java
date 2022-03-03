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
 *   info@cloudcms.com
 */
package org.gitana.platform.client;

import junit.framework.TestCase;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.util.TestConstants;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author uzi
 */
@Ignore
public class HttpProxyTest extends TestCase
{
    @Test
    public void test()
        throws Exception
    {
        // set up proxy details
        System.setProperty("https.proxyHost", "108.59.10.141");
        System.setProperty("https.proxyPort", "55555");

        Gitana gitana = new Gitana("cloudcms", TestConstants.TEST_CLOUDCMS_CLIENT_KEY, TestConstants.TEST_CLOUDCMS_CLIENT_SECRET);
        Platform platform = gitana.authenticate(TestConstants.TEST_CLOUDCMS_AUTHGRANT_KEY, TestConstants.TEST_CLOUDCMS_AUTHGRANT_SECRET);
        assertNotNull(platform);
    }
}
