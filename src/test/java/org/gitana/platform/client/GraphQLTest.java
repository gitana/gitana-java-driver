/**
 * Copyright (C) 2021 Gitana Software, Inc.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Gitana Software, Inc and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Gitana Software, Inc. and its suppliers
 * and may be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 *
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Gitana Software, Inc.
 */
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.junit.Test;

/**
 * @author mwhitman
 */
public class GraphQLTest extends AbstractTestCase
{
    @Test
    public void testGraphql()
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create a repository
        Repository repository = platform.createRepository();

        // get the master branch
        Branch master = repository.readBranch("master");

        String schema = master.graphqlSchema();
        assertNotNull(schema);

        String query = "query { n_nodes { title } }";
        ObjectNode result = master.graphqlQuery(query);
        assertNotNull(result);
    }
}
