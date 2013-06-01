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

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.TypedIDConstants;
import org.gitana.util.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author uzi
 */
public class RegistrationImpl extends AbstractApplicationDocumentImpl implements Registration
{
    public RegistrationImpl(Application application, ObjectNode obj, boolean isSaved)
    {
        super(application, obj, isSaved);

        initRegistration();
    }

    protected void initRegistration()
    {
        if (!has(FIELD_USER_PROPERTIES))
        {
            set(FIELD_USER_PROPERTIES, JsonUtil.createObject());
        }

        if (!has(FIELD_SIGNUP_PROPERTIES))
        {
            set(FIELD_SIGNUP_PROPERTIES, JsonUtil.createObject());
        }

        if (!has(FIELD_DISCOUNTS))
        {
            set(FIELD_DISCOUNTS, JsonUtil.createArray());
        }

        if (!has(FIELD_ADDONS))
        {
            set(FIELD_ADDONS, JsonUtil.createArray());
        }

        if (!has(FIELD_ACTIVE))
        {
            set(FIELD_ACTIVE, false);
        }
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_REGISTRATION;
    }

    @Override
    public String getResourceUri()
    {
        return "/applications/" + getApplicationId() + "/registrations/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof Registration)
        {
            Registration other = (Registration) object;

            equals = (this.getId().equals(other.getId()));
        }

        return equals;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // SELFABLE
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update()
    {
        getRemote().put(getResourceUri(), getObject());
    }

    @Override
    public void delete()
    {
        getRemote().delete(getResourceUri());
    }

    @Override
    public void reload()
    {
        Registration registration = getApplication().readRegistration(getId());

        this.reload(registration.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // API
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getUserEmail()
    {
        return getString(FIELD_USER_EMAIL);
    }

    @Override
    public void setUserEmail(String userEmail)
    {
        set(FIELD_USER_EMAIL, userEmail);
    }

    @Override
    public String getUserName()
    {
        return getString(FIELD_USER_NAME);
    }

    @Override
    public void setUserName(String username)
    {
        set(FIELD_USER_NAME, username);
    }
    
    @Override
    public void setUserDomainId(String userDomainId) 
    {
        set(FIELD_USER_DOMAIN_ID, userDomainId);
    }

    @Override
    public String getUserDomainId() 
    {
        return getString(FIELD_USER_DOMAIN_ID);
    }

    @Override
    public void setUserProperties(ObjectNode userProperties)
    {
        set(FIELD_USER_PROPERTIES, userProperties);
    }

    @Override
    public ObjectNode getUserProperties()
    {
        return getObject(FIELD_USER_PROPERTIES);
    }

    @Override
    public void setSignupProperties(ObjectNode signupProperties)
    {
        set(FIELD_SIGNUP_PROPERTIES, signupProperties);
    }

    @Override
    public ObjectNode getSignupProperties()
    {
        return getObject(FIELD_SIGNUP_PROPERTIES);
    }

    @Override
    public void setTenantTitle(String title)
    {
        set(FIELD_TENANT_TITLE, title);
    }

    @Override
    public String getTenantTitle()
    {
        return getString(FIELD_TENANT_TITLE);
    }

    @Override
    public void setTenantDescription(String description)
    {
        set(FIELD_TENANT_DESCRIPTION, description);
    }

    @Override
    public String getTenantDescription()
    {
        return getString(FIELD_TENANT_DESCRIPTION);
    }

    @Override
    public String getTenantPlanKey()
    {
        return getString(FIELD_TENANT_PLAN_KEY);
    }

    @Override
    public void setTenantPlanKey(String tenantPlanKey)
    {
        set(FIELD_TENANT_PLAN_KEY, tenantPlanKey);
    }

    @Override
    public void setTenantRegistrarId(String tenantRegistrarId) 
    {
        set(FIELD_TENANT_REGISTRAR_ID, tenantRegistrarId);
    }

    @Override
    public String getTenantRegistrarId() 
    {
        return getString(FIELD_TENANT_REGISTRAR_ID);
    }

    @Override
    public void setConfirmationSent(boolean confirmationSent) 
    {
        set(FIELD_CONFIRMATION_SENT, confirmationSent);
    }

    @Override
    public boolean getConfirmationSent() 
    {
        return getBoolean(FIELD_CONFIRMATION_SENT);
    }

    @Override
    public void setCompleted(boolean completed) 
    {
        set(FIELD_COMPLETED, completed);
    }

    @Override
    public boolean getCompleted() 
    {
        return getBoolean(FIELD_COMPLETED);
    }

    @Override
    public String getCompletedPrincipalId() 
    {
        return getString(FIELD_COMPLETED_PRINCIPAL_ID);
    }

    @Override
    public void setCompletedPrincipalId(String completedPrincipalId) 
    {
        set(FIELD_COMPLETED_PRINCIPAL_ID, completedPrincipalId);
    }

    @Override
    public String getCompletedTenantId() 
    {
        return getString(FIELD_COMPLETED_TENANT_ID);
    }

    @Override
    public void setCompletedTenantId(String completedTenantId) 
    {
        set(FIELD_COMPLETED_TENANT_ID, completedTenantId);
    }

    @Override
    public ObjectNode getEmailConfiguration(String emailKey) 
    {
        ObjectNode emailConfiguration = null;
        
        ObjectNode emails = getObject(FIELD_EMAILS);
        if (emails != null)
        {
            emailConfiguration = JsonUtil.objectGetObject(emails, emailKey);
        }
        
        return emailConfiguration;
    }

    @Override
    public void setEmailConfiguration(String emailKey, ObjectNode email) 
    {
        ObjectNode emails = getObject(FIELD_EMAILS);
        if (emails == null)
        {
            emails = JsonUtil.createObject();
            set(FIELD_EMAILS, emails);
        }

        emails.put(emailKey, email);
    }

    @Override
    public String getEmailProviderId()
    {
        return getString(FIELD_EMAIL_PROVIDER_ID);
    }

    @Override
    public void setEmailProviderId(String emailProviderId)
    {
        set(FIELD_EMAIL_PROVIDER_ID, emailProviderId);
    }

    @Override
    public String getPaymentMethodId()
    {
        return getString(FIELD_PAYMENT_METHOD_ID);
    }

    @Override
    public void setPaymentMethodId(String paymentMethodId)
    {
        set(FIELD_PAYMENT_METHOD_ID, paymentMethodId);
    }

    @Override
    public void sendConfirmationEmail()
    {
        getRemote().post(getResourceUri() + "/send/confirmation", getObject());
    }

    @Override
    public void sendWelcomeEmail()
    {
        getRemote().post(getResourceUri() + "/send/welcome", getObject());
    }

    @Override
    public void confirm(String newUserPassword)
    {
        confirm(newUserPassword, null);
    }

    @Override
    public void confirm(String newUserPassword, ObjectNode paymentMethodObject)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("password", newUserPassword);

        if (paymentMethodObject != null)
        {
            getRemote().post(getResourceUri() + "/confirm", params, paymentMethodObject);
        }
        else
        {
            getRemote().post(getResourceUri() + "/confirm", params);
        }
    }

    @Override
    public List<String> getDiscounts()
    {
        return (List) get(FIELD_DISCOUNTS);
    }

    @Override
    public void setDiscounts(List<String> discounts)
    {
        set(FIELD_DISCOUNTS, discounts);
    }

    @Override
    public List<String> getAddons()
    {
        return (List) get(FIELD_ADDONS);
    }

    @Override
    public void setAddons(List<String> addons)
    {
        set(FIELD_ADDONS, addons);
    }

    @Override
    public boolean getActive()
    {
        return getBoolean(FIELD_ACTIVE);
    }

    @Override
    public void setActive(boolean active)
    {
        set(FIELD_ACTIVE, active);
    }

}
