/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ApplicationEndpoints.java
 * Last modified: 31/08/2022, 15:07
 * Project name: chess-app-backend
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.chessappbackend.config;

//----------------------------------------------------------------------------------------------------------------------

public class ApplicationEndpoints {

    public static final String BASIC_ENDPOINT = "/javabean/app/v1/";

    //------------------------------------------------------------------------------------------------------------------

    public static final String NEWSLETTER_EMAIL_ENDPOINT = BASIC_ENDPOINT + "newsletter-email";
    public static final String NEWSLETTER_SUBSCRIBE = "/subscribe";
    public static final String NEWSLETTER_ATTEMPT_UNSUBSCRIBE = "/attempt-unsubscribe";
    public static final String NEWSLETTER_UNSUBSCRIBE_VIA_OTA = "/unsubscribe-via-ota";
    public static final String NEWSLETTER_UNSUBSCRIBE_VIA_JWT = "/unsubscribe-via-jwt";

    public static final String AUTH_LOCAL_ENDPOINT = BASIC_ENDPOINT + "auth-local";
    public static final String LOGIN_VIA_LOCAL = "/login-via-local";
    public static final String LOGIN_VIA_OAUTH2 = "/login-via-oauth";
    public static final String ATTEMPT_FINISH_SIGNUP_VIA_OAUTH2 = "/attempt-finish-signup-via-oauth";
    public static final String FINISH_SIGNUP_VIA_OAUTH2 = "/finish-signup-via-oauth";
    public static final String SIGNUP_VIA_LOCAL = "/signup-via-local";

    public static final String RENEW_CREDETIALS_LOCAL = BASIC_ENDPOINT + "renew-credentials";
    public static final String ATTEMPT_TO_CHANGE_PASSWORD = "/attempt-to-change-password";

    public static final String EXPOSE_STATIC_DATA_ENDPOINT = BASIC_ENDPOINT + "static-data";
    public static final String SIGNUP_CALENDAR_DATA = "/signup-calendar-data";
    public static final String SIGNUP_GENDER_DATA = "/signup-gender-data";
    public static final String SIGNUP_COUNTRY_DATA = "/signup-country-data";

    public static final String OTA_TOKEN_ENDPOINT = BASIC_ENDPOINT + "ota-token";
    public static final String CHANGE_PASSWORD = "/change-password";
    public static final String ACTIVATE_ACCOUNT = "/activate-account";
}
