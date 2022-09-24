/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: CookieOAuth2ReqRepository.java
 * Last modified: 21/09/2022, 02:29
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

package pl.miloszgilga.chessappbackend.oauth;

import com.nimbusds.oauth2.sdk.util.StringUtils;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.miloszgilga.chessappbackend.utils.CookieHelper;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;

//----------------------------------------------------------------------------------------------------------------------

public class CookieOAuth2ReqRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final CookieHelper cookieHelper;
    private final EnvironmentVars environment;
    private final int cookieExpInSec;

    public CookieOAuth2ReqRepository(CookieHelper cookieHelper, EnvironmentVars environment) {
        this.cookieHelper = cookieHelper;
        this.environment = environment;
        this.cookieExpInSec = environment.getOauth2CookieExpiredMinutes() * 60;
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest req) {
        return cookieHelper.getCookie(req, environment.getOauth2SessionRememberCookieName())
                .map(c -> cookieHelper.deserializeCookieObjectData(c, OAuth2AuthorizationRequest.class))
                .orElseThrow(() -> {
                    throw new AuthException.OAuth2AuthenticationProcessingException("Unable to load account data.");
                });
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authReq, HttpServletRequest req, HttpServletResponse res) {
        final String baseRedirPathName = environment.getOauth2BaseRedirectUriCookieName();
        final String afterLoginRedirPathName = environment.getOauth2AfterLoginRedirectUriCookieName();
        final String afterSignupRedirPathName = environment.getOauth2AfterSignupRedirectUriCookieName();

        if (authReq == null) {
            cookieHelper.deleteCookie(req, res, environment.getOauth2SessionRememberCookieName());
            cookieHelper.deleteCookie(req, res, afterLoginRedirPathName);
            cookieHelper.deleteCookie(req, res, afterSignupRedirPathName);
            return;
        }

        cookieHelper.addCookie(res, environment.getOauth2SessionRememberCookieName(),
                cookieHelper.serializeCookieObjectData(authReq), cookieExpInSec);

        final String redirUriBase = req.getParameter(baseRedirPathName);
        if (StringUtils.isNotBlank(req.getParameter(afterLoginRedirPathName))) {
            final String redirUriAfterLogin = redirUriBase + "/" + req.getParameter(afterLoginRedirPathName);
            cookieHelper.addCookie(res, afterLoginRedirPathName, redirUriAfterLogin, cookieExpInSec);
        }
        if (StringUtils.isNotBlank(req.getParameter(afterSignupRedirPathName))) {
            final String redirUriAfterSignup = redirUriBase + "/" + req.getParameter(afterSignupRedirPathName);
            cookieHelper.addCookie(res, afterSignupRedirPathName, redirUriAfterSignup, cookieExpInSec);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest req) {
        return loadAuthorizationRequest(req);
    }
}
