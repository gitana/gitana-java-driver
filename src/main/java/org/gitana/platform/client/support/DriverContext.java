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

package org.gitana.platform.client.support;

import org.gitana.platform.client.Driver;

/**
 * @author uzi
 */
public class DriverContext
{
    private static ThreadLocal<DriverContext> instance = new ThreadLocal<DriverContext>();

    private Driver driver;

    public static void setDriver(Driver driver)
    {
        DriverContext context = get();
        context.driver = driver;
    }

    public static Driver getDriver()
    {
        DriverContext context = get();

        return context.driver;
    }

    private static DriverContext get()
    {
        DriverContext context = instance.get();
        if (context == null)
        {
            context = new DriverContext();

            DriverContext.instance.set(context);
        }

        return context;
    }

    public static void releaseDriver()
    {
        instance.remove();
    }
}
