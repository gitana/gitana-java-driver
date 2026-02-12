/**
 * Copyright 2026 Gitana Software, Inc.
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
import org.gitana.platform.client.webhost.WebHost;
import org.junit.Test;

/**
 * @author uzi
 */
public class WebHostTest extends AbstractTestCase
{
    @Test
    public void testWebHosts()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // list web hosts
        int baseCount = platform.listWebHosts().size();

        // create three web hosts
        WebHost webhost1 = platform.createWebHost();
        WebHost webhost2 = platform.createWebHost();
        WebHost webhost3 = platform.createWebHost();

        // list web hosts
        assertEquals(baseCount + 3, platform.listWebHosts().size());

        // read one back for assurance
        WebHost check2 = platform.readWebHost(webhost2.getId());
        assertEquals(webhost2, check2);

        // update the third one
        webhost3.update();

        // delete the third one
        webhost3.delete();

        // read back for assurance
        WebHost check3 = platform.readWebHost(webhost3.getId());
        assertNull(check3);

        // list web hosts
        assertEquals(baseCount + 2, platform.listWebHosts().size());
    }

}
