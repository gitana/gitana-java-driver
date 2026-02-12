/**
 * Copyright 2026 Gitana Software, Inc.
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
 *   info@gitana.io
 */
package org.gitana.platform.client.application;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.gitana.platform.support.TypedIDConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author uzi
 */
public class EmailProviderImpl extends AbstractApplicationDocumentImpl implements EmailProvider
{
    public EmailProviderImpl(Application application, ObjectNode obj, boolean isSaved)
    {
        super(application, obj, isSaved);
    }

    @Override
    public String getTypeId()
    {
        return TypedIDConstants.TYPE_EMAIL_PROVIDER;
    }

    @Override
    public String getResourceUri()
    {
        return "/applications/" + getApplicationId() + "/emailproviders/" + getId();
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equals = false;

        if (object instanceof EmailProvider)
        {
            EmailProvider other = (EmailProvider) object;

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
        Email reset = getApplication().readEmail(getId());

        this.reload(reset.getObject());
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // API
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setHost(String host)
    {
        set(FIELD_HOST, host);
    }

    @Override
    public String getHost()
    {
        return getString(FIELD_HOST);
    }

    @Override
    public void setPort(int port)
    {
        set(FIELD_PORT, port);
    }

    @Override
    public int getPort()
    {
        return getInt(FIELD_PORT);
    }

    @Override
    public void setUsername(String username)
    {
        set(FIELD_USERNAME, username);
    }

    @Override
    public String getUsername()
    {
        return getString(FIELD_USERNAME);
    }

    @Override
    public void setPassword(String password)
    {
        set(FIELD_PASSWORD, password);
    }

    @Override
    public String getPassword()
    {
        return getString(FIELD_PASSWORD);
    }

    @Override
    public void setSmtpEnabled(boolean smtpEnabled)
    {
        set(FIELD_SMTP_ENABLED, smtpEnabled);
    }

    @Override
    public boolean getSmtpEnabled()
    {
        return getBoolean(FIELD_SMTP_ENABLED);
    }

    @Override
    public void setSmtpRequiresAuth(boolean smtpRequiresAuth)
    {
        set(FIELD_SMTP_REQUIRES_AUTH, smtpRequiresAuth);
    }

    @Override
    public boolean getSmtpRequiresAuth()
    {
        return getBoolean(FIELD_SMTP_REQUIRES_AUTH);
    }

    @Override
    public void setSmtpIsSecure(boolean smtpIsSecure)
    {
        set(FIELD_SMTP_IS_SECURE, smtpIsSecure);
    }

    @Override
    public boolean getSmtpIsSecure()
    {
        return getBoolean(FIELD_SMTP_IS_SECURE);
    }

    @Override
    public void setSmtpStartTLSEnabled(boolean smtpStartTlsEnabled)
    {
        set(FIELD_SMTP_STARTTLS_ENABLED, smtpStartTlsEnabled);
    }

    @Override
    public boolean getSmtpStartTLSEnabled()
    {
        return getBoolean(FIELD_SMTP_STARTTLS_ENABLED);
    }

    @Override
    public void send(Email email)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email.getId());

        getRemote().post(getResourceUri() + "/send", params);
    }
    
}
