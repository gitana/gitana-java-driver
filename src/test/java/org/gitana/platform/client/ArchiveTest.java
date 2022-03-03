/**
 * Copyright 2022 Gitana Software, Inc.
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

import org.gitana.platform.client.archive.Archive;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.StreamUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author uzi
 */
public class ArchiveTest extends AbstractTestCase
{
    @Test
    public void testArchives()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a vault
        Vault vault = platform.createVault();

        // create artifact 1
        // org/gitana/1.0.0/test-artifact1-1.0.0.zip
        byte[] bytes1 = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/archive-repository.zip");
        InputStream in1 = new ByteArrayInputStream(bytes1);
        vault.uploadArchive(in1, bytes1.length);
        // wait for completion
        Thread.sleep(10000);
        // verify
        Archive archive1 = vault.lookupArchive("org.gitana", "test-artifact1", "1.0.0");
        assertNotNull(archive1);


        // create artifact 2
        // org/gitana/2.0.0/test-artifact2-2.0.0.zip
        byte[] bytes2 = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/archive-domain.zip");
        InputStream in2 = new ByteArrayInputStream(bytes2);
        vault.uploadArchive(in2, bytes2.length);
        // wait for completion
        Thread.sleep(10000);
        // verify
        Archive archive2 = vault.lookupArchive("org.gitana", "test-artifact2", "2.0.0");
        assertNotNull(archive2);


        // create artifact 3
        // org/cloudcms/1.0.0/test-artifact3-1.0.0.zip
        byte[] bytes3 = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/archive-node.zip");
        InputStream in3 = new ByteArrayInputStream(bytes3);
        vault.uploadArchive(in3, bytes3.length);
        // wait for completion
        Thread.sleep(10000);
        // verify
        Archive archive3 = vault.lookupArchive("org.cloudcms", "test-artifact3", "1.0.0");
        assertNotNull(archive3);


        // query for all artifacts with version "1.0.0"
        // should be two
        ResultMap<Archive> query1 = vault.queryArchives(QueryBuilder.start(Archive.FIELD_VERSION_ID).is("1.0.0").get());
        assertEquals(2, query1.size());

        // query for all artifacts with version "2.0.0"
        // should be one
        ResultMap<Archive> query2 = vault.queryArchives(QueryBuilder.start(Archive.FIELD_VERSION_ID).is("2.0.0").get());
        assertEquals(1, query2.size());

        // query for all artifacts with group "org.gitana"
        // should be two
        ResultMap<Archive> query3 = vault.queryArchives(QueryBuilder.start(Archive.FIELD_GROUP_ID).is("org.gitana").get());
        assertEquals(2, query3.size());

        // query for all artifacts that could be imported into a "platform"
        ResultMap<Archive> query4 = vault.queryArchives(QueryBuilder.start("_containerType").is("platform").get());
        assertEquals(2, query4.size());

        // query for all artifacts that could be imported into a "node"
        ResultMap<Archive> query5 = vault.queryArchives(QueryBuilder.start("_containerType").is("node").get());
        assertEquals(1, query5.size());

        // query for all artifacts that could be imported into a "domain"
        ResultMap<Archive> query6 = vault.queryArchives(QueryBuilder.start("_containerType").is("domain").get());
        assertEquals(0, query6.size());


        // read artifact #1
        Archive arc1 = vault.lookupArchive("org.gitana", "test-artifact1", "1.0.0");
        InputStream in = arc1.download();
        byte[] bytes = StreamUtil.getBytes(in);
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);


        // delete artifact #1
        vault.lookupArchive("org.gitana", "test-artifact1", "1.0.0").delete();


        // query for all artifacts with version "1.0.0"
        // should now only be one
        ResultMap<Archive> query10 = vault.queryArchives(QueryBuilder.start(Archive.FIELD_VERSION_ID).is("1.0.0").get());
        assertEquals(1, query10.size());

        // try to fetch binary for artifact 1
        // should throw
        Exception ex = null;
        try
        {
            vault.downloadArchive(arc1.getId());
        }
        catch(Exception e)
        {
            ex = e;
        }
        assertNotNull(ex);
    }
}
