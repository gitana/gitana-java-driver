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

import org.gitana.mimetype.MimeTypeMap;
import org.gitana.util.ClasspathUtil;
import org.junit.Test;

import java.io.IOException;

/**
 * @author uzi
 */
public class SecurityAvatarTest extends AbstractTestCase
{
    @Test
    public void testAvatar()
        throws IOException
    {
        Gitana gitana = new Gitana();

        // authenticate
        Server server = gitana.authenticate("admin", "admin");

        // create user1
        String user1name = "testuser1_" + System.currentTimeMillis();
        SecurityUser user1 = server.createUser(user1name, "password");

        // upload an "avatar" attachment for user1
        byte[] avatarBytes = ClasspathUtil.bytesFromClasspath("org/gitana/repo/client/avatar.png");
        user1.uploadAttachment("avatar", avatarBytes, MimeTypeMap.IMAGE_PNG);

        // create user2
        String user2name = "testuser2_" + System.currentTimeMillis();
        SecurityUser user2 = server.createUser(user2name, "password");



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
        Server server2 = gitana.authenticate(user1.getName(), "password");
        SecurityUser user1x = server2.readUser(user1name);
        byte[] avatarBytes1x = user1x.downloadAttachment("avatar");
        assertEquals(avatarBytes.length, avatarBytes1x.length);



        //
        // log in as user2
        //
        Server server3 = gitana.authenticate(user1.getName(), "password");
        SecurityUser user1xx = server3.readUser(user1name);

        // download the "avatar" attachment
        byte[] avatarBytes1xx = user1xx.downloadAttachment("avatar");
        assertEquals(avatarBytes.length, avatarBytes1xx.length);


    }

}
