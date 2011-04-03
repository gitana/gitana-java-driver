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
import java.util.HashMap;
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
        StringBuilder sb = new StringBuilder();
        sb.append(this.remoteURL);
        sb.append(uri);

        if (expand)
        {
            boolean added = false;
            if (uri.indexOf("?") > -1)
            {
                added = true;
            }

            if (this.metadata)
            {
                sb.append((added ? "&" : "?"));
                sb.append("metadata=true");
                added = true;
            }
            if (this.full)
            {
                sb.append((added ? "&" : "?"));
                sb.append("full=true");
                added = true;
            }
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
    public Response post(String uri)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, true);

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
        byte[] x = JsonUtil.stringify(object, false).getBytes();
        String mimetype = "application/json";

        return post(uri, x, mimetype);
    }

    @Override
    public Response post(String uri, byte[] bytes, String mimetype)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, true);

            byte[] x = HttpUtilEx.POST(client, URL, bytes, mimetype);
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
    public Response get(String uri)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, true);

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
    public Response put(String uri, ObjectNode object)
    {
        Response response = null;
        try
        {
            byte[] x = JsonUtil.stringify(object, false).getBytes();
            String mimetype = "application/json";

            response = put(uri, x, mimetype);

        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return response;
    }

    @Override
    public Response put(String uri, byte[] bytes, String mimetype)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, true);

            byte[] x = HttpUtilEx.PUT(client, URL, bytes, mimetype);
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
                params = new HashMap<String, String>();
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
    public Response put(String uri, Map<String, String> params, ObjectNode object, HttpPayload payload)
    {
        Response response = null;
        try
        {
            String URL = buildURL(uri, false);

            if (params == null)
            {
                params = new HashMap<String, String>();
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
	public byte[] download(String uri)
        throws Exception
	{
        String URL = buildURL(uri, false);
		GetMethod method = new GetMethod(URL);

		int sc = client.executeMethod(method);
        if (sc != 200)
        {
            throw new RuntimeException("Download failed");
        }

		InputStream in = method.getResponseBodyAsStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileCopyUtils.copy(in, baos);

		return baos.toByteArray();
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
