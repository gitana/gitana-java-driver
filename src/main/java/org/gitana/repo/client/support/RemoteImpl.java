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

package org.gitana.repo.client.support;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.http.HttpPayload;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.exceptions.RemoteServerException;
import org.gitana.util.HttpUtilEx;
import org.gitana.util.JsonUtil;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class RemoteImpl implements Remote
{
    private HttpClient client;
    private String remoteURL;

    private boolean full;
    private boolean metadata;

    public RemoteImpl(HttpClient client, String remoteURL)
    {
        this.client = client;
        this.remoteURL = remoteURL;

        this.full = true;
        this.metadata = true;
    }

    public void setFull(boolean full)
    {
        this.full = full;
    }

    public void setMetadata(boolean metadata)
    {
        this.metadata = metadata;
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

            byte[] x = HttpUtilEx.GET(client, URL);
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

            byte[] x = HttpUtilEx.DELETE(client, URL);
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

            byte[] x = HttpUtilEx.POST(client, URL);
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

            byte[] x = HttpUtilEx.POST(client, URL, in, length, mimetype);
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

            byte[] x = HttpUtilEx.MULTIPART_POST(client, URL, params, object, payload);
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

            byte[] x = HttpUtilEx.PUT(client, URL);
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

            byte[] x = HttpUtilEx.PUT(client, URL, in, length, mimetype);
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

            byte[] x = HttpUtilEx.MULTIPART_PUT(client, URL, params, object, payload);
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
		InputStreamRequestEntity entity = new InputStreamRequestEntity(new ByteArrayInputStream(bytes));

        String URL = buildURL(uri, false);
		PostMethod method = new PostMethod(URL);
		method.setRequestEntity(entity);

		Header contentType = new Header("Content-Type", mimetype);
		Header contentLength = new Header("Content-Length", String.valueOf(bytes.length));
		method.addRequestHeader(contentType);
		method.addRequestHeader(contentLength);

		int sc = client.executeMethod(method);
        if (sc != 200)
        {
            throw new RuntimeException("Upload failed");
        }
	}

    @Override
	public void upload(String uri, byte[] bytes, String mimetype, String fileName)
        throws Exception
	{
        String URL = buildURL(uri, false);

        PostMethod method = new PostMethod(URL);
        Part[] parts = {
                new FilePart(fileName, new ByteArrayPartSource(fileName, bytes) , mimetype,null)
        };
        method.setRequestEntity(
                new MultipartRequestEntity(parts, method.getParams())
        );

        int sc = client.executeMethod(method);
        if (sc != 200)
        {
            throw new RuntimeException("Upload failed");
        }
	}

    @Override
    public void upload(String uri, HttpPayload... payloads)
        throws Exception
    {
        Map<String, String> params = new LinkedHashMap<String, String>();

        upload(uri, params, payloads);
    }

    @Override
    public void upload(String uri, Map<String, String> params, HttpPayload... payloads)
        throws Exception
    {
        String URL = buildURL(uri, false);

        Map<String, HttpPayload> payloadMap = new LinkedHashMap<String, HttpPayload>();
        for (HttpPayload payload: payloads)
        {
            payloadMap.put(payload.getFilename(), payload);
        }

        HttpUtilEx.multipartPost(client, URL, params, payloadMap);
    }

    @Override
    public byte[] downloadBytes(String uri)
        throws Exception
    {
        GetMethod method = download(uri);

        InputStream in = method.getResponseBodyAsStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileCopyUtils.copy(in, out);

        return out.toByteArray();
    }

    @Override
	public GetMethod download(String uri)
        throws Exception
	{
        String URL = buildURL(uri, false);
		GetMethod method = new GetMethod(URL);

		int sc = client.executeMethod(method);
        if (sc != 200)
        {
            throw new RuntimeException("Download failed");
        }

        return method;
	}

    private ObjectNode toObjectNode(byte[] response)
        throws Exception
    {
        return JsonUtil.createObject(new String(response));
    }

    private Response toResult(byte[] response)
        throws Exception
    {
        ObjectNode object = toObjectNode(response);

        return new Response(object);
    }
}
