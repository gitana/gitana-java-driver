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

import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.http.HttpPayload;
import org.gitana.repo.client.Response;

import java.io.InputStream;
import java.util.Map;

/**
 * @author uzi
 */
public interface Remote
{
    public Response post(String uri);

    public Response post(String uri, ObjectNode object);

    public Response post(String uri, byte[] bytes, String mimetype);

    public Response post(String uri, InputStream in, long length, String mimetype);

    public Response get(String uri);

    public Response delete(String uri);

    public Response put(String uri, ObjectNode object);

    public Response put(String uri, byte[] bytes, String mimetype);

    public Response post(String uri, Map<String, String> params, ObjectNode object, HttpPayload payload);

    public Response put(String uri, Map<String, String> params, ObjectNode object, HttpPayload payload);

	public void upload(String uri, byte[] bytes, String mimetype) throws Exception;

	public byte[] downloadBytes(String uri) throws Exception;

    public GetMethod download(String uri) throws Exception;
}