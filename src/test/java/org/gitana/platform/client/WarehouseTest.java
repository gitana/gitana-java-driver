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
import org.gitana.platform.client.warehouse.Warehouse;
import org.junit.Test;

/**
 * @author uzi
 */
public class WarehouseTest extends AbstractTestCase
{
    @Test
    public void testWebHosts()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // list warehouses
        int baseCount = platform.listWarehouses().size();

        // create three warehouses
        Warehouse warehouse1 = platform.createWarehouse();
        Warehouse warehouse2 = platform.createWarehouse();
        Warehouse warehouse3 = platform.createWarehouse();

        // list warehouses
        assertEquals(baseCount + 3, platform.listWarehouses().size());

        // read one back for assurance
        Warehouse check2 = platform.readWarehouse(warehouse2.getId());
        assertEquals(warehouse2, check2);

        // update the third one
        warehouse3.update();

        // delete the third one
        warehouse3.delete();

        // read back for assurance
        Warehouse check3 = platform.readWarehouse(warehouse3.getId());
        assertNull(check3);

        // list warehouses
        assertEquals(baseCount + 2, platform.listWarehouses().size());
    }

}
