/**
 * Copyright 2013 Gitana Software, Inc.
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

import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.project.Project;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.stack.Stack;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class ProjectTest extends AbstractTestCase
{
    @Test
    public void test1()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a stack
        Stack stack = platform.createStack();
        stack.update();

        // create a repository + assign to stack
        Repository repository1 = platform.createRepository();
        stack.assignDataStore(repository1);
        // create a domain + assign to stack
        Domain domain1 = platform.createDomain();
        stack.assignDataStore(domain1);

        // create project #1
        String val = "val-" + System.currentTimeMillis();
        Project project1 = platform.createProject(QueryBuilder.start("prop").is(val).get());
        // create project #2 (using stack)
        Project project2 = platform.createProject(QueryBuilder.start("prop").is(val).get());
        project2.setStackId(stack.getId());
        project2.setSharedStack(true);
        project2.update();
        // create project #3 (using stack)
        Project project3 = platform.createProject(QueryBuilder.start("prop").is(val).get());
        project3.setStackId(stack.getId());
        project3.setSharedStack(true);
        project3.update();


        // query for projects
        ResultMap<Project> results = platform.queryProjects(QueryBuilder.start("prop").is(val).get());
        assertEquals(3, results.size());


        // delete project #1
        project1.delete();

        results = platform.queryProjects(QueryBuilder.start("prop").is(val).get());
        assertEquals(2, results.size());


        // try to delete the stack
        // should throw exception
        Exception ex2 = null;
        try
        {
            stack.delete();
        }
        catch (Exception ex)
        {
            ex2 = ex;
        }
        assertNotNull(ex2);


        // delete project #2
        project2.delete();

        // delete project #3
        project3.delete();


        results = platform.queryProjects(QueryBuilder.start("prop").is(val).get());
        assertEquals(0, results.size());


        // try to delete the stack
        // should now work
        Exception ex3 = null;
        try
        {
            stack.delete();
        }
        catch (Exception ex)
        {
            ex3 = ex;
        }
        assertNull(ex3);
    }

}
