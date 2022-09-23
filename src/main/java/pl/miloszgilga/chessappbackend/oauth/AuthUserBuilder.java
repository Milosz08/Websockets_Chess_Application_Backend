/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LocalUserBuilder.java
 * Last modified: 19/09/2022, 22:38
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

import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.List;

import pl.miloszgilga.chessappbackend.security.SecurityHelper;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class AuthUserBuilder {

    private final SecurityHelper securityHelper;

    AuthUserBuilder(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    public AuthUser buildUser(LocalUserModel user, Map<String, Object> attributes, OidcIdToken token, OidcUserInfo info) {
        final List<SimpleGrantedAuthority> authorities = securityHelper.generateSimpleGrantedAuthorities(user.getRoles());
        final var authUser = new AuthUser(user, authorities, token, info);
        authUser.setAttributes(attributes);
        return authUser;
    }
}