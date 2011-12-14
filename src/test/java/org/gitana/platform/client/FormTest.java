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
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.types.HasFormAssociation;
import org.gitana.platform.client.types.TypeDefinition;
import org.gitana.platform.support.QName;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class FormTest extends AbstractTestCase
{
    @Test
    public void testCRUD()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        // create a definition
        TypeDefinition typeDefinition = master.defineType(QName.create("blah:blah"));

        // list forms
        Map<String, HasFormAssociation> results1 = typeDefinition.fetchFormAssociations();
        assertEquals(0, results1.size());

        // create three forms
        typeDefinition.createForm("test1");
        typeDefinition.createForm("test2");
        typeDefinition.createForm("test3");

        // list forms
        List<HasFormAssociation> results2 = typeDefinition.listFormAssociations();
        assertEquals(3, results2.size());

        // delete a form
        typeDefinition.deleteForm("test2");

        // list forms
        List<HasFormAssociation> results3 = typeDefinition.listFormAssociations();
        assertEquals(2, results3.size());
    }

}
