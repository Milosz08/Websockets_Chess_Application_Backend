/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OAuth2RegistrationData.java
 * Last modified: 21/09/2022, 02:06
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

package pl.miloszgilga.chessappbackend.oauth.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.util.Map;

import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;

//----------------------------------------------------------------------------------------------------------------------

@Data
@AllArgsConstructor
public class OAuth2RegistrationData {
    private CredentialsSupplier supplier;
    private Map<String, Object> attributes;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    public OAuth2RegistrationData(CredentialsSupplier supplier, Map<String, Object> attributes) {
        this.supplier = supplier;
        this.attributes = attributes;
    }
}
