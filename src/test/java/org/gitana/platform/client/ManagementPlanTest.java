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

import org.gitana.platform.client.management.Management;
import org.gitana.platform.client.management.ManagementImpl;
import org.gitana.platform.client.management.Plan;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class ManagementPlanTest extends AbstractTestCase
{
    @Test
    public void testDefaultPlans()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Management management = new ManagementImpl(platform, "default");

        // query to make sure that the default plans exist

        Plan starter = management.readPlan("starter");
        assertNotNull(starter);

        Plan personal = management.readPlan("personal");
        assertNotNull(personal);

        Plan basic = management.readPlan("basic");
        assertNotNull(basic);

        Plan premium = management.readPlan("premium");
        assertNotNull(premium);

        Plan pro = management.readPlan("pro");
        assertNotNull(pro);
    }

    @Test
    public void testPlanCRUD()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Management management = new ManagementImpl(platform, "default");

        // count current plans
        int currentSize = management.listPlans().size();

        String planKey = "test-" + System.currentTimeMillis();

        try
        {
            Plan plan = management.createPlan(planKey);
            assertEquals(planKey, plan.getPlanKey());

            assertEquals(currentSize + 1, management.listPlans().size());

            ResultMap<Plan> plans = management.queryPlans(QueryBuilder.start(Plan.FIELD_PLAN_KEY).is(planKey).get());
            assertEquals(1, plans.size());

            plan.set("abc", "def");
            management.updatePlan(plan);

            Plan testPlan = management.readPlan(planKey);
            assertEquals("def", testPlan.getString("abc"));
        }
        finally
        {
            if (management.readPlan(planKey) != null)
            {
                management.deletePlan(planKey);
            }
        }
    }

}
