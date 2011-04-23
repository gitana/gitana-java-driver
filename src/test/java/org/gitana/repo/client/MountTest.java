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

import org.gitana.repo.client.nodes.Node;
import org.junit.Test;

import java.util.List;

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
        gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = gitana.repositories().create();

        // get the master branch
        Branch master = repository.branches().read("master");

        // get the mount nodes for this branch
        List<Node> mounts1 = master.nodes().list();

        // create a new mount
        Node node1 = master.nodes().create();
        node1.mount("scoobydoo");

        // assert mount nodes size + 1
        List<Node> mounts2 = master.nodes().list();
        assertEquals(mounts1.size() + 1, mounts2.size());

        // unmount
        node1.unmount();

        // assert mount nodes size back to before
        List<Node> mounts3 = master.nodes().list();
        assertEquals(mounts1.size(), mounts3.size());
    }

}
