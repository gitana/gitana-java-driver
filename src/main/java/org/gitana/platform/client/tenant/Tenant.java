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

package org.gitana.platform.client.tenant;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.api.Consumer;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.registrar.RegistrarDocument;
import org.gitana.platform.client.repository.Repository;
import org.gitana.platform.client.vault.Vault;
import org.gitana.platform.support.Pagination;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Tenant extends RegistrarDocument
{
    // fields
    public final static String FIELD_PLAN_KEY = "planKey";
    public final static String FIELD_PRINCIPAL_ID = "principalId";
    public final static String FIELD_DOMAIN_ID = "domainId";

    public void setPlanKey(String planKey);
    public String getPlanKey();

    public void setPrincipalId(String principalId);
    public String getPrincipalId();

    public void setDomainId(String domainId);
    public String getDomainId();

    public ResultMap<ObjectNode> listAllocatedObjects();
    public ResultMap<ObjectNode> listAllocatedObjects(Pagination pagination);

    // list/query

    public ResultMap<Repository> listRepositories();
    public ResultMap<Repository> listRepositories(Pagination pagination);

    public ResultMap<Domain> listDomains();
    public ResultMap<Domain> listDomains(Pagination pagination);

    public ResultMap<Vault> listVaults();
    public ResultMap<Vault> listVaults(Pagination pagination);

    public ResultMap<Consumer> listConsumers();
    public ResultMap<Consumer> listConsumers(Pagination pagination);

    public ResultMap<Registrar> listRegistrars();
    public ResultMap<Registrar> listRegistrars(Pagination pagination);

    /**
     * Administrative method that will only work for the "owner" of the tenant.  Otherwise will return null.
     *
     * @return consumer
     */
    public Consumer readDefaultConsumer();

}
