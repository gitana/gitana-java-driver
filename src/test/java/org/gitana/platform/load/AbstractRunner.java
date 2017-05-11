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

package org.gitana.platform.load;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

/**
 * @author uzi
 */
public abstract class AbstractRunner<V> implements Runner<V>
{
    private String id = null;

    public AbstractRunner(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    public void init() throws Exception
    {
        doInit();
    }

    protected void doInit() throws Exception
    {

    }

    @Override
    public V call() throws Exception
    {
        V result = null;

        long t1 = -1;
        try
        {
            doBeforeExecute();

            // execute
            t1 = System.currentTimeMillis();
            result = doExecute();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            handleCallException(ex);

            throw ex;
        }
        finally
        {
            if (t1 > -1)
            {
                long t2 = System.currentTimeMillis() - t1;
            }

            doAfterExecute();

            // custom handler
            handleCallFinally();
        }

        return result;
    }

    protected void doBeforeExecute() throws Exception
    {

    }

    protected abstract V doExecute() throws Exception;

    protected void doAfterExecute() throws Exception
    {

    }

    protected void handleCallException(Exception ex)
    {

    }

    protected void handleCallFinally()
    {

    }

    protected MetricRegistry metrics()
    {
        return MetricUtil.registry();
    }

    protected Histogram histogram(String histogramId)
    {
        return metrics().histogram(histogramId);
    }

    protected void mark(String histogramId, long time)
    {
        metrics().histogram(histogramId).update(time);
    }

}
