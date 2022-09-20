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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import pl.miloszgilga.chessappbackend.oauth.AuthUser;
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
}
