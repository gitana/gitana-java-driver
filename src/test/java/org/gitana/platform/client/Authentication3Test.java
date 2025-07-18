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

import org.gitana.http.OAuth2HttpMethodExecutor;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.RemoteImpl;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * Tests out the scenario where the access token expires on the server.
 * Both the access token and refresh token are expired.
 *
 * A new access token is acquired by the username/password flow.
 *
 * @author uzi
 */
public class Authentication3Test extends AbstractTestCase
{
    @Test
    public void testAutoRefresh()
    {
        // loads the test client (via gitana.properties in test)
        Gitana gitana = new Gitana();

        // tests authentication via authentication grant (custom credentials)
        String credentialsUsername = "db7f7538-bc53-410a-a347-f3ebff4b6b59";
        String credentialsPassword = "q8kUSxe+Nr7KF8A2yGYLibrqVcXcB6bktKHNzUGNiT6Gku1rklH0Djt7hsbzhk459IQ7XoW46BxVDfLSYgWo9yhxJUrZNsQG61noPiW3ovY=";
        Platform platform = gitana.authenticate(credentialsUsername, credentialsPassword);

        // list domains
        ResultMap<Domain> domains = platform.listDomains();
        assertNotNull(domains);
        assertTrue(domains.size() >= 0);

        // now wipe out the access token
        DriverContext.getDriver().expire(true);
        OAuth2HttpMethodExecutor executor = ((OAuth2HttpMethodExecutor) ((RemoteImpl) DriverContext.getDriver().getRemote()).getHttpMethodExecutor());
        String accessToken1 = executor.getAccessToken();
        String refreshToken1 = executor.getRefreshToken();
        executor.invalidateAccessToken();

        // list domains again
        // the existing access token is expired and the refresh token is invalid
        // as such, with username/password flow, we should acquire a NEW access token and refresh token
        Exception ex1 = null;
        try
        {
            domains = platform.listDomains();
        }
        catch (Exception ex)
        {
            ex1 = ex;
        }
        assertNull(ex1);

        assertTrue(domains.size() > 0);

        String accessToken2 = executor.getAccessToken();
        String refreshToken2 = executor.getRefreshToken();

        assertTrue(!accessToken2.equals(accessToken1));
        assertTrue(!refreshToken2.equals(refreshToken1));
    }
}
