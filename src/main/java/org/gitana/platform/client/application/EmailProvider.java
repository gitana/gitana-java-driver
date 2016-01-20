/**
 * Copyright 2016 Gitana Software, Inc.
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

import org.gitana.platform.client.support.Selfable;

/**
 * @author uzi
 */
public interface EmailProvider extends ApplicationDocument, Selfable
{
    public static String FIELD_HOST = "host";
    public static String FIELD_PORT = "port";
    public static String FIELD_USERNAME = "username";
    public static String FIELD_PASSWORD = "password";

    // smtp properties
    public static String FIELD_SMTP_ENABLED  = "smtp_enabled";
    public static String FIELD_SMTP_REQUIRES_AUTH = "smtp_requires_auth";
    public static String FIELD_SMTP_IS_SECURE = "smtp_is_secure";
    public static String FIELD_SMTP_STARTTLS_ENABLED = "smtp_starttls_enabled";

    public void setHost(String host);
    public String getHost();

    public void setPort(int port);
    public int getPort();

    public void setUsername(String username);
    public String getUsername();

    public void setPassword(String password);
    public String getPassword();

    public void setSmtpEnabled(boolean smtpEnabled);
    public boolean getSmtpEnabled();

    public void setSmtpRequiresAuth(boolean smtpRequiresAuth);
    public boolean getSmtpRequiresAuth();

    public void setSmtpIsSecure(boolean smtpIsSecure);
    public boolean getSmtpIsSecure();

    public void setSmtpStartTLSEnabled(boolean smtpStartTlsEnabled);
    public boolean getSmtpStartTLSEnabled();

    public void send(Email email);
}
