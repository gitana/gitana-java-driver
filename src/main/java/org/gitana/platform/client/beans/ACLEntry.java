/**
 * Copyright 2025 Gitana Software, Inc.
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
package org.gitana.platform.client.beans;

import java.util.List;

/**
 * @author uzi
 */
public class ACLEntry
{
    private String principalId;

    private List<String> authorities;

    public ACLEntry()
    {
    }

    public void setPrincipal(String principalId)
    {
        this.principalId = principalId;
    }

    public String getPrincipal()
    {
        return this.principalId;
    }

    public void setAuthorities(List<String> authorities)
    {
        this.authorities = authorities;
    }

    public List<String> getAuthorities()
    {
        return this.authorities;
    }
}