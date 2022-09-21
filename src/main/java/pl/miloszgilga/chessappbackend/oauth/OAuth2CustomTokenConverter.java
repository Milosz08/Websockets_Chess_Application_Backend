/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OAuth2CustomTokenConverter.java
 * Last modified: 21/09/2022, 03:06
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

import org.springframework.util.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

//----------------------------------------------------------------------------------------------------------------------

public class OAuth2CustomTokenConverter implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {

    private final OAuth2AccessToken.TokenType defAccessToken = OAuth2AccessToken.TokenType.BEARER;
    private final String[] tokenParameters = { ACCESS_TOKEN, TOKEN_TYPE, EXPIRES_IN, REFRESH_TOKEN, SCOPE };

    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> sourceParameters) {
        final String accessToken = (String) sourceParameters.get(ACCESS_TOKEN);
        int expiresIn = (Integer)sourceParameters.get(EXPIRES_IN);

        Set<String> scopes = Collections.emptySet();
        if (sourceParameters.containsKey(SCOPE)) {
            String scope = (String) sourceParameters.get(SCOPE);
            scopes = Arrays.stream(StringUtils.delimitedListToStringArray(scope, " ")).collect(Collectors.toSet());
        }

        final Map<String, Object> additionalParameters = new LinkedHashMap<>();
        sourceParameters.entrySet().stream()
                .filter(e -> !getTokenResponseParameters().contains(e.getKey()))
                .forEach(e -> additionalParameters.put(e.getKey(), e.getValue()));

        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(defAccessToken)
                .expiresIn(expiresIn)
                .scopes(scopes)
                .additionalParameters(additionalParameters)
                .build();
    }

    private Set<String> getTokenResponseParameters() {
        return Stream.of(tokenParameters).collect(Collectors.toSet());
    }
}
