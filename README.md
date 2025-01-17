# Cloud CMS Java Driver

The Cloud CMS Java Driver is an Apache 2.0 licensed client library that you can drop into your Java or Android
applications to connect to Cloud CMS.  It provides a nice client interface with multi-threaded support that works
well with modern Java applications including those that run on mobile devices and the Spring Framework.

The Cloud CMS Java driver handles all of the HTTPS calls, data conversions, OAuth2 authorization handshakes and any and
all interaction with the Cloud CMS platform in a secure manner.  It works against Cloud CMS running on our SaaS platform
as well as on-premise installations at any endpoint you configure.

## Maven Dependency

To include this driver in your projects, add the following repository to your build:

````
<repositories>
    <repository>
        <id>cloudcms-public</id>
        <name>cloudcms-public</name>
        <url>https://maven.cloudcms.com/public</url>
    </repository>
</repositories>
````

And then add the following Maven dependency:

````
<dependencies>
    <dependency>
        <groupId>org.gitana</groupId>
        <artifactId>gitana-java-driver</artifactId>
        <version>1.0.26</version>
    </dependency>
</dependencies>
````

## Connecting to Cloud CMS

To connect to Cloud CMS, use the static `Gitana.connect()` method.  You can connect using a resource bundle, a JSON
configuration object or a Map of properties.

The following API Key properties are required:

- `clientKey`
- `clientSecret`
- `username`
- `password`

These properties are available with your Cloud CMS subscription.

In addition, you may wish to supply a `baseURL` to indicate an alternative location for the Cloud CMS API endpoint.
By default the `baseURL` is `https://api.cloudcms.com`.

The following method calls are equivalent -

````
ResourceBundle bundle = ...;
Platform platform1 = Gitana.connect(bundle);

ObjectNode config = ...;
Platform platform2 = Gitana.connect(config);

Map<String, String> properties = ...;
Platform platform3 = Gitana.connect(properties);
````

## Examples

Once you've connected, you may wish to query for repositories:

````
ResultMap<Repository> repositories = platform.listRepositories();
for (Repository repository: repositories.values()) {
    System.out.println("Found repository: " + repository.getId());
}
````

Or query for content from the master branch of a known repository:

````
Repository repository = platform.readRepository("abcdef123456");
Branch branch = repository.readBranch("master");

// the query we want to run (using MongoDB syntax)
ObjectNode query = JsonUtil.createObject();
query.put("category", "large");

// run query
ResultMap<Node> nodes = branch.queryNodes(query);
for (Node node: nodes.values()) {
    System.out.println("Found content item: " + node.stringify(true));
}
````

## Multithreading

When you connect to Cloud CMS using the `Gitana.connect()` method, a thread local is populated that stores the driver
context.  In other words, the authentication is stored on the current thread.

After authenticating, you can access the authentication state like this:

````
Driver driver = DriverContext.getDriver();
````

If you decide to spin up a second thread, you can either re-authenticate on that thread or you can copy the `Driver`
context over and use it at zero code.  On the new thread, simply call:

````
DriverContext.setDriver(driver);
````

Note that `Driver` is not serializable.  That said, it is a common practice to cache the `Driver` instances in a
singleton or producer class to reuse them across requests.  You may also wish to cache other Cloud CMS objects such as
`Platform`, `Repository`, `Branch` and `Node`s.

## Access and Refresh Tokens

The driver automatically manages access tokens and refresh tokens for you.  Access and refresh tokens are acquired when
the driver connects and are maintained during the lifecycle of the driver.  Access tokens (also referred to as bearer
tokens) are automatically applied to your requests when they are sent to Cloud CMS.  They are used to assert the identity
of the user making the connection.  If an access token expires (or is otherwise rejected), the refresh token is used
automatically to acquire a new access token.

The driver's goal is make this transparent so that developers need not worry about handling intermittent or unpredictable
authentication issues that may arise.  Bear in mind that an access token may expire at any time (or be forcibly expired)
and so the handling of 401 unauthenticated status codes is ever-present.  The driver's goal is make this easier for you
developer so that they don't have to handle these situations each and every time.

The drivers supports two modes to assist with this - a "normal" mode and an "automatic" mode.

In normal mode, the call is made to the remote endpoint, committing the request stream.  If a 401 is received, the
driver simply invalidates the access token locally and allows the request to fail.  In this case, the invoker gets back
a 401.  However, the next request will see a null access token and will pre-emptively acquire a new access token ahead
of committing the request.  Thus, in this scenario, a failed call is allowed provided that the next one will work
as expected.  This is very HTTP friendly and some would argue is more "pure".

In "automatic" mode, the driver attempts to make a call and if it gets back an Invalid Access Token error, it then makes
another call using the refresh token to acquire a new access token.  Once acquired, the original call is attempted again.
If it fails a second time, the second response is handed back to the invoker.

To make this possible in "automatic" mode, the driver first caches the request stream to disk.  That way, if the original
call fails with a 401, it can re-stream the second request after the access token was acquired.  This make things perfectly
seamless from a usage perspective but does introduce a few interesting factors:

1.  There is a performance penalty due to the stream caching to disk.  The impact of this will depend on the size of the
stream and the performance of your disk.  It may be fairly negligible for small stream sizes.
2.  This requires temp file space on disk.  You will need to make sure you size partitions accordingly.

By default, the driver operates in "normal" mode.  To switch automatic mode on, you can use the following system
property (see system properties below):

- `gitana.useAutomaticReattempt` - (boolean)


## System Properties

The following system properties can be used to adjust the driver's behavior.  These can either be plugged into
`System.getProperties()` or you can supply them at launch time using `-D` switches.

- `gitana.socketSoTimeout` - (ms) socket timeout (default: 10 minutes)
- `gitana.requestConnectTimeout` - (ms) time to establish connection with remote (default: 2 minutes)
- `gitana.requestSocketTimeout` - (ms) how long to wait for data to start coming back (default: 10 minutes)
- `gitana.connectionRequestTimeout` - (ms) how long to wait for a connection from the pool (default: 1 minute)

## Proxy

Use the following system properties to control the HTTP proxy:

- http.proxyHost
- http.proxyPort
- https.proxyHost
- https.proxyPort

## More Examples

One very good place to look for examples of the Cloud CMS Java Driver in use is within the source code itself.  The
unit tests work through a variety of interesting use cases and may give you ideas of some of the ways you can use the
driver to accomplish what you need.

## Resources

* Gitana: https://gitana.io
* Git Hub: http://github.com/gitana/gitana-java-driver
* Java Driver Download: http://maven.cloudcms.com/public/org/gitana/gitana-java-driver/
* Java Driver Documentation: https://gitana.io/documentation.html
* Developers Guide: https://gitana.io/developers.html

## Support

For information or questions about the Java Driver, please contact Gitana
at <a href="mailto:support@gitana.io">support@gitana.io</a>.
