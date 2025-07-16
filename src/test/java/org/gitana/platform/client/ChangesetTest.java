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

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.changeset.Changeset;
import org.gitana.platform.client.node.BaseNode;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class ChangesetTest extends AbstractTestCase
{
    @Test
    public void testChangesets1()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repo
        Repository repository = platform.createRepository();

        // get the master branch
        Branch branch = repository.readBranch("master"); // 0

        // create some nodes
        Node n1 = (Node) branch.createNode(); // 1
        Node n2 = (Node) branch.createNode(); // 2
        assertNotSame(n1.getChangesetId(), n2.getChangesetId());
        Node n3 = (Node) branch.createNode(); // 3
        assertNotSame(n2.getChangesetId(), n3.getChangesetId());
        Node n4 = (Node) branch.createNode(); // 4
        assertNotSame(n3.getChangesetId(), n4.getChangesetId());
        Node n5 = (Node) branch.createNode(); // 5
        assertNotSame(n4.getChangesetId(), n5.getChangesetId());
        Node n6 = (Node) branch.createNode(); // 6
        assertNotSame(n5.getChangesetId(), n6.getChangesetId());
        Node n7 = (Node) branch.createNode(); // 7
        assertNotSame(n6.getChangesetId(), n7.getChangesetId());
        Node n8 = (Node) branch.createNode(); // 8
        assertNotSame(n7.getChangesetId(), n8.getChangesetId());

        // list all of the changesets (should be 9 - 8 created + 0:root)
        assertEquals(9, repository.listChangesets().size());

        // fetch changesets
        assertEquals(9, repository.listChangesets().size());

        // assert paginated lists work
        Pagination pagination = new Pagination();
        pagination.setLimit(3);
        assertEquals(3, repository.listChangesets(pagination).size());

        // query
        assertEquals(9, repository.queryChangesets(JsonUtil.createObject()).size());

        // assert paginated queries work
        assertEquals(3, repository.queryChangesets(JsonUtil.createObject(), pagination).size());

        // check one of the changesets and make sure we can snag nodes off it
        Changeset changeset6 = repository.listChangesets().get(n6.getChangesetId());
        ResultMap<BaseNode> nodes6 = changeset6.listNodes();
        assertTrue(nodes6.get(n6.getId()) != null);
    }
}
