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

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class LatencyTest extends AbstractTestCase
{
    @Test
    public void testLatency()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // run a 300 second delay
        System.out.println("300 second delay");
        Exception ex2 = null;
        HttpResponse response2 = null;
        try
        {
            response2 = platform.test(1000, 300 * 1000, -1);
            EntityUtils.consume(response2.getEntity());
        }
        catch (Exception ex)
        {
            ex2 = ex;
        }
        assertNull(ex2);
        assertEquals(200, response2.getStatusLine().getStatusCode());
    }

}
