/**
 * Copyright 2017 Gitana Software, Inc.
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
import org.gitana.platform.client.node.type.Definition;
import org.gitana.platform.client.node.type.Rule;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.services.transfer.TransferImportStrategy;
import org.gitana.platform.support.QName;
import org.gitana.util.ClasspathUtil;
import org.gitana.util.JsonUtil;
import org.junit.Test;

/**
 * @author uzi
 */
public class DraftMasterCopy3Test extends AbstractTestCase
{
    @Test
    public void test()
        throws Exception
    {
        // authenticate as admin
        Platform platform = new Gitana().authenticate("admin", "admin");

        // create repository #1 (live)
        Repository repository1 = platform.createRepository();

        // create repository #2 (draft)
        Repository repository2 = platform.createRepository();

        // live branch
        Branch master = repository1.readBranch("master");

        // draft branch
        Branch draft = repository2.readBranch("master");

        // define "my:article" on draft
        QName QNAME_MY_ARTICLE = QName.create("my:article");
        Node draftArticleDefinition = draft.defineType(QNAME_MY_ARTICLE);

        // create a rule on draft where any instances of my:article are automatically sync'd to master
        ObjectNode touchRuleObject = ClasspathUtil.objectFromClasspath("org/gitana/platform/client/touchrule1.json");
        ((ObjectNode) touchRuleObject.get("actions").get(0).get("config")).put("targetRepositoryId", master.getRepositoryId());
        ((ObjectNode) touchRuleObject.get("actions").get(0).get("config")).put("targetBranchId", master.getId());
        Node rule1 = (Node) draft.createNode(touchRuleObject);

        // bind the rule to the "p:afterTouchNode" policy for all draft my:articles
        ObjectNode associationObject = JsonUtil.createObject();
        associationObject.put("policy", "p:afterTouchNode");
        associationObject.put("scope", 1); // 0 = instance, 1 = class
        draftArticleDefinition.associate(rule1, QName.create("a:has_behavior"), associationObject);

        // now, whenever we update a my:article instance and "state" == "published", it will copy to the master branch

        // on the draft branch, create an article
        Node draftNode = (Node) draft.createNode(QNAME_MY_ARTICLE);
        draftNode.set("version", "1");
        draftNode.update();

        // put three attachments onto the node
        byte[] bytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/bugs.jpeg");
        draftNode.uploadAttachment("attach1", bytes, "image/jpeg");
        draftNode.uploadAttachment("attach2", bytes, "image/jpeg");
        draftNode.uploadAttachment("attach3", bytes, "image/jpeg");

        // assert - node exists on draft, not on master
        assertEquals(1, draft.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(0, master.queryNodes(JSONBuilder.start("version").is("1").get()).size());

        // set draft to published
        draftNode.set("state", "published");
        draftNode.update();

        // wait a bit
        Thread.sleep(10000);

        // assert - node exists on draft and on master
        assertEquals(1, draft.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(1, master.queryNodes(JSONBuilder.start("version").is("1").get()).size());

        // update on draft
        draftNode.set("version", "2");
        draftNode.update();

        // wait a bit
        Thread.sleep(10000);

        // assert - node exists on draft (v2) and on master (v2)
        assertEquals(0, draft.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(1, draft.queryNodes(JSONBuilder.start("version").is("2").get()).size());
        assertEquals(0, master.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(1, master.queryNodes(JSONBuilder.start("version").is("2").get()).size());

        // delete the node
        draftNode.delete();

        // wait a bit
        Thread.sleep(10000);

        // verify it was also deleted on master
        assertEquals(0, master.queryNodes(JSONBuilder.start("version").is("1").get()).size());
        assertEquals(0, master.queryNodes(JSONBuilder.start("version").is("2").get()).size());

    }
}
