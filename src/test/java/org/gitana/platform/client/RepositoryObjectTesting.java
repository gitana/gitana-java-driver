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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.support.QueryBuilder;

public class RepositoryObjectTesting
{
//    private static String clientId = "0";
//
    private Driver uploadDriver;
//    private static boolean update=false;

//    public static void main(String[] args) throws Exception {
//        RepositoryObjectTesting test = new RepositoryObjectTesting();
//        Platform platform = test.authenticateGitanaForUpload();
//        Repository repository = test.getRepository(clientId, platform);
//        repository.reload();
//        if (repository != null) {
//            ObjectNode releaseMap = repository.getObject("releases");
//            releaseMap.put("blockMaster", update);   // false in case of unlocking masterBranch direct operations and true in case of locking
//            repository.update();
//
//            System.out.println("Master branch lock updated successfully!");
//        }
//    }

    // Helper methods
    protected Platform authenticateGitanaForUpload() throws Exception {
        String baseURL = "";

//        String gitanaClientKey = "";
//        String gitanaClientCode = "";
//        String gitanaUsername = "*****";
//        String gitanaUserCode = "*******";
//
//        Gitana gitana = new Gitana(null, gitanaClientKey, gitanaClientCode, baseURL);
//        Platform platform = gitana.authenticate(gitanaUsername, gitanaUserCode);

        Gitana gitana = new Gitana();
        Platform platform = gitana.authenticate("admin", "admin");

        uploadDriver = DriverContext.getDriver();

        return platform;
    }

    protected Repository getRepository(String clientId, Platform platform) throws Exception {
        authenticateGitanaForUpload();
        ObjectNode query = null;
        try {

            Repository repo = null;

            String repoTitle = clientId + " 'content' repository";
            query = QueryBuilder.start(Repository.FIELD_TITLE).is(repoTitle).get();
            if (platform != null) {

                repo = handleRepositoryQuery(platform, clientId, query);

            }

            return repo;
        } catch (Exception e) {

        }
        return null;
    }

    private Repository handleRepositoryQuery(Platform platform, String clientId2, ObjectNode query) {

        try {
            if (query != null) {
                return platform.queryRepositories(query).asList().get(0);
            } else {

                return null;
            }
        } catch (Exception e) {

            return null;

        }
    }

}