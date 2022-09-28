/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AuthLocalServiceHelper.java
 * Last modified: 23/09/2022, 20:56
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

package pl.miloszgilga.chessappbackend.network.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;

import static pl.miloszgilga.chessappbackend.security.LocalUserRole.USER;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class AuthServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceHelper.class);

    private final JsonWebTokenCreator tokenCreator;
    private final ILocalUserRoleRepository roleRepository;
    private final ILocalUserRepository localUserRepository;

    AuthServiceHelper(JsonWebTokenCreator tokenCreator, ILocalUserRoleRepository roleRepository,
                      ILocalUserRepository localUserRepository) {
        this.tokenCreator = tokenCreator;
        this.roleRepository = roleRepository;
        this.localUserRepository = localUserRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public Set<LocalUserRoleModel> findAndGenerateUserRole() {
        final Set<LocalUserRoleModel> roles = new HashSet<>();
        final Optional<LocalUserRoleModel> foundRole = roleRepository.findRoleByType(USER);
        if (foundRole.isEmpty()) {
            LOGGER.error("Role for user is not present in database.");
            throw new AuthException.RoleNotFoundException("Unable to create new account. Try again later.");
        }
        roles.add(foundRole.get());
        return roles;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    LocalUserModel findUserAndReturnUserData(Long userId) {
        if (userId == null) {
            LOGGER.error("Passed user ID is null. Nulls in indexes are strictly prohibited.");
            throw new AuthException.UserNotFoundException("Unable to load user data. Try again later.");
        }
        final Optional<LocalUserModel> findingUser = localUserRepository.findById(userId);
        if (findingUser.isEmpty()) {
            LOGGER.error("Unable to load user based id req data. Id: {}", userId);
            throw new AuthException.UserNotFoundException("User based passed data not exist.");
        }
        return findingUser.get();
    }

    //------------------------------------------------------------------------------------------------------------------

    public RefreshTokenModel generateRefreshTokenModel(LocalUserModel userModel) {
        final Pair<String, Date> refreshTokenParams = tokenCreator.createUserRefreshToken(userModel);
        return RefreshTokenModel.builder()
                .refreshToken(refreshTokenParams.getValue0())
                .expiredAt(refreshTokenParams.getValue1())
                .localUser(userModel)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    void sendEmailMessageForActivateAccount(LocalUserModel userModel) {
        final String token = tokenCreator.createActivateAccountToken(userModel);

        // TODO: Send email with verification link
    }
}
