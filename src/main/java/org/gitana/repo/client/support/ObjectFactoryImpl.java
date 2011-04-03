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
import org.gitana.repo.client.*;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.nodes.NodeImpl;
import org.gitana.repo.namespace.QName;
import org.gitana.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class ObjectFactoryImpl implements ObjectFactory
{
    private Gitana gitana;

    public ObjectFactoryImpl(Gitana gitana)
    {
        this.gitana = gitana;
    }

    @Override
    public Repository repository()
    {
        return repository(JsonUtil.createObject());
    }

    @Override
    public Repository repository(ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new RepositoryImpl(gitana, object, false);
    }

    @Override
    public Repository repository(Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new RepositoryImpl(gitana, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Repository> repositories(Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Repository> list = new HashMap<String, Repository>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Repository repository = new RepositoryImpl(gitana, object, true);
            list.put(repository.getId(), repository);
        }

        return list;
    }

    @Override
    public Branch branch(Repository repository)
    {
        return branch(repository, JsonUtil.createObject());
    }

    @Override
    public Branch branch(Repository repository, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        return new BranchImpl(gitana, repository, object, false);
    }

    @Override
    public Branch branch(Repository repository, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new BranchImpl(gitana, repository, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Branch> branches(Repository repository, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Branch> list = new HashMap<String, Branch>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Branch branch = new BranchImpl(gitana, repository, object, true);
            list.put(branch.getId(), branch);
        }

        return list;
    }

    @Override
    public Changeset changeset(Repository repository, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return new ChangesetImpl(gitana, repository, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Changeset> changesets(Repository repository, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Changeset> list = new HashMap<String, Changeset>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Changeset changeset = new ChangesetImpl(gitana, repository, object, true);
            list.put(changeset.getId(), changeset);
        }

        return list;
    }

    @Override
    public Node node(Branch branch, QName typeQName)
    {
        return node(branch, typeQName, JsonUtil.createObject());
    }

    @Override
    public Node node(Branch branch, QName typeQName, ObjectNode object)
    {
        if (object == null)
        {
            object = JsonUtil.createObject();
        }

        object.put(Node.FIELD_TYPE_QNAME, typeQName.toString());

        return produce(branch, object, false);
    }

    @Override
    public Node node(Branch branch, Response response)
    {
        if (!response.isDataDocument())
        {
            throw new RuntimeException("Response must be a data document");
        }

        return produce(branch, response.getObjectNode(), true);
    }

    @Override
    public Map<String, Node> nodes(Branch branch, Response response)
    {
        if (!response.isListDocument())
        {
            throw new RuntimeException("Response must be a list document");
        }

        Map<String, Node> list = new HashMap<String, Node>();
        for (ObjectNode object : response.getObjectNodes())
        {
            Node node = produce(branch, object, true);
            list.put(node.getId(), node);
        }

        return list;
    }

    /**
     * Factory method that produces typed node objects based on type QName.
     *
     * TODO: implement all of this
     *
     * @param branch
     * @param object
     * @param isSaved
     *
     * @return node
     */
    private Node produce(Branch branch, ObjectNode object, boolean isSaved)
    {
        /*
        String type = JsonUtil.objectGetString(object, Node.FIELD_TYPE_QNAME);
        if (type == null)
        {
            type = "n:node"; // TODO: GitanaModel into objects?
        }

        QName typeQName = QName.create(type);
        */

        return new NodeImpl(gitana, branch, object, isSaved);
    }
}
