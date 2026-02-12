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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class Repository2Test extends AbstractTestCase
{
    @Test
    public void test()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

        // set some properties on it
        repository.setTitle("foo");
        ObjectNode releasesObject1 = repository.getObject("releases");
        if (releasesObject1 == null) {
            releasesObject1 = JsonUtil.createObject();
            repository.getObject().put("releases", releasesObject1);
        }
        releasesObject1.put("blockMaster", true);
        repository.update();

        // verify
        String t11 = repository.getTitle();
        assertEquals("foo", t11);
        boolean blockMaster11 = repository.getObject("releases").get("blockMaster").booleanValue();
        assertTrue(blockMaster11);

        // reload
        repository.reload();

        // verify
        String t12 = repository.getTitle();
        assertEquals("foo", t12);
        boolean blockMaster2 = repository.getObject("releases").get("blockMaster").booleanValue();
        assertTrue(blockMaster2);

        // read it again
        Repository repository2 = platform.readRepository(repository.getId());

        // verify
        String t21 = repository.getTitle();
        assertEquals("foo", t21);
        boolean blockMaster21 = repository.getObject("releases").get("blockMaster").booleanValue();
        assertTrue(blockMaster21);

        // change it
        repository2.setTitle("foo2");
        ObjectNode releasesObject2 = repository2.getObject("releases");
        releasesObject2.put("blockMaster", false);
        repository2.update();

        // verify
        repository2.reload();
        String t22 = repository2.getTitle();
        assertEquals("foo2", t22);
        boolean blockMaster22 = repository2.getObject("releases").get("blockMaster").booleanValue();
        assertFalse(blockMaster22);
    }
}
