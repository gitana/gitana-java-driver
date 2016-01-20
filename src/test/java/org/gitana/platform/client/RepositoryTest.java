/**
 * Copyright 2016 Gitana Software, Inc.
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

import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.Pagination;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class RepositoryTest extends AbstractTestCase
{
    @Test
    public void testRepositories()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // list repositories
        int baseCount = platform.listRepositories(Pagination.limit(-1)).size();

        // create three repositories
        Repository repository1 = platform.createRepository();
        Repository repository2 = platform.createRepository();
        Repository repository3 = platform.createRepository();

        // list repositories
        assertEquals(baseCount + 3, platform.listRepositories(Pagination.limit(-1)).size());

        // read one back for assurance
        Repository check2 = platform.readRepository(repository2.getId());
        assertEquals(repository2, check2);

        // update the third one
        repository3.update();

        // delete the third one
        repository3.delete();

        // read back for assurance
        Repository check3 = platform.readRepository(repository3.getId());
        assertNull(check3);

        // list repositories
        assertEquals(baseCount + 2, platform.listRepositories(Pagination.limit(-1)).size());
    }

    @Test
    public void testRepositoryFiles()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repo
        Repository repo = platform.createRepository();

        // upload a file
        String filename = "testfilename-" + System.currentTimeMillis() + "-bugs.jpeg";
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg");
        repo.uploadFile(filename, bytes, "image/jpeg");

        // download hte file
        byte[] verify = repo.downloadFile(filename);
        assertEquals(bytes.length, verify.length);
    }
}
