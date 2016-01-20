/**
 * Copyright 2016 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.services.job.JobState;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class PrincipalTransferTest extends AbstractTestCase
{
    @Test
    public void test()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate as "admin"
        Platform platform = gitana.authenticate("admin", "admin");

        // create a user on default domain
        DomainUser user = platform.readDomain("default").createUser("testuser-" + System.currentTimeMillis(), TestConstants.TEST_PASSWORD);

        // create a tenant for this user
        Tenant tenant = platform.readRegistrar("default").createTenant(user, "unlimited");
        ObjectNode defaultClientObject = tenant.readDefaultAllocatedClientObject();
        String clientKey = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_KEY);
        String clientSecret = JsonUtil.objectGetString(defaultClientObject, Client.FIELD_SECRET);

        // AUTHENTICATE AS THE TENANT USER
        gitana = new Gitana(clientKey, clientSecret);
        platform = gitana.authenticate(user.getName(), TestConstants.TEST_PASSWORD);


        /////////////////////////////////////////////////////////////////////////////


        // create a domain with some stuff
        Domain domain = platform.createDomain();
        DomainUser user1 = domain.createUser("user1", TestConstants.TEST_PASSWORD);

        // create a vault
        Vault vault = platform.createVault();

        // export the user
        Archive archive = user1.exportArchive(vault, "a", "b", "1");

        // TEST - new domain, import into domain
        Domain domain2 = platform.createDomain();
        Job job2 = domain2.importArchive(archive);
        assertTrue(JobState.FINISHED.equals(job2.getState()));
        assertNotNull(domain2.readPrincipal("user1"));
    }

}
