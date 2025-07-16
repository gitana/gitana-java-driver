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
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class MountTest extends AbstractTestCase
{
    @Test
    public void testMounts()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // get the mount nodes for this branch
        ResultMap<Node> mounts1 = master.listNodes();

        // create a new mount
        Node node1 = (Node) master.createNode();
        node1.mount("scoobydoo");

        // assert mount nodes size + 1
        ResultMap<Node> mounts2 = master.listNodes();
        assertEquals(mounts1.size() + 1, mounts2.size());

        // unmount
        node1.unmount();

        // assert mount nodes size back to before
        ResultMap<Node> mounts3 = master.listNodes();
        assertEquals(mounts1.size(), mounts3.size());
    }

}
