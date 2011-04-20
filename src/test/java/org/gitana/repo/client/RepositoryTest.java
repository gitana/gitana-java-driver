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

import org.gitana.repo.client.services.Repositories;
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
        gitana.authenticate("admin", "admin");

        // repositories
        Repositories repositories = gitana.repositories();

        // list repositories
        int baseCount = repositories.list().size();

        // create three repositories
        Repository repository1 = gitana.repositories().create();
        Repository repository2 = gitana.repositories().create();
        Repository repository3 = gitana.repositories().create();

        // list repositories
        assertEquals(baseCount + 3, repositories.list().size());

        // read one back for assurance
        Repository check2 = repositories.read(repository2.getId());
        assertEquals(repository2, check2);

        // update the third one
        repository3.update();

        // delete the third one
        repository3.delete();

        // read back for assurance
        Repository check3 = repositories.read(repository3.getId());
        assertNull(check3);

        // list repositories
        assertEquals(baseCount + 2, repositories.list().size());
    }

    @Test
    public void testRepositoryFiles()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        gitana.authenticate("admin", "admin");

        // create a repo
        Repository repo = gitana.repositories().create();

        // upload a file
        String filename = "testfilename-" + System.currentTimeMillis() + "-bugs.jpeg";
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/bugs.jpeg");
        repo.uploadFile(filename, bytes, "image/jpeg");

        // download hte file
        byte[] verify = repo.downloadFile(filename);
        assertEquals(bytes.length, verify.length);
    }
}
