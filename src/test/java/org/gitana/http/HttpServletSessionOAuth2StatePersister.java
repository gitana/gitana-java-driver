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
package org.gitana.http;

import javax.servlet.http.HttpSession;

/**
 * Persists OAuth2State to servlet session.
 *
 * @author uzi
 */
public class HttpServletSessionOAuth2StatePersister extends AbstractOAuth2StatePersister
{
    private HttpSession session = null;

    public HttpServletSessionOAuth2StatePersister(HttpSession session)
    {
        this.session = session;
    }

    @Override
    protected OAuth2State doRead()
    {
        if (session == null)
        {
            return null;
        }

        return (OAuth2State) session.getAttribute("oauth2State");
    }

    @Override
    protected void doWrite(OAuth2State state)
    {
        session.setAttribute("oauth2State", state);
    }
}
