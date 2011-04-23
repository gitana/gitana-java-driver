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

package org.gitana.repo.client.services;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.nodes.Association;
import org.gitana.repo.client.nodes.Node;
import org.gitana.repo.client.types.Form;
import org.gitana.repo.client.types.HasFormAssociation;
import org.gitana.repo.client.types.TypeDefinition;
import org.gitana.util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class Forms extends AbstractService
{
    private TypeDefinition node;

    public Forms(Gitana gitana, TypeDefinition node)
    {
        super(gitana);

        this.node = node;
    }

    public Repository getRepository()
    {
        return getBranch().getRepository();
    }

    public String getRepositoryId()
    {
        return getRepository().getId();
    }

    public Branch getBranch()
    {
        return getNode().getBranch();
    }

    public String getBranchId()
    {
        return getBranch().getId();
    }

    public TypeDefinition getNode()
    {
        return node;
    }

    private Map<String, HasFormAssociation> _convert(Map<String, Association> map)
    {
        Map<String, HasFormAssociation> converted = new HashMap<String, HasFormAssociation>();
        for (Association association : map.values())
        {
            converted.put(association.getId(), (HasFormAssociation) association);
        }

        return converted;
    }

    /**
     * Retrieves associations pointing to any forms for this type definition.
     *
     * @return map
     */
    public Map<String, HasFormAssociation> associationsMap()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/definitions/" + getNode().getQName().toString() + "/forms");

        Map<String, Association> associations = getFactory().associations(getBranch(), response);
        return _convert(associations);
    }

    /**
     * Retrieves associations pointing to any forms for this type definition.
     *
     * @return list
     */
    public List<HasFormAssociation> associationsList()
    {
        Map<String, HasFormAssociation> map = associationsMap();

        List<HasFormAssociation> list = new ArrayList<HasFormAssociation>();
        for (HasFormAssociation association : map.values())
        {
            list.add(association);
        }

        return list;
    }

    /**
     * Retrieves a specific form for this type definition.
     *
     * @param formKey
     *
     * @return node
     */
    public Form read(String formKey)
    {
        Form form = null;

        try
        {
            Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/definitions/" + getNode().getQName().toString() + "/forms/" + formKey);
            form = (Form) getFactory().node(getBranch(), response);
        }
        catch (Exception ex)
        {
            // swallow for the time being
            // TODO: the remote layer needs to hand back more interesting more interesting
            // TODO: information so that we can detect a proper 404
        }

        return form;
    }

    public Form create(String formKey)
    {
        return create(formKey, JsonUtil.createObject());
    }

    /**
     * Creates a form.
     *
     * @param formKey
     * @param object
     * @return
     */
    public Form create(String formKey, ObjectNode object)
    {
        // create the form
        object.put(Node.FIELD_TYPE_QNAME, Form.QNAME.toString());
        Form form = (Form) getBranch().nodes().create(object);

        // create the association
        HasFormAssociation association = (HasFormAssociation) getNode().associate(form, HasFormAssociation.QNAME);
        association.set(HasFormAssociation.FIELD_FORM_KEY, formKey);
        association.update();

        return form;
    }

    /**
     * Deletes a form.
     *
     * @param formKey
     */
    public void delete(String formKey)
    {
        Form form = read(formKey);
        if (form == null)
        {
            throw new RuntimeException("Unable to find form for form-key: " + formKey + " for node: " + getNode().getId());
        }

        form.delete();
    }

}
