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

import org.gitana.platform.client.application.Application;
import org.gitana.platform.client.application.Settings;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.support.QueryBuilder;
import org.junit.Test;

import java.util.Arrays;

/**
 * Application settings tests
 * 
 * @author uzi
 */
public class SettingsTest extends AbstractTestCase
{
    @Test
    public void testApplicationSettings()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");
        
        // create an application
        Application application = platform.createApplication();
        
        // get the settings for the app
        Settings settings = application.readApplicationSettings();
        settings.set("theme", "blue");
        settings.set("dashlets", Arrays.asList("dashlet1", "dashlet2", "dashlet3", "dashlet4"));
        settings.update();
        
        // read back settings and verify values
        settings = application.readApplicationSettings();
        assertEquals("blue", settings.getString("theme"));
        assertEquals(4, settings.getArray("dashlets").size());

        // verify that this app settings can be picked off via a query
        assertEquals(1, application.querySettings(QueryBuilder.start("scope").is("application").get()).size());
        
        // query for app settings with dashlets = "dashlet2"
        assertEquals(1, application.querySettings(QueryBuilder.start("scope").is("application").and("dashlets").is("dashlet2").get()).size());
    }

    @Test
    public void testApplicationPrincipalSettings()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // create some users
        DomainUser user1 = platform.readPrimaryDomain().createUser("user1-" + System.currentTimeMillis(), "pw");
        DomainUser user2 = platform.readPrimaryDomain().createUser("user2-" + System.currentTimeMillis(), "pw");
        DomainUser user3 = platform.readPrimaryDomain().createUser("user3-" + System.currentTimeMillis(), "pw");

        // create an application
        Application application = platform.createApplication();
        
        // user settings #1
        Settings userSettings1 = application.readApplicationPrincipalSettings(user1);
        userSettings1.set("theme", "red");
        userSettings1.set("dashlets", Arrays.asList("dashlet1", "dashlet2"));
        userSettings1.update();

        // user settings #2
        Settings userSettings2 = application.readApplicationPrincipalSettings(user2);
        userSettings2.set("theme", "blue");
        userSettings2.set("dashlets", Arrays.asList("dashlet2", "dashlet3"));
        userSettings2.update();

        // user settings #3
        Settings userSettings3 = application.readApplicationPrincipalSettings(user3);
        userSettings3.set("theme", "blue");
        userSettings3.set("dashlets", Arrays.asList("dashlet4"));
        userSettings3.update();

        // read back and verify
        assertEquals(3, application.querySettings(QueryBuilder.start("scope").is("principal").get()).size());
        assertEquals(1, application.querySettings(QueryBuilder.start("theme").is("red").get()).size());
        assertEquals(2, application.querySettings(QueryBuilder.start("theme").is("blue").get()).size());
        assertEquals(1, application.querySettings(QueryBuilder.start("dashlets").is("dashlet1").get()).size());
        assertEquals(2, application.querySettings(QueryBuilder.start("dashlets").is("dashlet2").get()).size());
        assertEquals(1, application.querySettings(QueryBuilder.start("dashlets").is("dashlet3").get()).size());
        assertEquals(1, application.querySettings(QueryBuilder.start("dashlets").is("dashlet4").get()).size());
    }

}
