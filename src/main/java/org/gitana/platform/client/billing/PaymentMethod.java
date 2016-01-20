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

package org.gitana.platform.client.billing;

import org.gitana.platform.GitanaObject;
import org.gitana.platform.client.support.Selfable;

/**
 * @author uzi
 */
public interface PaymentMethod extends GitanaObject, Selfable
{
    // fields
    public final static String FIELD_ID = "id";
    public final static String FIELD_EXPIRATION_MONTH = "expirationMonth";
    public final static String FIELD_EXPIRATION_YEAR = "expirationYear";
    public final static String FIELD_HOLDER_NAME = "holderName";
    public final static String FIELD_NUMBER_LAST4 = "numberLast4";
    public final static String FIELD_NUMBER = "number";
    public final static String FIELD_NUMBER_MASKED = "numberMasked";
    public final static String FIELD_TYPE = "type";

    public String getId();

    public int getExpirationMonth();
    public void setExpirationMonth(int expirationMonth);

    public int getExpirationYear();
    public void setExpirationYear(int expirationYear);

    public String getHolderName();
    public void setHolderName(String holderName);

    public String getNumber();
    public void setNumber(String number);

    // get only
    public String getNumberLastFour();
    public String getNumberMasked();
    public String getType();

}
