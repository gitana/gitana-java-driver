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

import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.job.Job;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainGroup;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.vault.Vault;
import org.junit.Test;

/**
 * @author uzi
 */
public class DomainTransferTest extends AbstractTestCase
{
    @Test
    public void test()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a domain with some stuff
        Domain domain = platform.createDomain();
        DomainGroup group = domain.createGroup("group1");
        DomainUser user = domain.createUser("user1", "pw1");
        group.addPrincipal(user);

        // create a vault
        Vault vault = platform.createVault();

        // export domain
        Archive archive = domain.exportArchive(vault, "a", "b", "1");

        // FIRST - create a new domain and import into it
        Domain domain2 = platform.createDomain();
        domain2.importArchive(archive);
        assertNotNull(domain2.readPrincipal("group1"));
        assertNotNull(domain2.readPrincipal("user1"));

        // SECOND - import into platform (new domain)
        Job job = platform.importArchive(archive);
        Domain domain3 = platform.readDomain(job.getString("importedDataStoreId"));
        assertNotNull(domain3.readPrincipal("group1"));
        assertNotNull(domain3.readPrincipal("user1"));
    }

}
