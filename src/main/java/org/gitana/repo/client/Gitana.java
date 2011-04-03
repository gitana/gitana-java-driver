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

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.gitana.repo.client.exceptions.AuthenticationFailedException;
import org.gitana.repo.client.exceptions.RemoteServerException;
import org.gitana.repo.client.services.*;
import org.gitana.repo.client.support.ObjectFactoryImpl;
import org.gitana.repo.client.support.Remote;
import org.gitana.repo.client.support.RemoteImpl;

import java.util.ResourceBundle;

/**
 * @author uzi
 */
public class Gitana
{
    public final static String TICKET_COOKIE_NAME = "GITANA_TICKET";
    public final static String FIELD_TICKET = "ticket";

    private String host;
    private int port;

    private String ticket;

    private HttpClient client;

    private ObjectFactory factory;

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

    private void init()
    {
        // anonymous
        this.client = new HttpClient();

        // factory
        this.factory = new ObjectFactoryImpl(this);
    }

    /**
     * @return object factory
     */
    public ObjectFactory getFactory()
    {
        return this.factory;
    }

    /**
     * Request a remote bound to the given client.
     *
     * @return remote
     */
    public Remote getRemote()
    {
        // NOTE: this will incorporate connection pooling and such in the future
        // concurrency controls, etc.
        // if none, block, etc
        return new RemoteImpl(this.client, "http://" + this.host + ":" + this.port);
    }

    public String getTicket()
    {
        return this.ticket;
    }

    /**
     * Authenticates as the given user.
     *
     * @param username
     * @param password
     */
    public void authenticate(String username, String password)
    {
        // log in and fetch ticket anonymously
        Response result = null;
        try
        {
            result = getRemote().get("/security/login?u=" + username + "&p=" + new String(password));
            if (result.isError())
            {
                throw new RemoteServerException(result);
            }
        }
        catch (Exception ex)
        {
            throw new AuthenticationFailedException(ex);
        }

        this.ticket = result.getObjectNode().get(FIELD_TICKET).getTextValue();

        // build a new http client
        HttpClient httpClient = new HttpClient();

        // set a cookie into the http state
        Cookie cookie = buildCookie(this.ticket);
        httpClient.getState().addCookie(cookie);

        // replace existing client
        this.client = httpClient;
    }

    private String getDomain()
    {
        return this.host;
    }

    private Cookie buildCookie(String ticket)
    {
        Cookie cookie = new Cookie();
        cookie.setDomain(getDomain());
        cookie.setPath("/");
        cookie.setName(TICKET_COOKIE_NAME);
        cookie.setValue(ticket);

        return cookie;
    }

    public Server server()
    {
        return new Server(this);
    }

    public Repositories repositories()
    {
        return new Repositories(this);
    }

    public Branches branches(Repository repository)
    {
        return new Branches(this, repository);
    }

    public Changesets changesets(Repository repository)
    {
        return new Changesets(this, repository);
    }

    public Nodes nodes(Branch branch)
    {
        return new Nodes(this, branch);
    }

    public Users users()
    {
        return new Users(this);
    }

    public Groups groups()
    {
        return new Groups(this);
    }

}
