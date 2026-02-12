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
package org.gitana.platform.load;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.gitana.platform.client.support.Remote;

/**
 * @author uzi
 */
public class MultiThreadDownloadRunner extends AbstractRunner<RunnerResponse>
{
    private Remote remote = null;

    public void setRemote(Remote remote)
    {
        this.remote = remote;
    }

    public MultiThreadDownloadRunner(String id)
    {
        super(id);
    }

    @Override
    protected void doBeforeExecute() throws Exception
    {
    }

    @Override
    protected void doAfterExecute() throws Exception
    {
    }

    @Override
    protected RunnerResponse doExecute() throws Exception
    {
        long t1 = System.currentTimeMillis();
        HttpResponse httpResponse = remote.getEx("http://localhost:8080/favicon.ico");
        EntityUtils.consumeQuietly(httpResponse.getEntity());
        long t2 = System.currentTimeMillis();

        return new RunnerResponse(t2 - t1);
    }
}
