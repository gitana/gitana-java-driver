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

package org.gitana.repo.client;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.gitana.repo.client.support.ObjectFactoryImpl;
import org.gitana.repo.client.support.Remote;
import org.gitana.repo.client.support.RemoteImpl;
import org.gitana.repo.client.types.*;

/**
 * @author uzi
 */
public class Driver
{
    public final static String TICKET_COOKIE_NAME = "GITANA_TICKET";

    private Remote remote;

    private String host;
    private int port;

    private String ticket;
    private String username;

    private ObjectFactory factory;

    public Driver(String host, int port, String username, String ticket)
    {
        this.host = host;
        this.port = port;

        this.ticket = ticket;
        this.username = username;

        // build a new http client
        HttpClient client = new HttpClient();
        client.getParams().setSoTimeout(0);

        // disable retry on http calls
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        // set a cookie into the http state
        Cookie cookie = buildCookie(ticket);
        client.getState().addCookie(cookie);

        // build remote
        this.remote = new RemoteImpl(client, "http://" + host + ":" + port);

        this.init();
    }

    private void init()
    {
        factory = new ObjectFactoryImpl(this);

        // register default types
        factory.register(AssociationDefinition.QNAME, AssociationDefinitionImpl.class);
        factory.register(ChildAssociation.QNAME, ChildAssociationImpl.class);
        factory.register(CopiedFromAssociation.QNAME, CopiedFromAssociationImpl.class);
        factory.register(CreatedAssociation.QNAME, CreatedAssociationImpl.class);
        factory.register(DeletedAssociation.QNAME, DeletedAssociationImpl.class);
        factory.register(FeatureDefinition.QNAME, FeatureDefinitionImpl.class);
        factory.register(Form.QNAME, FormImpl.class);
        factory.register(Group.QNAME, GroupImpl.class);
        factory.register(HasBehaviorAssociation.QNAME, HasBehaviorAssociationImpl.class);
        factory.register(HasFormAssociation.QNAME, HasFormAssociationImpl.class);
        factory.register(HasLockAssociation.QNAME, HasLockAssociationImpl.class);
        factory.register(HasMountAssociation.QNAME, HasMountAssociationImpl.class);
        factory.register(HasTranslationAssociation.QNAME, HasTranslationAssociationImpl.class);
        factory.register(LinkedAssociation.QNAME, LinkedAssociationImpl.class);
        factory.register(OwnedAssociation.QNAME, OwnedAssociationImpl.class);
        factory.register(Person.QNAME, PersonImpl.class);
        factory.register(Rule.QNAME, RuleImpl.class);
        factory.register(TypeDefinition.QNAME, TypeDefinitionImpl.class);
        factory.register(UpdatedAssociation.QNAME, UpdatedAssociationImpl.class);
    }

    private String getDomain()
    {
        return this.host;
    }

    private Cookie buildCookie(String ticket)
    {
        Cookie cookie = new Cookie();
        cookie.setDomain(getDomain());
        cookie.setPath("/");
        cookie.setName(TICKET_COOKIE_NAME);
        cookie.setValue(ticket);

        return cookie;
    }

    public Remote getRemote()
    {
        return remote;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getTicket()
    {
        return ticket;
    }

    public String getUsername()
    {
        return username;
    }

    public ObjectFactory getFactory()
    {
        return factory;
    }

}
