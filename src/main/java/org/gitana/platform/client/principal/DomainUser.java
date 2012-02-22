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

package org.gitana.platform.client.principal;

import org.gitana.platform.client.branch.Branch;
import org.gitana.platform.client.directory.Directory;
import org.gitana.platform.client.identity.Identity;
import org.gitana.platform.client.types.Person;

/**
 * @author uzi
 */
public interface DomainUser extends DomainPrincipal
{
    // fields
    public final static String FIELD_FIRST_NAME = "firstName";
    public final static String FIELD_LAST_NAME = "lastName";
    public final static String FIELD_COMPANY_NAME = "companyName";
    public final static String FIELD_EMAIL = "email";

    // identity
    public final static String FIELD_DIRECTORY_ID = "directoryId";
    public final static String FIELD_IDENTITY_ID = "identityId";

    /**
     * @return first name
     */
    public String getFirstName();

    /**
     * Sets the first name.
     *
     * @param firstName
     */
    public void setFirstName(String firstName);

    /**
     * @return last name
     */
    public String getLastName();

    /**
     * Sets the last name.
     *
     * @param lastName
     */
    public void setLastName(String lastName);

    /**
     * @return company name
     */
    public String getCompanyName();

    /**
     * Sets the company name.
     *
     * @param companyName
     */
    public void setCompanyName(String companyName);

    /**
     * @return email
     */
    public String getEmail();

    /**
     * Sets the email.
     *
     * @param email
     */
    public void setEmail(String email);

    public Person readPerson(Branch branch);
    public Person readPerson(Branch branch, boolean createIfNotFound);

    public boolean hasIdentity();
    public String getDirectoryId();
    public String getIdentityId();
    public Directory readDirectory();
    public Identity readIdentity();

}