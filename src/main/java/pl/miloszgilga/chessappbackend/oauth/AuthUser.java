/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LocalUser.java
 * Last modified: 19/09/2022, 22:19
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

import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Map;
import java.util.List;
import java.io.Serializable;

import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

public class AuthUser extends User implements OAuth2User, OidcUser, Serializable {
    private static final long serialVersionUID = 1L;

    private final OidcIdToken oidcIdToken;
    private final OidcUserInfo oidcUserInfo;
    private final LocalUserModel userModel;
    private Map<String, Object> attributes;

    public AuthUser(LocalUserModel userModel, List<SimpleGrantedAuthority> authorities) {
        this(userModel, authorities, null, null);
    }

    AuthUser(LocalUserModel user, List<SimpleGrantedAuthority> authorities, OidcIdToken token, OidcUserInfo info) {
        super(user.getNickname(), user.getPassword(), user.isActivated(), true, true, !user.isBlocked(), authorities);
        this.userModel = user;
        this.oidcIdToken = token;
        this.oidcUserInfo = info;
    }

    void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getClaims() {
        return this.attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return this.oidcUserInfo;
    }

    @Override
    public OidcIdToken getIdToken() {
        return this.oidcIdToken;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.userModel.getNickname();
    }

    public LocalUserModel getUserModel() {
        return userModel;
    }
}
