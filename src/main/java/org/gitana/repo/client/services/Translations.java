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

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.gitana.repo.client.Branch;
import org.gitana.repo.client.Gitana;
import org.gitana.repo.client.Repository;
import org.gitana.repo.client.Response;
import org.gitana.repo.client.nodes.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author uzi
 */
public class Translations extends AbstractService
{
    private Node node;

    public Translations(Gitana gitana, Node node)
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

    public Node getNode()
    {
        return node;
    }

    public String getNodeId()
    {
        return getNode().getId();
    }

    /**
     * Creates a new translation.
     *
     * @param edition the edition
     * @param locale the locale
     * @param object the json object
     *
     * @return node
     */
    public Node create(String edition, Locale locale, ObjectNode object)
    {
        String uri = "/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getNodeId() + "/i18n?edition=" + edition + "&locale=" + locale.toString();
        Response r1 = getRemote().post(uri, object);
        String nodeId = r1.getId();

        Response r2 = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + nodeId);
        return getFactory().node(getBranch(), r2);
    }

    /**
     * @return the editions available for this master node
     */
    public List<String> getEditions()
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getNodeId() + "/i18n/editions");

        ArrayNode array = (ArrayNode) response.getObjectNode().get("editions");

        List<String> editions = new ArrayList<String>();
        for (int i = 0; i < array.size(); i++)
        {
            editions.add(array.get(i).getTextValue());
        }

        return editions;
    }

    /**
     * Hands back the locales available for a given edition of translations for this master node.
     *
     * @param edition
     * @return
     */
    public List<Locale> getLocales(String edition)
    {
        Response response = getRemote().get("/repositories/" + getRepositoryId() + "/branches/" + getBranchId() + "/nodes/" + getNodeId() + "/i18n/locales?edition=" + edition);

        ArrayNode array = (ArrayNode) response.getObjectNode().get("locales");

        List<Locale> locales = new ArrayList<Locale>();
        for (int i = 0; i < array.size(); i++)
        {
            String localeString = array.get(i).getTextValue();
            Locale locale = org.gitana.util.I18NUtil.parseLocale(localeString);

            locales.add(locale);

        }

        return locales;
    }

    /**
     * Reads a translation for the tip edition of the master node in the given locale.
     *
     * @param locale
     * @return
     */
    public Node read(Locale locale)
    {
        return read(null, locale);
    }

    /**
     * Reads a translation for the specified edition of the master node in the given locale.
     *
     * @param edition
     * @param locale
     * @return
     */
    public Node read(String edition, Locale locale)
    {
        String uri = "/repositories/" + this.getRepositoryId() + "/branches/" + this.getBranchId() + "/nodes/" + this.getNodeId() + "/i18n?locale=" + locale.toString();
        if (edition != null)
        {
            uri += "&edition=" + edition;
        }

        Response response = getRemote().get(uri);
        return getFactory().node(getBranch(), response);
    }
}
