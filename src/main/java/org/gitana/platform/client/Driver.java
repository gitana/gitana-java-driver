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

package org.gitana.platform.client;

import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.support.ObjectFactory;
import org.gitana.platform.client.support.ObjectFactoryImpl;
import org.gitana.platform.client.support.Remote;
import org.gitana.platform.client.node.type.*;

/**
 * @author uzi
 */
public class Driver
{
    private Remote remote;

    private ObjectFactory factory;

    private AuthInfo authInfo;
    private Platform platform;

    public Driver(Remote remote)
    {
        this.remote = remote;

        // initialize
        this.init();
    }

    public void setAuthInfo(AuthInfo authInfo)
    {
        this.authInfo = authInfo;
    }

    public void removeAuthInfo()
    {
        this.authInfo = null;
    }

    public AuthInfo getAuthInfo()
    {
        return this.authInfo;
    }

    public Platform getPlatform()
    {
        return this.platform;
    }

    public void setPlatform(Platform platform)
    {
        this.platform = platform;
    }

    private void init()
    {
        factory = new ObjectFactoryImpl();

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
        factory.register(NodeList.QNAME, NodeListImpl.class);
        factory.register(OwnedAssociation.QNAME, OwnedAssociationImpl.class);
        factory.register(Person.QNAME, PersonImpl.class);
        factory.register(Rule.QNAME, RuleImpl.class);
        factory.register(TypeDefinition.QNAME, TypeDefinitionImpl.class);
        factory.register(UpdatedAssociation.QNAME, UpdatedAssociationImpl.class);
    }

    public Remote getRemote()
    {
        return remote;
    }

    public ObjectFactory getFactory()
    {
        return factory;
    }

}
