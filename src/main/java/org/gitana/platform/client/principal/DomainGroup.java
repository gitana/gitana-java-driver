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
package org.gitana.platform.client.principal;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.node.type.Group;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface DomainGroup extends DomainPrincipal
{
    // CHILDREN

    public ResultMap<DomainPrincipal> listPrincipals();

    public ResultMap<DomainPrincipal> listPrincipals(boolean includeInherited);


    // ADD / REMOVE CHILDREN

    public void addPrincipal(DomainPrincipal principal);

    public void addPrincipal(String principalId);

    public void removePrincipal(DomainPrincipal principal);

    public void removePrincipal(String principalId);

    public Group readGroup(Branch branch);
    public Group readGroup(Branch branch, boolean createIfNotFound);
}