/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AppOAuth2UserService.java
 * Last modified: 21/09/2022, 01:57
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

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.*;

import pl.miloszgilga.chessappbackend.network.auth.SignupService;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;

import static pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier.findSupplierBasedRegistrationId;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class AppOAuth2UserService extends DefaultOAuth2UserService {

    private final SignupService authService;

    //------------------------------------------------------------------------------------------------------------------

    public AppOAuth2UserService(@Lazy SignupService authService) {
        this.authService = authService;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            final Map<String, Object> userAttributes = new HashMap<>(oAuth2User.getAttributes());
            final String credentialsSupplierRaw = userRequest.getClientRegistration().getRegistrationId();
            final CredentialsSupplier provider = findSupplierBasedRegistrationId(credentialsSupplierRaw);
            final var registrationData = new OAuth2RegistrationData(provider, userAttributes);

            return authService.registrationProcessingFactory(registrationData);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthException.OAuth2AuthenticationProcessingException(ex.getMessage());
        }
    }
}
