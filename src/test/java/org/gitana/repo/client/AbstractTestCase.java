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

import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.ResourceBundle;

/**
 * Base class for tests
 * 
 * @author uzi
 */
@Ignore public abstract class AbstractTestCase extends TestCase
{
    private ResourceBundle config = null;

	public void setUp() throws Exception
	{
        // load properties from classpath
        this.config = ResourceBundle.getBundle("gitana");
	}

	public void tearDown() throws Exception
	{
        // force the garbage collector (during tests)
        System.gc();
	}

    protected String getHost()
    {
        return this.config.getString("gitana.server.host");
    }

    protected int getPort()
    {
        return Integer.valueOf(this.config.getString("gitana.server.port"));
    }
}
