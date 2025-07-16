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

import org.gitana.platform.client.support.DriverContext;
import org.junit.Test;

/**
 * @author uzi
 */
public class AuthInfoTest extends AbstractTestCase
{
    @Test
    public void testAuthInfo()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // check driver properties
        AuthInfo authInfo = DriverContext.getDriver().getAuthInfo();

        // some things about the principal
        assertEquals("default", authInfo.getPrincipalDomainId());
        assertEquals("admin", authInfo.getPrincipalName());

        // client key
        assertEquals("eb9bcf0b-050d-4931-a11b-2231be0fd168", authInfo.getClientId());

        // assert we have a tenant id
        assertNotNull(authInfo.getTenantId());
    }
}
