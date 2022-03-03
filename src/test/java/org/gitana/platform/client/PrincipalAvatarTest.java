/**
 * Copyright 2022 Gitana Software, Inc.
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
package org.gitana.platform.client;

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.platform.client.domain.Domain;
import org.gitana.platform.client.platform.Platform;
import org.gitana.platform.client.principal.DomainUser;
import org.gitana.platform.util.TestConstants;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

import java.io.IOException;

/**
 * @author uzi
 */
public class PrincipalAvatarTest extends AbstractTestCase
{
    @Test
    public void testAvatar()
        throws IOException
    {
        Gitana gitana = new Gitana();

        // authenticate
        Platform platform = gitana.authenticate("admin", "admin");

        Domain domain = platform.readDomain("default");

        // create user1
        String user1name = "testuser1_" + System.currentTimeMillis();
        DomainUser user1 = domain.createUser(user1name, TestConstants.TEST_PASSWORD);

        // upload an "avatar" attachment for user1
        byte[] avatarBytes = ClasspathUtil.bytesFromClasspath("org/gitana/platform/client/avatar.png");
        user1.uploadAttachment("avatar", avatarBytes, MimeTypeMap.IMAGE_PNG);

        // create user2
        String user2name = "testuser2_" + System.currentTimeMillis();
        DomainUser user2 = domain.createUser(user2name, TestConstants.TEST_PASSWORD);



        //
        // logged in as admin user
        // ensure that can retrieve "avatar"
        //
        byte[] avatarBytes1 = user1.downloadAttachment("avatar");
        assertEquals(avatarBytes.length, avatarBytes1.length);



        //
        // log in as user1
        // ensure can retrieve "avatar"
        //
        Platform platform2 = gitana.authenticate(user1.getName(), TestConstants.TEST_PASSWORD);
        DomainUser user1x = (DomainUser) domain.readPrincipal(user1name);
        byte[] avatarBytes1x = user1x.downloadAttachment("avatar");
        assertEquals(avatarBytes.length, avatarBytes1x.length);



        //
        // log in as user2
        //
        Platform platform3 = gitana.authenticate(user1.getName(), TestConstants.TEST_PASSWORD);
        DomainUser user1xx = (DomainUser) domain.readPrincipal(user1name);

        // download the "avatar" attachment
        byte[] avatarBytes1xx = user1xx.downloadAttachment("avatar");
        assertEquals(avatarBytes.length, avatarBytes1xx.length);


    }

}
