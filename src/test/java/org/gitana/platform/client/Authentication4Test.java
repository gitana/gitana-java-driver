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

import org.gitana.http.*;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.util.TempFileUtil;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests out various OAuth2 state persisters.
 *
 * @author uzi
 */
public class Authentication4Test extends AbstractTestCase
{
    @Test
    public void testHeapStatePersister()
    {
        // loads the test client (via gitana.properties in test)
        Gitana gitana = new Gitana();

        // build a state persister that serializes to heap
        HeapOAuth2StatePersister statePersister = new HeapOAuth2StatePersister();

        // apply state persister to the gitana instance
        gitana.setStatePersister(statePersister);

        // now connect
        Platform platform = gitana.authenticate("admin", "admin");
        Repository repository = platform.createRepository();
        assertNotNull(repository);
    }

    @Test
    public void testSerializedFileStatePersister()
    {
        // loads the test client (via gitana.properties in test)
        Gitana gitana = new Gitana();

        // build a state persister that serializes to a temp file
        File tempFile = TempFileUtil.createTempFile("javatest-", ".ser");
        SerializedFileOAuth2StatePersister statePersister = new SerializedFileOAuth2StatePersister(tempFile);

        // apply state persister to the gitana instance
        gitana.setStatePersister(statePersister);

        // now connect
        Platform platform = gitana.authenticate("admin", "admin");
        Repository repository = platform.createRepository();
        assertNotNull(repository);

        // verify the file exists
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
    }

    @Test
    public void testJsonFileStatePersister()
    {
        // loads the test client (via gitana.properties in test)
        Gitana gitana = new Gitana();

        // build a state persister that serializes to a temp file
        File tempFile = TempFileUtil.createTempFile("javatest-", ".json");
        JsonFileOAuth2StatePersister statePersister = new JsonFileOAuth2StatePersister(tempFile);

        // apply state persister to the gitana instance
        gitana.setStatePersister(statePersister);

        // now connect
        Platform platform = gitana.authenticate("admin", "admin");
        Repository repository = platform.createRepository();
        assertNotNull(repository);

        // verify the file exists
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);
    }

    @Test
    public void testCustomStatePersister()
    {
        // loads the test client (via gitana.properties in test)
        Gitana gitana = new Gitana();

        // build a state persister that serializes to a temp file
        Map<String, OAuth2State> map = new HashMap<String, OAuth2State>();
        OAuth2StatePersister customStatePersister = new OAuth2StatePersister() {
            @Override
            public OAuth2State read() {
                return map.get("default");
            }

            @Override
            public void write(OAuth2State state) {
                map.put("default", state);
            }
        };

        // apply state persister to the gitana instance
        gitana.setStatePersister(customStatePersister);

        // now connect
        Platform platform = gitana.authenticate("admin", "admin");
        Repository repository = platform.createRepository();
        assertNotNull(repository);
    }
}
