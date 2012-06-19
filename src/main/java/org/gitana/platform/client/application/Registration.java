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

package org.gitana.platform.client.application;

import org.codehaus.jackson.node.ObjectNode;
import org.gitana.platform.client.support.Selfable;

import java.util.List;

/**
 * A registration object is intended for us in registering a new user to a web site.
 *
 * You can either:
 *
 *   1) create a registration object with all of the details of the user
 *   2) complete the registration (sending a welcome email)
 *
 * Or:
 *
 *   1) create a registration object with the email and optional additional details
 *   2) send a confirmation email
 *   3) populate the object with additional details
 *   3) complete the registration (sending a welcome email)
 *
 * @author uzi
 */
public interface Registration extends ApplicationDocument, Selfable
{
    // user fields
    public final static String FIELD_USER_EMAIL = "userEmail";
    public final static String FIELD_USER_NAME = "userName";
    public final static String FIELD_USER_DOMAIN_ID = "userDomainId";
    public final static String FIELD_USER_PROPERTIES = "userProperties";

    // signup properties
    public final static String FIELD_SIGNUP_PROPERTIES = "signupProperties";

    // tenant support
    public final static String FIELD_TENANT_TITLE = "tenantTitle";
    public final static String FIELD_TENANT_DESCRIPTION = "tenantDescription";
    public final static String FIELD_TENANT_PLAN_KEY = "tenantPlanKey";
    public final static String FIELD_TENANT_REGISTRAR_ID = "tenantRegistrarId";

    // payment method
    public final static String FIELD_PAYMENT_METHOD_ID = "paymentMethodId";

    // flow control
    public final static String FIELD_CONFIRMATION_SENT = "confirmationSent";

    // final state
    public final static String FIELD_COMPLETED = "completed";
    public final static String FIELD_COMPLETED_PRINCIPAL_ID = "completedPrincipalId";
    public final static String FIELD_COMPLETED_TENANT_ID = "completedTenantId";

    // email provider id
    public final static String FIELD_EMAIL_PROVIDER_ID = "emailProviderId";

    // configuration for email configurations
    public final static String FIELD_EMAILS = "emails";

    // the ids of any discounts to apply
    public final static String FIELD_DISCOUNTS = "discounts";

    // the ids of any addons to apply
    public final static String FIELD_ADDONS = "addons";

    // whether the registration is active
    public final static String FIELD_ACTIVE = "active";

    public void setUserEmail(String userEmail);
    public String getUserEmail();

    public void setUserName(String userName);
    public String getUserName();

    public void setUserDomainId(String userDomainId);
    public String getUserDomainId();

    public ObjectNode getUserProperties();
    public void setUserProperties(ObjectNode userProperties);

    public ObjectNode getSignupProperties();
    public void setSignupProperties(ObjectNode signupProperties);

    public void setTenantTitle(String title);
    public String getTenantTitle();

    public void setTenantDescription(String description);
    public String getTenantDescription();

    public void setTenantPlanKey(String planKey);
    public String getTenantPlanKey();

    public void setTenantRegistrarId(String tenantRegistrarId);
    public String getTenantRegistrarId();

    public void setConfirmationSent(boolean confirmationSent);
    public boolean getConfirmationSent();

    public void setCompleted(boolean completed);
    public boolean getCompleted();

    public String getCompletedPrincipalId();
    public void setCompletedPrincipalId(String completedPrincipalId);

    public String getCompletedTenantId();
    public void setCompletedTenantId(String completedTenantId);

    public ObjectNode getEmailConfiguration(String emailKey);
    public void setEmailConfiguration(String emailKey, ObjectNode email);

    public String getEmailProviderId();
    public void setEmailProviderId(String emailProviderId);

    public String getPaymentMethodId();
    public void setPaymentMethodId(String paymentMethodId);

    public void sendConfirmationEmail();
    public void sendWelcomeEmail();
    public void confirm(String newUserPassword);
    public void confirm(String newUserPassword, ObjectNode paymentMethodObject);

    public List<String> getDiscounts();
    public void setDiscounts(List<String> discounts);

    public List<String> getAddons();
    public void setAddons(List<String> addons);

    public boolean getActive();
    public void setActive(boolean active);

}

