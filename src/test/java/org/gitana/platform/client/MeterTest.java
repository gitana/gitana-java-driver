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

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.api.Client;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.meter.Meter;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.services.meter.MeterType;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class MeterTest extends AbstractTestCase
{
    @Test
    public void testMounts()
    {
        // authenticate
        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // create a domain
        Domain domain = platform.createDomain();

        // create a principal + tenant (#1)
        String userName1 = "user1-" + System.currentTimeMillis();
        DomainUser user1 = domain.createUser(userName1, "pw");
        Tenant tenant = registrar.createTenant(user1, "unlimited");
        ObjectNode defaultClientObject1 = tenant.readDefaultAllocatedClientObject();
        assertNotNull(defaultClientObject1);
        String clientKey1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_KEY);
        String clientSecret1 = JsonUtil.objectGetString(defaultClientObject1, Client.FIELD_SECRET);

        // tenant meters
        Meter storageMeter1 = tenant.readCurrentMeter(MeterType.STORAGE);
        long storageByteCount1 = storageMeter1.getRawByteCount(); // will be 0
        long storageObjectCount1 = storageMeter1.getRawObjectCount(); // will be 0
        Meter transferOutMeter1 = tenant.readCurrentMeter(MeterType.TRANSFER_OUT);
        long transferOutByteCount1 = transferOutMeter1.getRawByteCount(); // will be 0
        long transferOutObjectCount1 = transferOutMeter1.getRawObjectCount(); // will be 0


        //
        // now authenticate as the tenant principal #1
        //

        gitana = new Gitana(clientKey1, clientSecret1);
        platform = gitana.authenticateOnTenant(user1, "pw", tenant);

        // create a couple of things as this new tenant
        Domain domain1 = platform.createDomain();
        domain1.createUser("bob", "zemeckis");
        Repository repository1 = platform.createRepository();
        repository1.readBranch("master").createNode();
        repository1.readBranch("master").createNode();
        repository1.readBranch("master").createNode();


        //
        // authenticate again as admin
        //

        gitana = new Gitana();
        platform = gitana.authenticate("admin", "admin");

        // tenant meters
        Meter storageMeter2 = tenant.readCurrentMeter(MeterType.STORAGE);
        long storageByteCount2 = storageMeter2.getRawByteCount();
        long storageObjectCount2 = storageMeter2.getRawObjectCount();
        Meter transferOutMeter2 = tenant.readCurrentMeter(MeterType.TRANSFER_OUT);
        long transferOutByteCount2 = transferOutMeter2.getRawByteCount();
        long transferOutObjectCount2 = transferOutMeter2.getRawObjectCount();

        // the storage meters will be the same since the background thread won't have run to update the data store
        // statistics (it runs every few minutes)
        // and even if it did run, its possible that mongodb storage allocations would remain the same depending
        // on how mongodb is set up
        assertTrue(storageByteCount2 >= storageByteCount1);
        assertTrue(storageObjectCount2 >= storageObjectCount1);

        // the transfer meters should always increase on a per-hit basis, no background threads
        assertTrue(transferOutByteCount2 > transferOutByteCount1);
        assertTrue(transferOutObjectCount2 > transferOutObjectCount1);

    }

}
