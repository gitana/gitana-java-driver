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
 *   info@gitana.io
 */
package org.gitana.platform.client.support;

/**
 * @author uzi
 */
public class RemoteContextThreadLocal
{
    private static ThreadLocal<RemoteContextThreadLocal> instance = new ThreadLocal<RemoteContextThreadLocal>();

    private RemoteContext remoteContext;

    public static void setRemoteContext(RemoteContext remoteContext)
    {
        RemoteContextThreadLocal threadLocal = get();
        threadLocal.remoteContext = remoteContext;
    }

    public static RemoteContext getRemoteContext()
    {
        RemoteContextThreadLocal threadLocal = get();

        return threadLocal.remoteContext;
    }

    private static RemoteContextThreadLocal get()
    {
        RemoteContextThreadLocal context = instance.get();
        if (context == null)
        {
            context = new RemoteContextThreadLocal();

            RemoteContextThreadLocal.instance.set(context);
        }

        return context;
    }

    public static void release()
    {
        instance.remove();
    }
}
