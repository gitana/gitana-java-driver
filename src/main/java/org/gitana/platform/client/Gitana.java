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
 *   info@cloudcms.com
 */
package org.gitana.platform.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.gitana.http.HttpClientConfiguration;
import org.gitana.http.OAuth2HttpMethodExecutor;
import org.gitana.http.OAuth2TokenRequestConfigurer;
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
import org.gitana.platform.client.util.DriverUtil;
import org.gitana.util.HttpCredentials;
import org.gitana.util.HttpProxyConfiguration;
import org.gitana.util.HttpUtil;
import org.gitana.util.JsonUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author uzi
 */
public class Gitana
{
    private String baseURL;

    private String clientKey;
    private String clientSecret;

    private static Map<String, Lock> LOCKS = new HashMap<String, Lock>();

    private synchronized static Lock getOrCreateLock(String key) {

        Lock lock = LOCKS.get(key);
        if (lock == null)
        {
            lock = new ReentrantLock();
            LOCKS.put(key, lock);
        }

        return lock;
    }

    public String getBaseURL()
    {
        return this.baseURL;
    }

    public static Platform connect(ResourceBundle bundle)
    {
        return connect(bundle, null);
    }

    public static Platform connect(ResourceBundle bundle, ConnectOptions connectOptions)
    {
        String clientKey = DriverUtil.readKey(bundle, "clientKey");
        String clientSecret = DriverUtil.readKey(bundle, "clientSecret");

        String username = DriverUtil.readKey(bundle, "username");
        if (username == null)
        {
            username = DriverUtil.readKey(bundle, "gitana.username");
        }
        if (username == null)
        {
            username = DriverUtil.readKey(bundle, "gitana.credentials.username");
        }
        if (username == null)
        {
            username = DriverUtil.readKey(bundle, "authGrantKey");
        }

        String password = DriverUtil.readKey(bundle, "password");
        if (password == null)
        {
            password = DriverUtil.readKey(bundle, "gitana.password");
        }
        if (password == null)
        {
            password = DriverUtil.readKey(bundle, "gitana.credentials.password");
        }
        if (password == null)
        {
            password = DriverUtil.readKey(bundle, "authGrantSecret");
        }

        String baseURL = DriverUtil.readKey(bundle, "baseURL");

        return new Gitana(null, clientKey, clientSecret, baseURL).authenticate(username, password, connectOptions);
    }

    public static Platform connect(ObjectNode config)
    {
        return connect(config, null);
    }

    public static Platform connect(ObjectNode config, ConnectOptions connectOptions)
    {
        String clientKey = JsonUtil.objectGetString(config, "clientKey");
        String clientSecret = JsonUtil.objectGetString(config, "clientSecret");

        String username = JsonUtil.objectGetString(config, "username");
        if (username == null)
        {
            username = JsonUtil.objectGetString(config, "authGrantKey");
        }

        String password = JsonUtil.objectGetString(config, "password");
        if (password == null)
        {
            password = JsonUtil.objectGetString(config, "authGrantSecret");
        }

        String baseURL = JsonUtil.objectGetString(config, "baseURL");

        return new Gitana(null, clientKey, clientSecret, baseURL).authenticate(username, password, connectOptions);
    }

    public static Platform connect(Map<String, String> properties)
    {
        return connect(properties, null);
    }

    public static Platform connect(Map<String, String> properties, ConnectOptions connectOptions)
    {
        String clientKey = properties.get("clientKey");
        String clientSecret = properties.get("clientSecret");

        String username = properties.get("username");
        if (username == null)
        {
            username = properties.get("authGrantKey");
        }

        String password = properties.get("password");
        if (password == null)
        {
            password = properties.get("authGrantSecret");
        }

        String baseURL = properties.get("baseURL");

        return new Gitana(null, clientKey, clientSecret, baseURL).authenticate(username, password, connectOptions);
    }

    public static Platform connect()
    {
        return new Gitana(null, null, null, null).authenticate();
    }

    public static Platform connect(ConnectOptions connectOptions)
    {
        return new Gitana(null, null, null, null).authenticate(connectOptions);
    }

    public static Platform connect(String clientKey, String clientSecret, String username, String password, String baseURL)
    {
        return connect(clientKey, clientSecret, username, password, baseURL, null);
    }

    public static Platform connect(String clientKey, String clientSecret, String username, String password, String baseURL, ConnectOptions connectOptions)
    {
        return new Gitana(null, clientKey, clientSecret, baseURL).authenticate(username, password, connectOptions);
    }

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
        this(environmentId, clientKey, clientSecret, null);
    }

    public Gitana(String environmentId, String clientKey, String clientSecret, String baseURL)
    {
        this.clientKey = clientKey;
        this.clientSecret = clientSecret;
        this.baseURL = baseURL;

        if (this.baseURL == null)
        {
            if (environmentId == null)
            {
                environmentId = Environment.DEFAULT;
            }

            // load environment properties
            ResourceBundle environmentsBundle = readBundle("gitana-environments");
            if (environmentsBundle != null)
            {
                if (environmentsBundle.containsKey("gitana.environment." + environmentId + ".uri"))
                {
                    this.baseURL = environmentsBundle.getString("gitana.environment." + environmentId + ".uri");
                }
            }
        }

        if (this.baseURL == null)
        {
            this.baseURL = "https://api.cloudcms.com";
        }

        // load api keys?
        ResourceBundle apiKeysBundle = readBundle("gitana");
        if (apiKeysBundle != null)
        {
            // load client properties if not provided
            if (clientKey == null && clientSecret == null)
            {
                if (apiKeysBundle.containsKey("gitana.clientKey"))
                {
                    this.clientKey = apiKeysBundle.getString("gitana.clientKey");
                }

                // legacy support
                if (this.clientKey == null && apiKeysBundle.containsKey("gitana.clientId"))
                {
                    this.clientKey = apiKeysBundle.getString("gitana.clientId");
                }

                this.clientSecret = apiKeysBundle.getString("gitana.clientSecret");
            }

            // allow baseURL
            if (this.baseURL == null)
            {
                if (apiKeysBundle.containsKey("gitana.baseURL"))
                {
                    this.baseURL = apiKeysBundle.getString("gitana.baseURL");
                }
            }
        }
    }

    /**
     * Creates an anonymous remote that can be used to access any resources on the Gitana server that are not
     * protected behind oauth.
     *
     * @return remote
     */
    protected RemoteImpl createRemote() {
        return createRemote(null);
    }

    /**
     * Creates an anonymous remote that can be used to access any resources on the Gitana server that are not
     * protected behind oauth.
     *
     * @param clientConfiguration
     *
     * @return remote
     */
    protected RemoteImpl createRemote(HttpClientConfiguration clientConfiguration)
    {
        HttpCredentials credentials = null;
        HttpProxyConfiguration proxyConfig = null;

        if (baseURL != null && baseURL.toLowerCase().startsWith("http://"))
        {
            String httpProxyHost = System.getProperty("http.proxyHost");
            String httpProxyPort = System.getProperty("http.proxyPort");
            if (httpProxyHost != null && !"".equals(httpProxyHost))
            {
                if (httpProxyPort != null && !"".equals(httpProxyPort))
                {
                    Integer _httpProxyPort = Integer.parseInt(httpProxyPort);

                    proxyConfig = new HttpProxyConfiguration();
                    proxyConfig.setHost(httpProxyHost);
                    proxyConfig.setPort(_httpProxyPort);
                }
            }
        }

        if (baseURL != null && baseURL.toLowerCase().startsWith("https://"))
        {
            String httpsProxyHost = System.getProperty("https.proxyHost");
            String httpsProxyPort = System.getProperty("https.proxyPort");
            if (httpsProxyHost !=null && !"".equals(httpsProxyHost))
            {
                if (httpsProxyPort !=null && !"".equals(httpsProxyPort))
                {
                    Integer _httpsProxyPort = Integer.parseInt(httpsProxyPort);

                    proxyConfig = new HttpProxyConfiguration();
                    proxyConfig.setHost(httpsProxyHost);
                    proxyConfig.setPort(_httpsProxyPort);
                }
            }
        }

        // build a client, disable redirects, plug in any proxy config
        HttpClient client = HttpUtil.buildClient(false, credentials, proxyConfig, clientConfiguration);

        // wrap into a remote object
        return new RemoteImpl(client, baseURL);
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
        return authenticate(null);
    }

    public Platform authenticate(ConnectOptions connectOptions)
    {
        return authenticate((String)null, (String)null, connectOptions);
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
        return authenticate(user, password, null);
    }

    public Platform authenticate(DomainUser user, String password, ConnectOptions connectOptions)
    {
        return authenticate(user.getDomainQualifiedId(), password, connectOptions);
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
        return authenticate(username, password, null);
    }

    public Platform authenticate(String username, String password, final ConnectOptions connectOptions)
    {
        ResourceBundle bundle = readBundle("gitana");

        // if username and password are null, authenticate using credentials supplied in properties file
        if (username == null && password == null)
        {
            if (bundle.containsKey("username"))
            {
                username = bundle.getString("username");
            }
            if (username == null && bundle.containsKey("gitana.username"))
            {
                username = bundle.getString("gitana.username");
            }
            if (username == null && bundle.containsKey("gitana.credentials.username"))
            {
                username = bundle.getString("gitana.credentials.username");
            }

            if (bundle.containsKey("password"))
            {
                password = bundle.getString("password");
            }
            if (password == null && bundle.containsKey("gitana.password"))
            {
                password = bundle.getString("gitana.password");
            }
            if (password == null && bundle.containsKey("gitana.credentials.password"))
            {
                password = bundle.getString("gitana.credentials.password");
            }
        }

        RemoteImpl remote = createRemote(acquireClientConfiguration());

        // apply OAuth2 HTTP Method Executor
        OAuth2HttpMethodExecutor httpMethodExecutor = new OAuth2HttpMethodExecutor();
        httpMethodExecutor.setUri(this.baseURL + "/oauth/token");
        httpMethodExecutor.setClientId(this.clientKey);
        httpMethodExecutor.setClientSecret(this.clientSecret);
        httpMethodExecutor.setResourceOwnerPasswordCredentialsFlow(username, password);
        //httpMethodExecutor.setSignatureMethod(OAuth2SignatureMethod.QUERY_PARAMETER);
        httpMethodExecutor.setRequestedScope("api");

        // if we have auth options, add in a token request configurer to supply things like MFA CODE and other auth options
        if (connectOptions != null)
        {
            OAuth2TokenRequestConfigurer tokenRequestConfigurer = new OAuth2TokenRequestConfigurer() {
                @Override
                public void configure(HttpRequestBase httpMethod, String clientKey, String clientSecret, String username, String password, String refreshToken, String code, String redirectUri)
                {
                    if (connectOptions.getAuthenticatorCode() != null)
                    {
                        httpMethod.setHeader("authenticator_code", connectOptions.getAuthenticatorCode());
                    }
                }
            };

            httpMethodExecutor.setTokenRequestConfigurer(tokenRequestConfigurer);
        }
        remote.setHttpMethodExecutor(httpMethodExecutor);

        // "automatic" mode support
        boolean useAutomaticReattempt = DriverUtil.acquireBooleanFromSystemOrBundle(bundle, "gitana.useAutomaticReattempt", null, false);
        httpMethodExecutor.setUseAutomaticReattempt(useAutomaticReattempt);

        // set token lock
        Lock tokenLock = getOrCreateLock("tokenLock-" + this.baseURL);
        httpMethodExecutor.setTokenLock(tokenLock);

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
        RemoteImpl remote = createRemote(acquireClientConfiguration());

        // apply OAuth2 HTTP Method Executor
        OAuth2HttpMethodExecutor httpMethodExecutor = new OAuth2HttpMethodExecutor();
        httpMethodExecutor.setUri(this.baseURL + "/oauth/token");
        httpMethodExecutor.setClientId(this.clientKey);
        httpMethodExecutor.setClientSecret(this.clientSecret);
        httpMethodExecutor.setAuthorizationCodeFlow(code, redirectUri);
        //httpMethodExecutor.setSignatureMethod(OAuth2SignatureMethod.QUERY_PARAMETER);
        httpMethodExecutor.setRequestedScope("api");
        remote.setHttpMethodExecutor(httpMethodExecutor);

        // "automatic" mode support
        boolean useAutomaticReattempt = DriverUtil.acquireBoolean(System.getProperties(), "gitana.useAutomaticReattempt", false);
        httpMethodExecutor.setUseAutomaticReattempt(useAutomaticReattempt);

        // set token lock
        Lock tokenLock = getOrCreateLock("tokenLock-" + this.baseURL);
        httpMethodExecutor.setTokenLock(tokenLock);

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
        Response response = createRemote(acquireClientConfiguration()).get("/ping");
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

    private HttpClientConfiguration acquireClientConfiguration()
    {
        HttpClientConfiguration clientConfiguration = new HttpClientConfiguration();

        ResourceBundle bundle = readBundle("gitana");
        if (bundle != null)
        {
            clientConfiguration.socketSoTimeout = DriverUtil.acquireIntFromSystemOrBundle(bundle, "gitana.config.socketSoTimeout", "gitana.socketSoTimeout", 600000);
            clientConfiguration.requestConnectTimeout = DriverUtil.acquireIntFromSystemOrBundle(bundle, "gitana.config.requestConnectTimeout", "gitana.requestConnectTimeout", 120000);
            clientConfiguration.requestSocketTimeout = DriverUtil.acquireIntFromSystemOrBundle(bundle, "gitana.config.requestSocketTimeout", "gitana.requestSocketTimeout", 600000);
            clientConfiguration.connectionRequestTimeout = DriverUtil.acquireIntFromSystemOrBundle(bundle, "gitana.config.connectionRequestTimeout", "gitana.connectionRequestTimeout", 60000);
        }

        return clientConfiguration;
    }
}
