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

package org.gitana.platform.client.support;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.http.HttpInvoker;
import org.gitana.http.HttpMethodExecutor;
import org.gitana.http.HttpPayload;
import org.gitana.http.HttpPayloadContentBody;
import org.gitana.platform.client.exceptions.RemoteServerException;
import org.gitana.util.HttpUtil;
import org.gitana.util.JsonUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author uzi
 */
public class RemoteImpl implements Remote
{
    private HttpInvoker invoker;
    private String remoteURL;

    private boolean full;
    private boolean metadata;

    public RemoteImpl(HttpClient client, String remoteURL)
    {
        this.remoteURL = remoteURL;

        this.full = true;
        this.metadata = true;

        this.invoker = new HttpInvoker(client);
    }

    public void setHttpMethodExecutor(HttpMethodExecutor httpMethodExecutor)
    {
        this.invoker.setHttpMethodExecutor(httpMethodExecutor);
    }

    public void addCookie(Cookie cookie)
    {
        ((DefaultHttpClient)this.invoker.getClient()).getCookieStore().addCookie(cookie);
    }

    public void setFull(boolean full)
    {
        this.full = full;
    }

    public void setMetadata(boolean metadata)
    {
        this.metadata = metadata;
    }

    public void setLocale(Locale locale)
    {
        this.invoker.setLocale(locale);
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
        sb.append(this.remoteURL);
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

            byte[] x = invoker.GET(URL);
            response = toResult(x);

            if (!response.isOk())
            {
                throw new RemoteServerException(response);
            }
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

            byte[] x = invoker.DELETE(URL);
            response = toResult(x);

            if (!response.isOk())
            {
                throw new RemoteServerException(response);
            }
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

            byte[] x = invoker.POST(URL);
            response = toResult(x);

            if (!response.isOk())
            {
                throw new RemoteServerException(response);
            }
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

            byte[] x = invoker.POST(URL, in, length, mimetype);
            response = toResult(x);

            if (!response.isOk())
            {
                throw new RemoteServerException(response);
            }
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
        HttpResponse httpResponse = null;
        try
        {
            String URL = buildURL(uri, true);

            httpResponse = invoker.post(URL, in, length, mimetype);
        }
        catch (Exception ex)
        {
            // make sure to consume the response
            try { EntityUtils.consume(httpResponse.getEntity()); } catch (Exception ex1) { }

            throw new RuntimeException(ex);
        }

        return httpResponse;
    }

    @Override
    public HttpResponse postEx(String uri)
    {
        HttpResponse httpResponse = null;
        try
        {
            String URL = buildURL(uri, true);

            httpResponse = invoker.post(URL);
        }
        catch (Exception ex)
        {
            // make sure to consume the response
            try { EntityUtils.consume(httpResponse.getEntity()); } catch (Exception ex1) { }

            throw new RuntimeException(ex);
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

            byte[] x = invoker.MULTIPART_POST(URL, params, object, payload);
            response = toResult(x);

            if (!response.isOk())
            {
                throw new RemoteServerException(response);
            }
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

            byte[] x = invoker.PUT(URL);
            response = toResult(x);

            if (!response.isOk())
            {
                throw new RemoteServerException(response);
            }
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

            byte[] x = invoker.PUT(URL, in, length, mimetype);
            response = toResult(x);

            if (!response.isOk())
            {
                throw new RemoteServerException(response);
            }
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

            byte[] x = invoker.MULTIPART_PUT(URL, params, object, payload);
            response = toResult(x);

            if (!response.isOk())
            {
                throw new RemoteServerException(response);
            }
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
        httpPost.setEntity(entity);

        HttpResponse httpResponse = invoker.execute(httpPost);
        if (!HttpUtil.isOk(httpResponse))
        {
            throw new RuntimeException("Upload failed: " + EntityUtils.toString(httpResponse.getEntity()));
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
            throw new RuntimeException("Upload failed: " + EntityUtils.toString(httpResponse.getEntity()));
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
        httpPost.setEntity(entity);

        HttpResponse httpResponse = invoker.execute(httpPost);
        if (!HttpUtil.isOk(httpResponse))
        {
            throw new RuntimeException("Upload failed: " + EntityUtils.toString(httpResponse.getEntity()));
        }

        // consume the response fully so that the client connection can be reused
        EntityUtils.consume(httpResponse.getEntity());
    }

    @Override
    public void upload(String uri, InputStream in, long length, String mimetype, String filename) throws Exception
    {
        String URL = buildURL(uri, false);

        HttpPost httpPost = new HttpPost(URL);

        InputStreamBody inputStreamBody = new InputStreamBody(in, mimetype, filename);

        MultipartEntity entity = new MultipartEntity();
        entity.addPart(filename, inputStreamBody);
        httpPost.setEntity(entity);

        HttpResponse httpResponse = invoker.execute(httpPost);
        if (!HttpUtil.isOk(httpResponse))
        {
            throw new RuntimeException("Upload failed: " + EntityUtils.toString(httpResponse.getEntity()));
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

        HttpResponse response = invoker.multipartPost(URL, params, payloadMap);
        return toResult(EntityUtils.toByteArray(response.getEntity()));
    }

    @Override
    public byte[] downloadBytes(String uri)
        throws Exception
    {
        HttpResponse httpResponse = download(uri);

        return EntityUtils.toByteArray(httpResponse.getEntity());
    }

    @Override
	public HttpResponse download(String uri)
        throws Exception
	{
        String URL = buildURL(uri, false);

        HttpGet httpGet = new HttpGet(URL);

        HttpResponse httpResponse = invoker.execute(httpGet);
        if (!HttpUtil.isOk(httpResponse))
        {
            throw new RuntimeException("Download failed: " + EntityUtils.toString(httpResponse.getEntity()));
        }

        return httpResponse;
	}

    private ObjectNode toObjectNode(byte[] response)
        throws Exception
    {
        ObjectNode object = null;

        if (response.length > 0)
        {
            object = JsonUtil.createObject(new String(response));
        }

        return object;
    }

    private Response toResult(byte[] response)
        throws Exception
    {
        ObjectNode object = toObjectNode(response);

        return new Response(object);
    }
}
