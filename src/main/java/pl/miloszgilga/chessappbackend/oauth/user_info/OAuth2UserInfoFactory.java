/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OAuth2UserInfoFactory.java
 * Last modified: 21/09/2022, 01:45
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

package pl.miloszgilga.chessappbackend.oauth.user_info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.Map;

import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;

import static pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier.getAllSuppliers;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class OAuth2UserInfoFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2UserInfoFactory.class);

    public OAuth2UserInfo getOAuth2UserInfo(CredentialsSupplier supplier, Map<String, Object> attributes) {
        switch(supplier) {
            case FACEBOOK:
                return new FacebookOAuth2UserInfo(attributes);
            case GOOGLE:
                return new GoogleOAuth2UserInfo(attributes);
            default:
                LOGGER.error("Attempt to login via unsupported supplier. Unsupported supplier: {}", supplier);
                throw new AuthException.OAuth2CredentialsSupplierMalformedException(String.format(
                        "Login via supplier: %s is not supported yet. Try via: %s", supplier, getAllSuppliers()));
        }
    }
}
