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

package org.gitana.platform.client;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpConnectionParams;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.cluster.ClusterImpl;
import org.gitana.platform.client.exceptions.AuthenticationFailedException;
import org.gitana.platform.client.exceptions.RemoteServerException;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.Environment;
import org.gitana.platform.client.support.RemoteImpl;
import org.gitana.platform.client.support.Response;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author uzi
 */
public class Gitana
{
    public final static String TICKET_COOKIE_NAME = "GITANA_TICKET";

    public final static String FIELD_TICKET = "ticket";

    private String baseUrl;

    private String environmentId;
    private String consumerKey;
    private String consumerSecret;

    /**
     * Creates a Gitana instance that loads all of its settings from a properties bundle.
     */
    public Gitana()
    {
        this(null, null, null);
    }

    /**
     * Creates a Gitana instance bound to a Gitana API consumer.
     */
    public Gitana(String consumerKey, String consumerSecret)
    {
        this(null, consumerKey, consumerSecret);
    }

    /**
     * Creates a Gitana instance bound to a given consumer on an environment.
     *
     * @param environmentId
     * @param consumerKey
     * @param consumerSecret
     */
    public Gitana(String environmentId, String consumerKey, String consumerSecret)
    {
        if (environmentId == null)
        {
            environmentId = Environment.DEFAULT;
        }

        this.environmentId = environmentId;

        // load environment properties
        ResourceBundle bundle = ResourceBundle.getBundle("gitana-environments");
        this.baseUrl = bundle.getString("gitana.environment." + environmentId + ".uri");

        // load consumer properties if not provided
        if (consumerKey == null && consumerSecret == null)
        {
            bundle = ResourceBundle.getBundle("gitana");

            this.consumerKey = bundle.getString("gitana.consumerKey");
            this.consumerSecret = bundle.getString("gitana.consumerSecret");
        }
        else
        {
            this.consumerKey = consumerKey;
            this.consumerSecret = consumerSecret;
        }
    }

    public String getEnvironmentId()
    {
        return environmentId;
    }

    /**
     * Creates an anonymous remote that can be used to access any resources on the Gitana server that are not
     * protected behind oauth.
     *
     * @return
     */
    protected RemoteImpl createAnonymousRemote()
    {
        // build a new http client
        DefaultHttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setSoTimeout(client.getParams(), 0);
        client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

        // wrap into a remote object
        return new RemoteImpl(client, baseUrl);
    }

    /**
     * Creates a remote that has the configured consumer keys plugged onto it.  This is useful only for calling
     * authentication methods.  Authentication methods are unprotected but they require a consumer to be identified
     * so that the oauth handshake can complete.
     *
     * @return
     */
    protected RemoteImpl createRemote()
    {
        RemoteImpl remote = createAnonymousRemote();

        // plug our consumer keys onto the remote
        OAuthConsumer oauthConsumer = new CommonsHttpOAuthConsumer(this.consumerKey, this.consumerSecret);
        remote.setOAuthConsumer(oauthConsumer);

        return remote;
    }

    private String getDomain()
    {
        String domain = null;

        try
        {
            URL url = new URL(baseUrl);
            domain = url.getHost();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return domain;
    }

    private Cookie buildCookie(String name, String value)
    {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(getDomain());
        cookie.setPath("/");

        return cookie;
    }

    /**
     * Authenticates as the given principal via username/password credentials.
     *
     * @return server
     */
    public Platform authenticateWithTokens(String accessTokenKey, String accessTokenSecret)
    {
        RemoteImpl remote = createRemote();

        // pre-load authentication tokens onto remote
        remote.getOAuthConsumer().setTokenWithSecret(accessTokenKey, accessTokenSecret);

        // build driver and bind to thread local context
        Driver driver = new Driver(remote, this.consumerKey, accessTokenKey);
        DriverContext.setDriver(driver);

        // perform handshake and populate driver with info
        populateAuthenticationInformation(driver);

        // read back the platform
        return loadPlatform(driver);
    }

    protected void populateAuthenticationInformation(Driver driver)
    {
        // load authentication info
        try
        {
            Response response = driver.getRemote().get("/auth/info");

            AuthInfo authInfo = new AuthInfo(response.getObjectNode());
            driver.setAuthInfo(authInfo);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    protected Platform loadPlatform(Driver driver)
    {
        // read back the platform
        Platform platform = null;
        try
        {
            Cluster cluster = new ClusterImpl("default");

            Response response = driver.getRemote().get("/");
            platform = driver.getFactory().platform(cluster, response);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        driver.setPlatform(platform);

        return platform;
    }

    /**
     * Authenticates as the given user.
     *
     * @param username
     * @param password
     */
    public Platform authenticate(String username, String password)
    {
        RemoteImpl remote = createRemote();

        // we front-load the oauth access token with "ticket"
        // this allows for two-legged oAuth (consumer only) where the third leg (user) is decided by a ticket instead
        // of by an access token
        remote.getOAuthConsumer().setTokenWithSecret("ticket", null);

        // log in and fetch ticket anonymously
        Response result = null;
        try
        {
            result = remote.get("/auth/login?u=" + username + "&p=" + password);
            if (result.isError())
            {
                throw new RemoteServerException(result);
            }
        }
        catch (Exception ex)
        {
            throw new AuthenticationFailedException(ex);
        }

        // clear the special "ticket" token from the remote
        //remote.getOAuthConsumer().setTokenWithSecret(null, null);

        // we store the Gitana ticket as a cookie so that it will get picked up by the Gitana
        // pre-authentication filter chain
        // this is an alternative to using pure access tokens (which would require the secret on client side)
        String ticket = result.getObjectNode().get(FIELD_TICKET).getTextValue();
        Cookie cookie = buildCookie(TICKET_COOKIE_NAME, ticket);
        remote.addCookie(cookie);

        // build driver instance
        Driver driver = new Driver(remote, this.consumerKey, ticket);
        DriverContext.setDriver(driver);

        // perform handshake and populate driver with info
        populateAuthenticationInformation(driver);

        // read back the platform
        return loadPlatform(driver);
    }

    /**
     * Pings the server and returns the number of milliseconds for pong to return.
     * If the ping fails, an exception is thrown.
     *
     * @return milliseconds
     */
    public long ping()
    {
        long ping = -1;

        long t1 = System.currentTimeMillis();
        Response response = createRemote().get("/ping");
        long t2 = System.currentTimeMillis();
        if (response.isOk())
        {
            ping = t2 - t1;
        }
        else
        {
            throw new RuntimeException("Ping call did not return ok: " + response.getMessage());
        }

        return ping;
    }
}
