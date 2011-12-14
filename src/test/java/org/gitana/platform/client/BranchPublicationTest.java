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

import org.gitana.JSONBuilder;
import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.nodes.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.support.QName;
import org.gitana.util.StreamUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author uzi
 */
public class BranchPublicationTest extends AbstractTestCase
{
    @Test
    public void test()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default vault
        Vault vault = platform.readDefaultVault();

        // create a repository
        Repository repo1 = platform.createRepository();

        // get the master branch
        Branch master1 = repo1.readBranch("master");

        // create a bunch of nodes
        Node node1 = (Node) master1.createNode();
        Node node2 = (Node) master1.createNode();
        Node node3 = (Node) master1.createNode(JSONBuilder.start("def").is("jam").get());
        Node node4 = (Node) master1.createNode();
        Node node5 = (Node) master1.createNode();

        // connect together
        QName aChild = QName.create("a:child");
        node1.associate(node2, aChild);
        node1.associate(node3, aChild);
        node1.associate(node4, aChild);
        node4.associate(node5, aChild);

        // archive data
        String groupId = "uzquiano";
        String artifactId = "mike";
        String versionId = "version" + System.currentTimeMillis();

        // tell Gitana to export an archive of "master1" branch
        Archive archive = master1.exportPublicationArchive(vault, groupId, artifactId, versionId);
        assertNotNull(archive);

        // ensure we can read it back manually
        archive = vault.readArchive(groupId, artifactId, versionId);
        assertNotNull(archive);

        // download the archive
        // verify it has size
        InputStream in = vault.downloadArchive(groupId, artifactId, versionId);
        byte[] bytes = StreamUtil.getBytes(in);
        assertTrue(bytes.length > 0);

        // delete the archive
        vault.deleteArchive(groupId, artifactId, versionId);

        // verify it was deleted
        archive = vault.readArchive(groupId, artifactId, versionId);
        assertNull(archive);

        // upload the archive anew
        in = new ByteArrayInputStream(bytes);
        vault.uploadArchive(in, bytes.length);

        // verify the archive exists
        archive = vault.readArchive(groupId, artifactId, versionId);
        assertNotNull(archive);

        // create a new repo
        Repository repo2 = platform.createRepository();
        Branch master2 = repo2.readBranch("master");

        // import the archive into the new repo
        master2.importPublicationArchive(vault, groupId, artifactId, versionId);

        // verify the branch has all the content
        Node x1 = (Node) master2.readNode(node1.getId());
        assertNotNull(x1);
        assertEquals(node1.associations().size(), x1.associations().size());

    }

}
