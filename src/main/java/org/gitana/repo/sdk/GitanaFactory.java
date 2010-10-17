/**
 * Copyright 2010 Gitana Software, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 *
 * You may obtain a copy of the License at 
 * 	  http://www.apache.org/licenses/LICENSE-2.0 
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

package org.gitana.repo.sdk;

/**
 * Manufactures Gitana objects.
 * 
 * @author uzi
 */
public class GitanaFactory 
{
	private GitanaFactory()
	{
	}
	
	/**
	 * Build with default cloudcms settings
	 * 
	 * @return
	 */
	public static Gitana build()
	{
		return new Gitana();
	}
	
	/**
	 * Build with alternate settings
	 * 
	 * @param protocol
	 * @param host
	 * @param port
	 * @param baseUri
	 * @return
	 */
	public static Gitana build(String protocol, String host, int port)
	{
		Gitana client = new Gitana();
		client.setProtocol(protocol);
		client.setHost(host);
		client.setPort(port);
		client.setBaseUri("");
		
		return client;
	}

}
