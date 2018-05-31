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

import org.junit.Test;

import java.util.List;

/**
 * @author uzi
 */
public class MultiThreadConcurrentToken2Test
        extends AbstractLoadTest<Void>
{
    @Override
    protected Runner<Void> createRunner(String id, int index)
    {
        return new MultiThreadConcurrentToken2TestRunner(index);
    }

    @Override
    protected int getNumberOfRunners()
    {
        return 5;
    }

    @Override
    protected int getIterationCount()
    {
        return getNumberOfRunners() * 1;
    }

    @Test
    public void test1()
        throws Exception
    {
        long t1 = System.currentTimeMillis();
        List<RunnerResult<Void>> runners = execute();
        long totalTime = System.currentTimeMillis() - t1;

        float timePerRunner = ((float) ((float) totalTime) / ((float) getIterationCount()));

        // read back the histogram
        System.out.println("Total time: " + totalTime + ", time per runner: " + timePerRunner);
    }

}
