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
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import java.util.Optional;
import java.io.IOException;

import pl.miloszgilga.chessappbackend.utils.CookieHelper;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class OAuth2AuthSuceessfulResolver extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthSuceessfulResolver.class);

    private final CookieHelper cookieHelper;
    private final EnvironmentVars environment;
    private final JsonWebTokenCreator webTokenCreator;

    public OAuth2AuthSuceessfulResolver(EnvironmentVars environment, JsonWebTokenCreator webTokenCreator,
                                        CookieHelper cookieHelper) {
        this.environment = environment;
        this.cookieHelper = cookieHelper;
        this.webTokenCreator = webTokenCreator;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
            throws IOException {
        final String targetUrl = determineTargetUrl(req, res, auth);
        if (res.isCommitted()) {
            LOGGER.info("Response has already been committed. Unable to redirect to address: {}", targetUrl);
            return;
        }

        clearAuthenticationAttributes(req);
        cookieHelper.deleteCookie(req, res, environment.getOauth2SessionRememberCookieName());
        cookieHelper.deleteCookie(req, res, environment.getOauth2RedirectUriCookieName());

        getRedirectStrategy().sendRedirect(req, res, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest req, HttpServletResponse res, Authentication auth) {
        final Optional<String> redirectUri = cookieHelper
                .getCookieValue(req, environment.getOauth2RedirectUriCookieName());

        if (redirectUri.isPresent() && !checkIfUserIsAuthorizedViaRequestUri(redirectUri.get())) {
            LOGGER.error("Attempt to authenticate via OAuth2 by not supported URI. Redirect URI: {}", redirectUri.get());
            throw new AuthException.OAuth2NotSupportedUriException("Redirect URI is not supported by OAuth2 service.");
        }

        final String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        final String token = webTokenCreator.createUserCredentialsToken(auth);

        // TODO: come up with how to send data without any URI parameters
        return UriComponentsBuilder.fromUriString(targetUrl).queryParam("token", token).build().toUriString();
    }

    private boolean checkIfUserIsAuthorizedViaRequestUri(String uri) {
        final URI redirectClientUri = URI.create(uri);
        return environment.getOauth2RedirectUris().stream().anyMatch(reqUri -> {
            final URI authorizedUri = URI.create(reqUri);
            return authorizedUri.getHost().equalsIgnoreCase(redirectClientUri.getHost()) &&
                    authorizedUri.getPort() == redirectClientUri.getPort();
        });
    }
}
