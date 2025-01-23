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
package org.gitana.platform.client.accesspolicy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gitana.platform.client.platform.PlatformDocument;
import org.gitana.platform.client.support.AccessControllable;
import org.gitana.platform.client.support.Selfable;

import java.util.List;

/**
 * @author uzi
 */
public interface AccessPolicy extends PlatformDocument, AccessControllable, Selfable
{
    public final static String FIELD_SCOPE = "scope";

    public final static String FIELD_ORDER = "order";

    public final static String FIELD_STATEMENTS = "statements";

    public final static String FIELD_STATEMENT_ACTION = "action"; // grant, revoke
    public final static String FIELD_STATEMENT_ROLES = "roles";
    public final static String FIELD_STATEMENT_CONDITIONS = "conditions";
    public final static String FIELD_STATEMENT_CONDITION_TYPE = "type";
    public final static String FIELD_STATEMENT_CONDITION_CONFIG = "config";

    public String getScope();
    public void setScope(String scope);

    public int getOrder();
    public void setOrder(int order);

    public List<ObjectNode> getStatements();
    public void setStatements(List<ObjectNode> statements);

}
