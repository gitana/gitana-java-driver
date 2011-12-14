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

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.nodes.Node;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.support.QName;
import org.junit.Test;

/**
 * @author uzi
 */
public class SocialTest extends AbstractTestCase
{
    @Test
    public void testCommentsWithRating()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        int ratingTotalCount = -1;
        int ratingTotalValue = -1;
        double ratingAverageValue = 0;

        Branch master = platform.createRepository().readBranch("master");

        // product
        Node product = (Node) master.createNode();

        // create an empty comment
        // associate to product
        Node comment1 = (Node) master.createNode(QName.create("n:comment"));
        product.associate(comment1, QName.create("a:has_comment"));

        /*
        // verify product rating
        product.reload();
        ratingTotalCount = product.getObject("stats").get("ratingTotalCount").getIntValue();
        assertEquals(1, ratingTotalCount);
        ratingTotalValue = product.getObject("stats").get("ratingTotalValue").getIntValue();
        assertEquals(0, ratingTotalValue);
        ratingAverageValue = product.getObject("stats").get("ratingAverageValue").getDoubleValue();
        assertEquals(0, ratingAverageValue);

        // create an empty comment
        // associate to product
        Node comment2 = (Node) master.createNode(QName.create("n:comment"));
        product.associate(comment2, QName.create("a:has_comment"));

        // verify product rating
        product.reload();
        ratingTotalCount = product.getObject("stats").get("ratingTotalCount").getIntValue();
        assertEquals(2, ratingTotalCount);
        ratingTotalValue = product.getObject("stats").get("ratingTotalValue").getIntValue();
        assertEquals(0, ratingTotalValue);
        ratingAverageValue = product.getObject("stats").get("ratingAverageValue").getDoubleValue();
        assertEquals(0, ratingAverageValue);

        // create an empty comment
        // associate to product
        Node comment3 = (Node) master.createNode(QName.create("n:comment"));
        product.associate(comment3, QName.create("a:has_comment"));

        // verify product rating
        product.reload();
        ratingTotalCount = product.getObject("stats").get("ratingTotalCount").getIntValue();
        assertEquals(3, ratingTotalCount);
        ratingTotalValue = product.getObject("stats").get("ratingTotalValue").getIntValue();
        assertEquals(0, ratingTotalValue);
        ratingAverageValue = product.getObject("stats").get("ratingAverageValue").getDoubleValue();
        assertEquals(0, ratingAverageValue);

        // now update ratings
        comment1.set("rating", 3);
        comment1.update();
        comment2.set("rating", 6);
        comment2.update();
        comment3.set("rating", 9);
        comment3.update();

        // verify product rating
        product.reload();
        ratingTotalCount = product.getObject("stats").get("ratingTotalCount").getIntValue();
        assertEquals(3, ratingTotalCount);
        ratingTotalValue = product.getObject("stats").get("ratingTotalValue").getIntValue();
        assertEquals(18, ratingTotalValue);
        ratingAverageValue = product.getObject("stats").get("ratingAverageValue").getDoubleValue();
        assertEquals(6.0, ratingAverageValue);
        */
    }
}
