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
package org.sample;

import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.ResultMap;

public class Sample
{
    public static void main(String[] args)
    {
        String clientKey = "73bcd400-b1ea-416e-a483-89621a6ac13b";
        String clientSecret = "RUz7kg2CnCdhlVQSJRUNjvdMxs3+6r/iwUiTZoUykLwIXL1OVIKe4PfTferBKXMEIDe1iQgPvFJLL1kY8lb4HjpLdQZmXg0C3omyQGg06SA=";
        String username = "62d59da0-935a-4b40-9808-d690bcf6ada5";
        String password = "xN+N9I6PrQ35ZX3rnUZ3gIVEZEwSb5JVG3cqQgLNtKcRHpzdgxNkWoEQ5clqjRyHdEWzk50hq3E0vYoGkaS+dhnPP99ekOfywnO6xNsgcyA=";

        Gitana gitana = new Gitana(clientKey, clientSecret);
        Platform platform = gitana.authenticate(username, password);

        ResultMap<Repository> repositories = platform.listRepositories();
        for (Repository repository: repositories.values())
        {
            System.out.println("Found repository: " + repository.getId());
        }
    }
}
