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

package org.gitana.platform.client.registrar;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.billing.PaymentMethodValidation;
import org.gitana.platform.client.plan.Plan;
import org.gitana.platform.client.platform.PlatformDataStore;
import org.gitana.platform.client.principal.DomainPrincipal;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Registrar extends PlatformDataStore
{
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // TENANTS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Tenant> listTenants();
    public ResultMap<Tenant> listTenants(Pagination pagination);
    public ResultMap<Tenant> queryTenants(ObjectNode query);
    public ResultMap<Tenant> queryTenants(ObjectNode query, Pagination pagination);
    public Tenant readTenant(String tenantId);
    public Tenant lookupTenant(DomainPrincipal principal);
    public Tenant createTenant(DomainPrincipal principal, String planKey);
    public Tenant createTenant(DomainPrincipal principal, String planKey, ObjectNode object);
    public void updateTenant(Tenant tenant);
    public void deleteTenant(Tenant tenant);
    public void deleteTenant(String tenantId);

    //public ResultMap<Tenant> findTenantsWithPrincipalTeamMember(String principalId, Pagination pagination);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // PLANS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public ResultMap<Plan> listPlans();
    public ResultMap<Plan> listPlans(Pagination pagination);
    public ResultMap<Plan> queryPlans(ObjectNode query);
    public ResultMap<Plan> queryPlans(ObjectNode query, Pagination pagination);
    public Plan readPlan(String planKey);
    public Plan createPlan(String planKey);
    public Plan createPlan(String planKey, ObjectNode object);
    public void updatePlan(Plan plan);
    public void deletePlan(Plan plan);
    public void deletePlan(String planKey);



    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CREDIT CARDS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public PaymentMethodValidation validateCreditCard(String holderName, String number, int expirationMonth, int expirationYear);
    public PaymentMethodValidation validateCreditCard(ObjectNode object);

}
