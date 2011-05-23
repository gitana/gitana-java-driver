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

package org.gitana.repo.client;

import org.apache.commons.httpclient.HttpClient;
import org.gitana.repo.client.exceptions.AuthenticationFailedException;
import org.gitana.repo.client.exceptions.RemoteServerException;
import org.gitana.repo.client.support.ObjectFactoryImpl;
import org.gitana.repo.client.support.Remote;
import org.gitana.repo.client.support.RemoteImpl;
import org.gitana.repo.client.support.ServerImpl;
import org.gitana.repo.client.types.*;

import java.util.ResourceBundle;

/**
 * @author uzi
 */
public class Gitana
{
    public final static String FIELD_TICKET = "ticket";

    private String host;
    private int port;

    /**
     * Default constructor - loads from properties bundle
     */
    public Gitana()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("gitana");

        this.host = bundle.getString("gitana.server.host");
        this.port = Integer.parseInt(bundle.getString("gitana.server.port"));

        init();
    }

    public Gitana(String host, int port)
    {
        this.host = host;
        this.port = port;

        init();
    }

    protected void init()
    {
    }

    protected Remote getAnonymousRemote()
    {
        HttpClient client = new HttpClient();

        return new RemoteImpl(client, "http://" + this.host + ":" + this.port);
    }

    /**
     * Authenticates as the given user.
     *
     * @param username
     * @param password
     */
    public Server authenticate(String username, String password)
    {
        // log in and fetch ticket anonymously
        Response result = null;
        try
        {
            result = getAnonymousRemote().get("/security/login?u=" + username + "&p=" + password);
            if (result.isError())
            {
                throw new RemoteServerException(result);
            }
        }
        catch (Exception ex)
        {
            throw new AuthenticationFailedException(ex);
        }

        // ticket
        String ticket = result.getObjectNode().get(FIELD_TICKET).getTextValue();

        // build driver instance
        Driver driver = new Driver(this.host, this.port, username, ticket);

        // hand back server
        return new ServerImpl(driver);
    }
}
