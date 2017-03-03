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

package org.sample;

import org.gitana.platform.client.Gitana;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.support.ResultMap;

public class Sample
{
    public static void main(String[] args)
    {
        String clientKey = "986bc26d-3213-4473-abd6-8da268b2b2dc";
        String clientSecret = "doYe+pi4zdrbZruIzLn1TPYUpzzttvuzTdfmTK31ZBWVY0SnNErW9J+1bc+5BB5uAPcWYgdFa4FVIr+jJdkdKCJA33d573dYT4Cfuet13JQ=";
        String username = "89da83d0-096a-4594-ac8a-07d9be123abe";
        String password = "ZCcoDb3tXv9FPBdsOuSvuBMIGG7hO4S9Sh5k9W2EDD/WLcHTr8HLWezqz/fSdrhN3jS4ccGvTPwApo5nESDEjCP2I0oKUpT72W8x/8KrGto=";

        Gitana gitana = new Gitana(clientKey, clientSecret);
        Platform platform = gitana.authenticate(username, password);

        ResultMap<Repository> repositories = platform.listRepositories();
        for (Repository repository: repositories.values())
        {
            System.out.println("Found repository: " + repository.getId());
        }
    }
}
