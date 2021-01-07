/**
 * Copyright 2017 Gitana Software, Inc.
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

package org.gitana.platform.client.support;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.util.EntityUtils;
import org.gitana.http.HttpInvoker;
import org.gitana.http.HttpMethodExecutor;
import org.gitana.http.HttpPayload;
import org.gitana.http.HttpPayloadContentBody;
import org.gitana.util.HttpUtil;
import org.gitana.util.JsonUtil;
import org.gitana.util.StreamUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author uzi
 */
public class RemoteImpl implements Remote
{
    private static final String UTF_8 = "UTF-8";

    private HttpInvoker invoker;
    private String remoteURL;

    private boolean full;
    private boolean metadata;
    private boolean paths;

    public RemoteImpl(HttpClient client, String remoteURL)
    {
        this.remoteURL = remoteURL;

        this.full = true;
        this.metadata = true;
        this.paths = false;

        this.invoker = new HttpInvoker(client);
    }

    public void setHttpMethodExecutor(HttpMethodExecutor httpMethodExecutor)
    {
        this.invoker.setHttpMethodExecutor(httpMethodExecutor);
    }

    public HttpMethodExecutor getHttpMethodExecutor()
    {
        return this.invoker.getHttpMethodExecutor();
    }

    public void addCookie(Cookie cookie)
    {
        ((AbstractHttpClient)this.invoker.getClient()).getCookieStore().addCookie(cookie);
    }

    public String getRemoteURL()
    {
        return this.remoteURL;
    }

    public void setFull(boolean full)
    {
        this.full = full;
    }

    public void setMetadata(boolean metadata)
    {
        this.metadata = metadata;
    }

    public void setPaths(boolean paths)
    {
        this.paths = paths;
    }

    public void setLocale(Locale locale)
    {
        this.invoker.setLocale(locale);
    }

    @Override
    public void addHeader(String name, String value)
    {
        this.invoker.addHeader(name, value);
    }

    @Override
    public void setHeaders(Map<String, String> headers)
    {
        this.invoker.setHeaders(headers);
    }

    private String buildURL(String uri, boolean expand)
    {
        return buildURL(uri, null, expand);
    }

    private String buildURL(String uri, Map<String, String> params, boolean expand)
    {
        if (params == null)
        {
            params = new LinkedHashMap<String, String>();
        }

        if (expand)
        {
            buildParams(params);
        }

        return buildURL(uri, params);
    }

    private String buildURL(String uri, Map<String, String> params)
    {
        StringBuilder sb = new StringBuilder();

        if (!uri.toLowerCase().startsWith("http://") && !uri.toLowerCase().startsWith("https://"))
        {
            sb.append(this.remoteURL);
        }
        sb.append(uri);

        boolean added = false;
        if (uri.indexOf("?") > -1)
        {
            added = true;
        }

        for (String name : params.keySet())
        {
            String value = params.get(name);

            try
            {
                value = URLEncoder.encode(value, "utf-8");
            }
            catch (UnsupportedEncodingException uee)
            {
                throw new RuntimeException("Unsupported encoding: utf-8");
            }

            sb.append((added ? "&" : "?"));
            sb.append(name);
            sb.append("=");
            sb.append(value);

            added = true;
        }

        return sb.toString();
    }

    private void buildParams(Map<String, String> params)
    {
        if (this.metadata)
        {
            params.put("metadata", "true");
        }
        if (this.full)
        {
            params.put("full", "true");
        }
        if (this.paths)
        {
            params.put("paths", "true");
        }
    }

    @Override
    public Response get(String uri)
    {
        return get(uri, null);
    }

    @Override
    public Response get(String uri, Map<String, String> params)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, params, true);

            // this method throws an HTTP exception unless we get a 20x or 404
            // a 404 returns null
            HttpResponse httpResponse = null;
            try
            {
                httpResponse = invoker.get(URL);
            }
            catch (Exception ex)
            {
                // make sure to consume the response
                consumeQuietly(httpResponse);

                throw new RuntimeException(ex);
            }

            if (!HttpUtil.isOk(httpResponse))
            {
                if (httpResponse.getStatusLine().getStatusCode() == 404)
                {
                    EntityUtils.consume(httpResponse.getEntity());
                    return null;
                }

                HttpUtil.raiseHttpException(URL, httpResponse, (String)null);
            }

            response = toResponse(httpResponse);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
    public Response delete(String uri)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, true);

            // this method throws an HTTP exception unless we get a 20x
            HttpResponse httpResponse = null;
            try
            {
                httpResponse = invoker.delete(URL);
            }
            catch (Exception ex)
            {
                // make sure to consume the response
                consumeQuietly(httpResponse);

                throw new RuntimeException(ex);
            }

            if (!HttpUtil.isOk(httpResponse))
            {
                HttpUtil.raiseHttpException(URL, httpResponse, (String)null);
            }

            response = toResponse(httpResponse);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
    public Response post(String uri)
    {
        return post(uri, (Map) null);
    }

    @Override
    public Response post(String uri, Map<String, String> params)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, params, true);

            // this method throws an HTTP exception unless we get a 20x
            HttpResponse httpResponse = null;
            try
            {
                httpResponse = invoker.post(URL);
            }
            catch (Exception ex)
            {
                // make sure to consume the response
                consumeQuietly(httpResponse);

                throw new RuntimeException(ex);
            }

            if (!HttpUtil.isOk(httpResponse))
            {
                HttpUtil.raiseHttpException(URL, httpResponse, (String)null);
            }

            response = toResponse(httpResponse);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
    public Response post(String uri, ObjectNode object)
    {
        return post(uri, null, object);
    }

    @Override
    public Response post(String uri, Map<String, String> params, ObjectNode object)
    {
        byte[] x = JsonUtil.stringify(object, false).getBytes();
        String mimetype = "application/json";

        return post(uri, params, x, mimetype);
    }

    @Override
    public Response post(String uri, byte[] bytes, String mimetype)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return post(uri, in, bytes.length, mimetype);
    }

    @Override
    public Response post(String uri, Map<String, String> params, byte[] bytes, String mimetype)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return post(uri, params, in, bytes.length, mimetype);
    }

    @Override
    public Response post(String uri, InputStream in, long length, String mimetype)
    {
        return post(uri, null, in, length, mimetype);
    }

    @Override
    public Response post(String uri, Map<String, String> params, InputStream in, long length, String mimetype)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, params, true);

            // this method throws an HTTP exception unless we get a 20x
            HttpResponse httpResponse = null;
            try
            {
                httpResponse = invoker.post(URL, in, length, mimetype);
            }
            catch (Exception ex)
            {
                // make sure to consume the response
                consumeQuietly(httpResponse);

                throw new RuntimeException(ex);
            }

            if (!HttpUtil.isOk(httpResponse))
            {
                HttpUtil.raiseHttpException(URL, httpResponse, (String)null);
            }

            response = toResponse(httpResponse);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
    public HttpResponse postData(String uri, InputStream in, long length, String mimetype) 
    {
        String URL = buildURL(uri, true);

        HttpResponse httpResponse = null;
        try
        {
            httpResponse = invoker.post(URL, in, length, mimetype);
        }
        catch (Exception ex)
        {
            // make sure to consume the response
            consumeQuietly(httpResponse);

            throw new RuntimeException(ex);
        }

        if (!HttpUtil.isOk(httpResponse))
        {
            HttpUtil.raiseHttpException(URL, httpResponse, null);
        }

        return httpResponse;
    }

    @Override
    public HttpResponse getEx(String uri)
    {
        String URL = buildURL(uri, true);

        HttpResponse httpResponse = null;
        try
        {
            httpResponse = invoker.get(URL);
        }
        catch (Exception ex)
        {
            // make sure to consume the response
            consumeQuietly(httpResponse);

            throw new RuntimeException(ex);
        }

        if (!HttpUtil.isOk(httpResponse))
        {
            HttpUtil.raiseHttpException(URL, httpResponse, null);
        }

        return httpResponse;
    }

    @Override
    public HttpResponse postEx(String uri)
    {
        String URL = buildURL(uri, true);

        HttpResponse httpResponse = null;
        try
        {
            httpResponse = invoker.post(URL);
        }
        catch (Exception ex)
        {
            // make sure to consume the response
            consumeQuietly(httpResponse);

            throw new RuntimeException(ex);
        }

        if (!HttpUtil.isOk(httpResponse))
        {
            HttpUtil.raiseHttpException(URL, httpResponse, null);
        }

        return httpResponse;
    }

    @Override
    public Response post(String uri, Map<String, String> params, ObjectNode object, HttpPayload payload)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, false);

            if (params == null)
            {
                params = new LinkedHashMap<String, String>();
            }
            buildParams(params);

            // this method throws an HTTP exception unless we get a 20x
            HttpResponse httpResponse = null;
            try
            {
                httpResponse = invoker.multipartPost(URL, params, object, payload);
            }
            catch (Exception ex)
            {
                // make sure to consume the response
                consumeQuietly(httpResponse);

                throw new RuntimeException(ex);
            }

            if (!HttpUtil.isOk(httpResponse))
            {
                HttpUtil.raiseHttpException(URL, httpResponse, (String)null);
            }

            response = toResponse(httpResponse);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
    public Response put(String uri)
    {
        return put(uri, (Map) null);
    }

    @Override
    public Response put(String uri, Map<String, String> params)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, params, true);

            // this method throws an HTTP exception unless we get a 20x
            byte[] x = invoker.PUT(URL);
            ObjectNode object = toObjectNode(x);

            response = toResponse(object, null);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
    public Response put(String uri, ObjectNode object)
    {
        return put(uri, null, object);
    }

    @Override
    public Response put(String uri, Map<String, String> params, ObjectNode object)
    {
        byte[] x = JsonUtil.stringify(object, false).getBytes();
        String mimetype = "application/json";

        return put(uri, params, x, mimetype);
    }

    @Override
    public Response put(String uri, byte[] bytes, String mimetype)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return put(uri, in, bytes.length, mimetype);
    }

    @Override
    public Response put(String uri, Map<String, String> params, byte[] bytes, String mimetype)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        return put(uri, params, in, bytes.length, mimetype);
    }

    @Override
    public Response put(String uri, InputStream in, long length, String mimetype)
    {
        return put(uri, null, in, length, mimetype);
    }

    @Override
    public Response put(String uri, Map<String, String> params, InputStream in, long length, String mimetype)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, params, true);

            // this method throws an HTTP exception unless we get a 20x
            HttpResponse httpResponse = null;
            try
            {
                httpResponse = invoker.put(URL, in, length, mimetype);
            }
            catch (Exception ex)
            {
                // make sure to consume the response
                consumeQuietly(httpResponse);

                throw new RuntimeException(ex);
            }

            if (!HttpUtil.isOk(httpResponse))
            {
                HttpUtil.raiseHttpException(URL, httpResponse, (String)null);
            }

            response = toResponse(httpResponse);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
    public Response put(String uri, Map<String, String> params, ObjectNode object, HttpPayload payload)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, false);

            if (params == null)
            {
                params = new LinkedHashMap<String, String>();
            }
            buildParams(params);

            // this method throws an HTTP exception unless we get a 20x
            HttpResponse httpResponse = null;
            try
            {
                httpResponse = invoker.multipartPut(URL, params, object, payload);
            }
            catch (Exception ex)
            {
                // make sure to consume the response
                consumeQuietly(httpResponse);

                throw new RuntimeException(ex);
            }

            if (!HttpUtil.isOk(httpResponse))
            {
                HttpUtil.raiseHttpException(URL, httpResponse, (String)null);
            }

            response = toResponse(httpResponse);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
	public void upload(String uri, byte[] bytes, String mimetype)
        throws Exception
	{
		InputStreamEntity entity = new InputStreamEntity(new ByteArrayInputStream(bytes), bytes.length);
        entity.setContentType(mimetype);

        String URL = buildURL(uri, false);

        HttpPost httpPost = new HttpPost(URL);
        invoker.configure(httpPost);

        httpPost.setEntity(entity);

        HttpResponse httpResponse = invoker.execute(httpPost);
        if (!HttpUtil.isOk(httpResponse))
        {
            raiseHttpException(httpPost, httpResponse, "Upload failed");
        }

        // consume the response fully so that the client connection can be reused
        EntityUtils.consume(httpResponse.getEntity());
	}

    @Override
	public void upload(String uri, byte[] bytes, String mimetype, String filename)
        throws Exception
	{
        String URL = buildURL(uri, false);

        HttpPost httpPost = new HttpPost(URL);
        invoker.configure(httpPost);

        HttpPayload payload = new HttpPayload();
        payload.setBytes(bytes);
        payload.setContentType(mimetype);
        payload.setFilename(filename);
        payload.setLength(bytes.length);

        MultipartEntity entity = new MultipartEntity();
        entity.addPart(filename, new HttpPayloadContentBody(payload));
        httpPost.setEntity(entity);

        HttpResponse httpResponse = invoker.execute(httpPost);
        if (!HttpUtil.isOk(httpResponse))
        {
            raiseHttpException(httpPost, httpResponse, "Upload failed");
        }

        // consume the response fully so that the client connection can be reused
        EntityUtils.consume(httpResponse.getEntity());
	}

    @Override
    public void upload(String uri, InputStream in, long length, String mimetype) throws Exception
    {
        InputStreamEntity entity = new InputStreamEntity(in, length);
        entity.setContentType(mimetype);

        String URL = buildURL(uri, false);
        HttpPost httpPost = new HttpPost(URL);
        invoker.configure(httpPost);
        httpPost.setEntity(entity);

        HttpResponse httpResponse = invoker.execute(httpPost);
        if (!HttpUtil.isOk(httpResponse))
        {
            raiseHttpException(httpPost, httpResponse, "Upload failed");
        }

        // consume the response fully so that the client connection can be reused
        EntityUtils.consume(httpResponse.getEntity());
    }

    @Override
    public void upload(String uri, InputStream in, long length, String mimetype, String filename) throws Exception
    {
        String URL = buildURL(uri, false);

        HttpPost httpPost = new HttpPost(URL);
        invoker.configure(httpPost);

        InputStreamBody inputStreamBody = new InputStreamBody(in, mimetype, filename);

        MultipartEntity entity = new MultipartEntity();
        entity.addPart(filename, inputStreamBody);
        httpPost.setEntity(entity);

        HttpResponse httpResponse = invoker.execute(httpPost);
        if (!HttpUtil.isOk(httpResponse))
        {
            raiseHttpException(httpPost, httpResponse, "Upload failed");
        }

        // consume the response fully so that the client connection can be reused
        EntityUtils.consume(httpResponse.getEntity());
    }

    @Override
    public Response upload(String uri, HttpPayload... payloads)
        throws Exception
    {
        Map<String, String> params = new LinkedHashMap<String, String>();

        return upload(uri, params, payloads);
    }

    @Override
    public Response upload(String uri, Map<String, String> params, HttpPayload... payloads)
        throws Exception
    {
        String URL = buildURL(uri, false);

        if (params == null)
        {
            params = new LinkedHashMap<String, String>();
        }
        buildParams(params);

        Map<String, HttpPayload> payloadMap = new LinkedHashMap<String, HttpPayload>();
        for (HttpPayload payload: payloads)
        {
            payloadMap.put(payload.getFilename(), payload);
        }

        HttpResponse httpResponse = invoker.multipartPost(URL, params, payloadMap);
        if (!HttpUtil.isOk(httpResponse))
        {
            HttpUtil.raiseHttpException(URL, httpResponse, null);
        }

        // store onto thread local so that we have the ability to retrieve headers from thread local
        bindRemoteContext(httpResponse);

        return toResponse(httpResponse);
    }

    @Override
    public byte[] downloadBytes(String uri)
        throws Exception
    {
        byte[] bytes = null;

        HttpResponse httpResponse = null;
        try
        {
            httpResponse = download(uri);
            bytes = EntityUtils.toByteArray(httpResponse.getEntity());
        }
        finally
        {
            try { consumeQuietly(httpResponse); } catch (Exception ex) { }
        }

        return bytes;
    }

    @Override
	public HttpResponse download(String uri)
        throws Exception
	{
        String URL = buildURL(uri, false);

        HttpGet httpGet = new HttpGet(URL);
        invoker.configure(httpGet);

        HttpResponse httpResponse = invoker.execute(httpGet);
        if (!HttpUtil.isOk(httpResponse))
        {
            raiseHttpException(httpGet, httpResponse, "Download failed");
        }

        // store onto thread local so that we have the ability to retrieve headers from thread local
        bindRemoteContext(httpResponse);

        return httpResponse;
	}

    private ObjectNode toObjectNode(byte[] response)
        throws Exception
    {
        ObjectNode object = null;

        if (response.length > 0)
        {
            object = JsonUtil.createObject(new String(response, UTF_8));
        }

        return object;
    }

    private RemoteContext bindRemoteContext(HttpResponse httpResponse)
    {
        // store onto thread local so that we have the ability to retrieve headers from thread local
        Map<String, String> headerMap = buildHeaderMap(httpResponse);
        RemoteContext remoteContext = new RemoteContext(headerMap);
        RemoteContextThreadLocal.setRemoteContext(remoteContext);

        return remoteContext;
    }

    private Map<String, String> buildHeaderMap(HttpResponse httpResponse)
    {
        Map<String, String> headerMap = new HashMap<String, String>();

        // if we have headers, convert them to a map
        Header[] headers = httpResponse.getAllHeaders();
        if (headers != null && headers.length > 0)
        {
            for (int i = 0; i < headers.length; i++)
            {
                Header header = headers[i];

                String name = header.getName();
                String value = header.getValue();

                if (value != null)
                {
                    headerMap.put(name, value);
                }
            }
        }

        return headerMap;
    }

    private Response toResponse(HttpResponse httpResponse)
        throws Exception
    {
        Map<String, String> headerMap = buildHeaderMap(httpResponse);

        ObjectNode object = null;
        InputStream in = null;
        try
        {
            in = httpResponse.getEntity().getContent();
            object = JsonUtil.createObject(in, ObjectNode.class);
        }
        finally
        {
            StreamUtil.safeClose(in);
        }

        return toResponse(object, headerMap);
    }

    private Response toResponse(ObjectNode object, Map<String, String> headerMap)
    {
        Response response = new Response(object, headerMap);

        // store onto thread local so that we have the ability to retrieve headers from thread local
        RemoteContext remoteContext = new RemoteContext(headerMap);
        RemoteContextThreadLocal.setRemoteContext(remoteContext);

        return response;
    }

    private void raiseHttpException(HttpRequestBase httpRequest, HttpResponse httpResponse, String message)
    {
        HttpUtil.raiseHttpException(httpRequest, httpResponse, message);
    }

    private void consumeQuietly(HttpResponse httpResponse)
    {
        try
        {
            if (httpResponse != null)
            {
                EntityUtils.consumeQuietly(httpResponse.getEntity());
            }
        }
        catch (Exception ex)
        {
            // swallow
        }
    }
}
