/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SecurityHelper.java
 * Last modified: 19/09/2022, 22:28
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

package pl.miloszgilga.chessappbackend.security;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;

import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import pl.miloszgilga.chessappbackend.oauth.AuthUser;
import pl.miloszgilga.chessappbackend.oauth.OAuth2CustomTokenConverter;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserRoleModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class SecurityHelper {

    public List<SimpleGrantedAuthority> generateSimpleGrantedAuthorities(final Set<LocalUserRoleModel> roles) {
        final List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (LocalUserRoleModel role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName().getRoleName()));
        }
        return authorities;
    }

    AuthUser generateSecurityUserByModelData(final LocalUserModel model) {
        return new AuthUser(model, generateSimpleGrantedAuthorities(model.getRoles()));
    }

    OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> authTokenCodeResponseToTheClient() {
        final var converter = new OAuth2AccessTokenResponseHttpMessageConverter();
        converter.setAccessTokenResponseConverter(new OAuth2CustomTokenConverter());
        final var template = new RestTemplate(Arrays.asList(new FormHttpMessageConverter(), converter));
        template.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        final var responseClient = new DefaultAuthorizationCodeTokenResponseClient();
        responseClient.setRestOperations(template);
        return responseClient;
    }

    public String hashingStringValue(String value, char hashChar) {
        final String hashedPart = value.substring(0, value.indexOf('@'));
        int hashingCharsCount = 3;
        if (hashedPart.length() < 5) hashingCharsCount = 1;
        final String hashPartVisible = value.substring(0, hashingCharsCount);
        final String hashPartNonVisible = value.substring(hashingCharsCount + 1, value.indexOf('@'));
        final String nonHashPart = value.substring(value.indexOf('@') + 2);
        return hashPartVisible + Character.toString(hashChar).repeat(hashPartNonVisible.length()) + "@" + nonHashPart;
    }
}
