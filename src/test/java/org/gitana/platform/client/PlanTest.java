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
package org.gitana.platform.client;

import org.gitana.platform.client.plan.Plan;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.support.QueryBuilder;
import org.gitana.platform.support.ResultMap;
import org.junit.Test;

/**
 * @author uzi
 */
public class PlanTest extends AbstractTestCase
{
    @Test
    public void testDefaultPlans()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // query to make sure that the default plans exist

        Plan unlimited = registrar.readPlan("unlimited");
        assertNotNull(unlimited);
    }

    @Test
    public void testPlanCRUD()
        throws Exception
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        // default registrar
        Registrar registrar = platform.readRegistrar("default");

        // count current plans
        int currentSize = registrar.listPlans().size();

        String planKey = "test-" + System.currentTimeMillis();

        try
        {
            Plan plan = registrar.createPlan(planKey);
            assertEquals(planKey, plan.getPlanKey());

            assertEquals(currentSize + 1, registrar.listPlans().size());

            ResultMap<Plan> plans = registrar.queryPlans(QueryBuilder.start(Plan.FIELD_PLAN_KEY).is(planKey).get());
            assertEquals(1, plans.size());

            plan.set("abc", "def");
            registrar.updatePlan(plan);

            Plan testPlan = registrar.readPlan(planKey);
            assertEquals("def", testPlan.getString("abc"));
        }
        finally
        {
            if (registrar.readPlan(planKey) != null)
            {
                registrar.deletePlan(planKey);
            }
        }
    }

}
