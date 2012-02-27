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

package org.gitana.platform.client.identity;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.directory.DirectoryDocument;
import org.gitana.platform.client.registrar.Registrar;
import org.gitana.platform.client.support.Selfable;
import org.gitana.platform.support.ResultMap;

/**
 * @author uzi
 */
public interface Identity extends DirectoryDocument, Selfable
{
    /**
     * Changes the password for this identity.
     *
     * @param password
     * @param verifyPassword
     */
    public void changePassword(String password, String verifyPassword);

    public ResultMap<ObjectNode> findUserObjects();
    public ObjectNode findUserObjectForTenant(String tenantId);

    /**
     * @return the tenants that this identity participates in on this platform
     */
    public ResultMap<ObjectNode> findTenantObjects();

    /**
     * Retrieves the tenants that this identity participates in within the specified registry.
     *
     * @param registrar
     * @return
     */
    public ResultMap<ObjectNode> findTenantObjects(Registrar registrar);
}
