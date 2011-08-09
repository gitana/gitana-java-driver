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

import org.gitana.repo.query.QueryBuilder;
import org.gitana.repo.support.ResultMap;
import org.gitana.util.ClasspathUtil;
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
        Server server = gitana.authenticate("admin", "admin");

        // create artifact 1
        // org/gitana/1.0.0/test-artifact1-1.0.0.zip
        byte[] bytes1 = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/archive.zip");
        InputStream in1 = new ByteArrayInputStream(bytes1);
        server.uploadArchive("org.gitana", "test-artifact1", "1.0.0", in1, bytes1.length);
        // verify
        Archive archive1 = server.readArchive("org.gitana", "test-artifact1", "1.0.0");
        assertNotNull(archive1);


        // create artifact 2
        // org/gitana/2.0.0/test-artifact2-2.0.0.zip
        byte[] bytes2 = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/archive.zip");
        InputStream in2 = new ByteArrayInputStream(bytes2);
        server.uploadArchive("org.gitana", "test-artifact2", "2.0.0", in2, bytes2.length);
        // verify
        Archive archive2 = server.readArchive("org.gitana", "test-artifact2", "2.0.0");
        assertNotNull(archive2);


        // create artifact 3
        // org/cloudcms/1.0.0/test-artifact3-1.0.0.zip
        byte[] bytes3 = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/archive.zip");
        InputStream in3 = new ByteArrayInputStream(bytes3);
        server.uploadArchive("org.cloudcms", "test-artifact3", "1.0.0", in3, bytes3.length);
        // verify
        Archive archive3 = server.readArchive("org.cloudcms", "test-artifact3", "1.0.0");
        assertNotNull(archive3);


        // query for all artifacts with version "1.0.0"
        // should be two
        ResultMap<Archive> query1 = server.queryArchives(QueryBuilder.start(Archive.FIELD_VERSION_ID).is("1.0.0").get());
        assertEquals(2, query1.size());

        // query for all artifacts with version "2.0.0"
        // should be one
        ResultMap<Archive> query2 = server.queryArchives(QueryBuilder.start(Archive.FIELD_VERSION_ID).is("2.0.0").get());
        assertEquals(1, query2.size());

        // query for all artifacts with group "org.gitana"
        // should be two
        ResultMap<Archive> query3 = server.queryArchives(QueryBuilder.start(Archive.FIELD_GROUP_ID).is("org.gitana").get());
        assertEquals(2, query3.size());


        // read artifact #1
        byte[] bytes = server.downloadArchive("org.gitana", "test-artifact1", "1.0.0");
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);


        // delete artifact #1
        server.deleteArchive("org.gitana", "test-artifact1", "1.0.0");


        // query for all artifacts with version "1.0.0"
        // should now only be one
        ResultMap<Archive> query4 = server.queryArchives(QueryBuilder.start(Archive.FIELD_VERSION_ID).is("1.0.0").get());
        assertEquals(1, query4.size());

        // try to fetch binary for artifact 1
        // should throw
        Exception ex = null;
        try
        {
            bytes = server.downloadArchive("org.gitana", "test-artifact1", "1.0.0");
        }
        catch(Exception e)
        {
            ex = e;
        }
        assertNotNull(ex);
    }
}
