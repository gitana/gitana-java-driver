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

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.beans.ACL;
import org.gitana.repo.client.util.DriverUtil;
import org.gitana.repo.support.RepositoryType;

import java.util.List;

/**
 * @author uzi
 */
public class RepositoryImpl extends DocumentImpl implements Repository
{
    private Gitana gitana;

    protected RepositoryImpl(Gitana gitana, ObjectNode obj, boolean isSaved)
    {
        super(obj, isSaved);

        this.gitana = gitana;
    }

    protected Remote getRemote()
    {
        return gitana.getRemote();
    }

    @Override
    public RepositoryType getType()
    {
        return RepositoryType.valueOf(getString(Repository.FIELD_REPOSITORY_TYPE));
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Repository)
        {
            Repository other = (Repository) object;

            equals = (this.getId().equals(other.getId())) && (this.getType().equals(other.getType()));
        }

        return equals;
    }

    @Override
    public ACL getACL()
    {
        Response response = getRemote().get("/repositories/" + getId() + "/acl");

        return DriverUtil.toACL(response);
    }


    @Override
    public List<String> getAuthorities(String principalId)
    {
        Response response = getRemote().get("/repositories/" + getId() + "/acl/" + principalId);

        return DriverUtil.toStringList(response);
    }

    @Override
    public void grant(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getId() + "/acl/" + principalId + "/grant/" + authorityId);
    }

    @Override
    public void revoke(String principalId, String authorityId)
    {
        getRemote().post("/repositories/" + getId() + "/acl/" + principalId + "/revoke/" + authorityId);
    }

    @Override
    public void revokeAll(String principalId)
    {
        revoke(principalId, "all");
    }

    @Override
    public void uploadFile(String filename, byte[] bytes, String contentType)
    {
        try
        {
            getRemote().upload("/repositories/files/" + filename, bytes, contentType);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteFile(String filename)
    {
        getRemote().delete("/repositories/files/" + filename);
    }

    @Override
    public byte[] downloadFile(String filename)
    {
        byte[] bytes = null;

        try
        {
            bytes = getRemote().download("/repositories/files/" + filename);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        return bytes;
    }

}
