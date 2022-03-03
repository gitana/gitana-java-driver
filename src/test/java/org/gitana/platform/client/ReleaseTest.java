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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.JSONBuilder;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.release.Release;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.association.Direction;
import org.gitana.platform.services.association.Directionality;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.QName;
import org.gitana.util.DateUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

import java.util.Calendar;

/**
 * @author uzi
 */
public class ReleaseTest extends AbstractTestCase
{
    public static Calendar secondsInFuture(int seconds)
    {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.SECOND, seconds);

        return date;
    }

    @Test
    public void testRelativesAndChildren()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // create something on master branch
        Node node1 = (Node) master.createNode();
        node1.setTitle("Hello World");
        node1.update();

        // create a release
        ObjectNode releaseObject1 = JsonUtil.createObject();
        releaseObject1.put("title", "First Release");
        Release release1 = repository.createRelease(releaseObject1);

        Node node1prime = (Node) release1.getBranch().readNode(node1.getId());
        node1prime.setTitle("Hello World Updated");
        node1prime.update();

        // set the release time to 10 seconds from now
        release1.set(Release.FIELD_RELEASE_DATE, DateUtil.getTimestamp(secondsInFuture(10)));
        release1.update();

        // finalize the release
        ObjectNode finalizeResult1 = release1.finalize(false);

        // schedule the release
        ObjectNode finalizeResult2 = release1.finalize(true);

        // wait until it goes live
        Thread.sleep(20000);

        // read back on master and verify that master has been updated
        node1.reload();

        assertTrue("Hello World Updated".equals(node1.getTitle()));
    }
}
