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
import org.junit.Test;

/**
 * @author uzi
 */
public class ConnectionTest extends AbstractTestCase
{
    @Test
    public void testConnection()
    {
        Gitana gitana = new Gitana();

        // authenticate as admin/admin, ensure no errors
        Platform platform = null;
        Exception ex1 = null;
        try
        {
            platform = gitana.authenticate("admin", "admin");
        }
        catch (Exception ex)
        {
            ex1 = ex;
        }
        assertNull(ex1);

        assertNotNull(platform);

        // now try to authenticate as some jacked user and ensure it throws
        Platform platform2 = null;
        Exception ex2  = null;
        try
        {
            platform2 = gitana.authenticate("rivers", "cuomo");
        }
        catch (Exception ex)
        {
            ex2 = ex;
        }
        assertNotNull(ex2);

        assertNull(platform2);
    }
}
