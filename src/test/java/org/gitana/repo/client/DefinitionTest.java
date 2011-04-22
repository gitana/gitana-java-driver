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

package org.gitana.repo.client;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.services.Branches;
import org.gitana.repo.namespace.QName;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.io.IOException;

/**
 * @author uzi
 */
public class DefinitionTest extends AbstractTestCase {
    @Test
    public void testCRUD() {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = gitana.repositories().create();

        Branches branches = repository.branches();

        // list branches (should have 1)
        assertEquals(1, branches.list().size());

        // get the master branch
        Branch master = branches.read("master");
        assertNotNull(master);
        assertTrue(master.isMaster());

        ObjectNode testDefinitionObj = JsonUtil.createObject();
        //int beforeSize = master.definitions().list().size();
        Node testDefinition = master.definitions().defineType(QName.create("test:product"), testDefinitionObj);
        //int afterSize = master.definitions().list().size();
        //assertEquals(beforeSize + 1, afterSize);
        Node verifyDefinition = master.definitions().read(testDefinition.getQName());
        assertNotNull(verifyDefinition);
        //assertEquals(verifyDefinition.getQName().toString(), "test:product");
    }
}
