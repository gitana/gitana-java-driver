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
import org.apache.http.cookie.Cookie;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.http.HttpPayload;

import java.io.InputStream;
import java.util.Map;

/**
 * @author uzi
 */
public interface Remote
{
    public Response get(String uri);

    public Response get(String uri, Map<String, String> params);

    public Response delete(String uri);


    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // POST
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public Response post(String uri);

    public Response post(String uri, Map<String, String> params);

    public Response post(String uri, ObjectNode object);

    public Response post(String uri, Map<String, String> params, ObjectNode object);

    public Response post(String uri, byte[] bytes, String mimetype);

    public Response post(String uri, Map<String, String> params, byte[] bytes, String mimetype);

    public Response post(String uri, InputStream in, long length, String mimetype);

    public Response post(String uri, Map<String, String> params, InputStream in, long length, String mimetype);

    public HttpResponse postEx(String uri);

    public HttpResponse postData(String uri, InputStream in, long length, String mimetype);


    /**
     * Performs a multipart post.
     *
     * @param uri
     * @param params
     * @param object
     * @param payload
     * @return
     */
    public Response post(String uri, Map<String, String> params, ObjectNode object, HttpPayload payload);


    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // PUT
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

    public Response put(String uri);

    public Response put(String uri, Map<String, String> params);

    public Response put(String uri, ObjectNode object);

    public Response put(String uri, Map<String, String> params, ObjectNode object);

    public Response put(String uri, byte[] bytes, String mimetype);

    public Response put(String uri, Map<String, String> params, byte[] bytes, String mimetype);

    public Response put(String uri, InputStream in, long length, String mimetype);

    public Response put(String uri, Map<String, String> params, InputStream in, long length, String mimetype);

    /**
     * Performs a multipart put.
     *
     * @param uri
     * @param params
     * @param object
     * @param payload
     * @return
     */
    public Response put(String uri, Map<String, String> params, ObjectNode object, HttpPayload payload);


    //////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // UPLOAD AND DOWNLOAD
    //
    //////////////////////////////////////////////////////////////////////////////////////////////////

	public void upload(String uri, byte[] bytes, String mimetype) throws Exception;

	public void upload(String uri, byte[] bytes, String mimetype, String filename) throws Exception;

    public void upload(String uri, InputStream in, long length, String mimetype) throws Exception;

    public void upload(String uri, InputStream in, long length, String mimetype, String filename) throws Exception;

    public Response upload(String uri, HttpPayload... payloads) throws Exception;

    public Response upload(String uri, Map<String, String> params, HttpPayload... payloads) throws Exception;

	public byte[] downloadBytes(String uri) throws Exception;

    public HttpResponse download(String uri) throws Exception;

    public void addCookie(Cookie cookie);
}