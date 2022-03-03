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
 *   info@cloudcms.com
 */
package org.gitana.platform.load;

import java.util.concurrent.Future;

/**
 * @author uzi
 */
public class RunnerResult<V>
{
    private Future<V> future;
    private V value;
    private Exception exception;

    public RunnerResult(Future<V> future)
    {
        this.future = future;
    }

    public RunnerResult(V value)
    {
        this.value = value;
    }

    public RunnerResult()
    {
    }

    public V get()
    {
        // if we got an exception, throw that
        if (this.exception != null)
        {
            throw new RuntimeException(exception);
        }

        // if we have a tangible value, return it
        if (this.value != null)
        {
            return this.value;
        }

        // otherwise, wait for the async operation to complete
        V result = null;
        try
        {
            result = future.get();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return result;
    }

    public boolean isReady()
    {
        boolean ready = false;

        if (this.value != null)
        {
            ready = true;
        }
        else
        {
            ready = future.isDone();
        }

        return ready;
    }

    public void setException(Exception exception)
    {
        this.exception = exception;
    }

    public Exception getException()
    {
        return this.exception;
    }
}
