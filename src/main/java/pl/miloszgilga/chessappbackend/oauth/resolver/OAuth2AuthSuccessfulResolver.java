/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OAuth2AuthSuceessfulResolver.java
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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.*;

import java.net.URI;
import java.util.Set;
import java.util.Optional;
import java.io.IOException;

import pl.miloszgilga.lib.jmpsl.util.ServletPathUtil;
import pl.miloszgilga.lib.jmpsl.util.cookie.CookieUtil;

import pl.miloszgilga.chessappbackend.oauth.AuthUser;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class OAuth2AuthSuccessfulResolver extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthSuccessfulResolver.class);

    private final EnvironmentVars environment;
    private final JsonWebTokenCreator webTokenCreator;

    //------------------------------------------------------------------------------------------------------------------

    public OAuth2AuthSuccessfulResolver(EnvironmentVars environment, JsonWebTokenCreator webTokenCreator) {
        this.environment = environment;
        this.webTokenCreator = webTokenCreator;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
            throws IOException {
        final String targetUrl = determineTargetUrl(req, res, auth);
        if (res.isCommitted()) {
            LOGGER.info("Response has already been committed. Unable to redirect to address: {}", targetUrl);
            return;
        }
        clearAuthenticationAttributes(req);
        CookieUtil.deleteMultipleCookies(req, res, Set.of(environment.getOauth2SessionRememberCookieName(),
                environment.getOauth2AfterLoginRedirectUriCookieName(),
                environment.getOauth2AfterSignupRedirectUriCookieName()));

        getRedirectStrategy().sendRedirect(req, res, targetUrl);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected String determineTargetUrl(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
        final AuthUser localAuthUser = (AuthUser) auth.getPrincipal();
        final String token = webTokenCreator.createUserCredentialsToken(auth);
        final String credentialsSupplier = localAuthUser.getUserModel().getCredentialsSupplier().getName();

        if (localAuthUser.getUserModel().getIsActivated()) {
            final String redirectLoginUri = checkIfRedirectUriIsValidAndReturn(req,
                    environment.getOauth2AfterLoginRedirectUriCookieName());

            return ServletPathUtil.redirectTokenUri(token, redirectLoginUri, credentialsSupplier).toString();
        }
        final String redirectSignupUri = checkIfRedirectUriIsValidAndReturn(req,
                environment.getOauth2AfterSignupRedirectUriCookieName());

        return ServletPathUtil.redirectTokenUri(token, redirectSignupUri, credentialsSupplier).toString();
    }

    //------------------------------------------------------------------------------------------------------------------

    private boolean checkIfUserIsAuthorizedViaRequestUri(String uri) {
        final URI redirectClientUri = URI.create(uri);
        return environment.getOauth2RedirectUris().stream().noneMatch(reqUri -> {
            final URI authorizedUri = URI.create(reqUri);
            return authorizedUri.getHost().equalsIgnoreCase(redirectClientUri.getHost()) &&
                    authorizedUri.getPort() == redirectClientUri.getPort();
        });
    }

    //------------------------------------------------------------------------------------------------------------------

    private String checkIfRedirectUriIsValidAndReturn(HttpServletRequest req, String cookieName) {
        final Optional<String> redirectUri = CookieUtil.getCookieValue(req, cookieName);
        if (redirectUri.isEmpty() || checkIfUserIsAuthorizedViaRequestUri(redirectUri.get())) {
            LOGGER.error("Attempt to authenticate via OAuth2 by not supported URI.");
            throw new AuthException.OAuth2NotSupportedUriException("Redirect URI is not supported by OAuth2 service.");
        }
        return redirectUri.orElse(getDefaultTargetUrl());
    }
}
