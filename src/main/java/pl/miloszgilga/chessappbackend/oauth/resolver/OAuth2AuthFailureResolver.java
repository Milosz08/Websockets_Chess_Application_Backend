/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OAuth2AuthFailureResolver.java
 * Last modified: 21/09/2022, 02:50
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

package pl.miloszgilga.chessappbackend.oauth.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.util.Set;
import javax.servlet.http.*;
import java.io.IOException;

import pl.miloszgilga.lib.jmpsl.util.ServletPathUtil;
import pl.miloszgilga.lib.jmpsl.util.cookie.CookieUtil;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class OAuth2AuthFailureResolver extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthFailureResolver.class);

    private final EnvironmentVars environment;

    //------------------------------------------------------------------------------------------------------------------

    public OAuth2AuthFailureResolver(EnvironmentVars environment) {
        this.environment = environment;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
            throws IOException {
        final String targetUrl = CookieUtil
                .getCookieValue(req, environment.getOauth2AfterLoginRedirectUriCookieName())
                .orElse("/");

        LOGGER.error("OAuth2 authorization failure: Error: {}", ex.getMessage());
        deleteOAuth2AuthorizationRequestCookies(req, res);

        final String redirectPath = ServletPathUtil.redirectErrorUri(ex.getLocalizedMessage(), targetUrl).toString();
        getRedirectStrategy().sendRedirect(req, res, redirectPath);
    }

    //------------------------------------------------------------------------------------------------------------------

    private void deleteOAuth2AuthorizationRequestCookies(HttpServletRequest req, HttpServletResponse res) {
        CookieUtil.deleteMultipleCookies(req, res, Set.of(environment.getOauth2SessionRememberCookieName(),
                environment.getOauth2AfterLoginRedirectUriCookieName(),
                environment.getOauth2AfterSignupRedirectUriCookieName()));
    }
}
