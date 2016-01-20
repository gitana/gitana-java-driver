/**
 * Copyright 2016 Gitana Software, Inc.
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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.gitana.http.OAuth2HttpMethodExecutor;
import org.gitana.platform.client.cluster.Cluster;
import org.gitana.platform.client.cluster.ClusterImpl;
import org.gitana.platform.client.identity.Identity;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.client.support.DriverContext;
import org.gitana.platform.client.support.Environment;
import org.gitana.platform.client.support.RemoteImpl;
import org.gitana.platform.client.support.Response;
import org.gitana.platform.client.tenant.Tenant;
import org.gitana.util.HttpUtil;
import org.gitana.util.JsonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author uzi
 */
public class Gitana
{
    private String baseUrl;

    private String environmentId;
    private String clientKey;
    private String clientSecret;

    /**
     * Creates a Gitana instance that loads all of its settings from a properties bundle.
     */
    public Gitana()
    {
        this(null, null, null);
    }

    /**
     * Creates a Gitana instance bound to a Gitana API client.
     */
    public Gitana(String clientKey, String clientSecret)
    {
        this(null, clientKey, clientSecret);
    }

    /**
     * Creates a Gitana instance bound to a given client on an environment.
     *
     * @param environmentId
     * @param clientKey
     * @param clientSecret
     */
    public Gitana(String environmentId, String clientKey, String clientSecret)
    {
        if (environmentId == null)
        {
            environmentId = Environment.DEFAULT;
        }

        this.environmentId = environmentId;

        // load environment properties
        ResourceBundle bundle = readBundle("gitana-environments");
        this.baseUrl = bundle.getString("gitana.environment." + environmentId + ".uri");

        // load client properties if not provided
        if (clientKey == null && clientSecret == null)
        {
            bundle = readBundle("gitana");

            if (bundle.containsKey("gitana.clientKey"))
            {
                this.clientKey = bundle.getString("gitana.clientKey");
            }

            // legacy support
            if (this.clientKey == null && bundle.containsKey("gitana.clientId"))
            {
                this.clientKey = bundle.getString("gitana.clientId");
            }

            this.clientSecret = bundle.getString("gitana.clientSecret");
        }
        else
        {
            this.clientKey = clientKey;
            this.clientSecret = clientSecret;
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
        // build a client and disable redirects
        HttpClient client = HttpUtil.buildClient(false);

        // wrap into a remote object
        return new RemoteImpl(client, baseUrl);
    }

    /**
     * Creates an anonymous remote instance.
     *
     * @return
     */
    protected RemoteImpl createRemote()
    {
        return createAnonymousRemote();
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
     * Authenticates using the user credentials stored in the Gitana resource bundle.
     *
     * @return platform
     */
    public Platform authenticate()
    {
        return authenticate((String)null, (String)null);
    }

    /**
     * Authenticates as the guest user.
     *
     * @return platformw
     */
    public Platform authenticateAsGuest()
    {
        return authenticate("guest", "guest");
    }

    /**
     * Authenticates as the given domain user.
     *
     * @param user
     * @param password
     * @return
     */
    public Platform authenticate(DomainUser user, String password)
    {
        return authenticate(user.getDomainQualifiedId(), password);
    }

    /**
     * Authenticates as the given identity using a domain user that belongs to a domain on the given
     * tenant.  If multiple users are found, the first one is picked.
     * 
     * @param identity
     * @param password
     * @param tenantId
     * @return
     */
    public Platform authenticateOnTenant(Identity identity, String password, String tenantId)
    {
        ObjectNode userObject = identity.findPolicyUserObjectForTenant(tenantId);
        if (userObject == null)
        {
            throw new RuntimeException("Unable to find user on tenant: " + tenantId + " for identity: " + identity.getId());
        }
        
        String domainId = JsonUtil.objectGetString(userObject, "domainId");
        String principalName = JsonUtil.objectGetString(userObject, DomainUser.FIELD_NAME);
        
        return authenticate(domainId + "/" + principalName, password);
    }

    /**
     * Authenticates the user's identity using a domain user that belongs to a domain on the given
     * tenant.  If multiple users are found, the first one is picked.
     *
     * @param user
     * @param password
     * @param tenantId
     * @return
     */
    public Platform authenticateOnTenant(DomainUser user, String password, String tenantId)
    {
        Identity identity = user.readIdentity();
        if (identity == null)
        {
            throw new RuntimeException("The user: " + user.getId() + " with name: " + user.getName() + " does not have an identity associated with it");
        }

        return authenticateOnTenant(user.readIdentity(), password, tenantId);
    }

    /**
     * Authenticates the user's identity using a domain user that belongs to a domain on the given
     * tenant.  If multiple users are found, the first one is picked.
     *
     * @param user
     * @param password
     * @param tenant
     * @return
     */
    public Platform authenticateOnTenant(DomainUser user, String password, Tenant tenant)
    {
        return authenticateOnTenant(user, password, tenant.getId());
    }
    
    /**
     * Authenticates to Gitana using the given credentials.
     *
     * These credentials can be any of the following:
     *
     *    username/password (assumes the default domain of the platform)
     *    user id/password (assumes the default domain of the platform)
     *    domain qualified username / password
     *    domain qualified id / password
     *    credential key / credential secret
     *
     * This utilizes the OAuth2 resource owner username/password flow.
     *
     * @param username
     * @param password
     */
    public Platform authenticate(String username, String password)
    {
        // if username and password are null, authenticate using credentials supplied in properties file
        if (username == null && password == null)
        {
            ResourceBundle bundle = readBundle("gitana");

            username = bundle.getString("gitana.credentials.username");
            password = bundle.getString("gitana.credentials.password");
        }

        RemoteImpl remote = createRemote();

        // apply OAuth2 HTTP Method Executor
        OAuth2HttpMethodExecutor httpMethodExecutor = new OAuth2HttpMethodExecutor();
        httpMethodExecutor.setUri(this.baseUrl + "/oauth/token");
        httpMethodExecutor.setClientId(this.clientKey);
        httpMethodExecutor.setClientSecret(this.clientSecret);
        httpMethodExecutor.setResourceOwnerPasswordCredentialsFlow(username, password);
        //httpMethodExecutor.setSignatureMethod(OAuth2SignatureMethod.QUERY_PARAMETER);
        httpMethodExecutor.setRequestedScope("api");
        remote.setHttpMethodExecutor(httpMethodExecutor);

        // build driver instance
        Driver driver = new Driver(remote);
        DriverContext.setDriver(driver);

        // perform handshake and populate driver with info
        populateAuthenticationInformation(driver);

        // read back the platform
        return loadPlatform(driver);
    }

    /**
     * Authenticates with the code supplied by the Authorization Flow authorization server call.
     *
     * @param code
     * @param redirectUri
     *
     * @return platform
     */
    public Platform authenticateWithCode(String code, String redirectUri)
    {
        RemoteImpl remote = createRemote();

        // apply OAuth2 HTTP Method Executor
        OAuth2HttpMethodExecutor httpMethodExecutor = new OAuth2HttpMethodExecutor();
        httpMethodExecutor.setUri(this.baseUrl + "/oauth/token");
        httpMethodExecutor.setClientId(this.clientKey);
        httpMethodExecutor.setClientSecret(this.clientSecret);
        httpMethodExecutor.setAuthorizationCodeFlow(code, redirectUri);
        //httpMethodExecutor.setSignatureMethod(OAuth2SignatureMethod.QUERY_PARAMETER);
        httpMethodExecutor.setRequestedScope("api");
        remote.setHttpMethodExecutor(httpMethodExecutor);

        // build driver and bind to thread local context
        Driver driver = new Driver(remote);
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

    public static ResourceBundle readBundle(String bundleId)
    {
        ResourceBundle bundle = null;

        // check current file path
        File file = new File(bundleId + ".properties");
        if (file != null && file.exists())
        {
            try
            {
                FileInputStream fis = new FileInputStream(file);
                bundle = new PropertyResourceBundle(fis);

                System.out.println("Found local " + bundleId + ".properties, loading...");

            }
            catch (Exception ex)
            {
                throw new RuntimeException(ex);
            }
        }

        if (bundle == null)
        {
            bundle = ResourceBundle.getBundle(bundleId);
        }

        return bundle;
    }

}
