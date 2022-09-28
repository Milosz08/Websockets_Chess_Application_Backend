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

import java.util.Date;
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
    private final JsonWebTokenVerificator tokenVerificator;
    private final ILocalUserRepository localUserRepository;

    AuthServiceHelper(StringManipulator manipulator, JsonWebTokenCreator tokenCreator,
                      JsonWebTokenVerificator tokenVerificator, ILocalUserRepository localUserRepository) {
        this.manipulator = manipulator;
        this.tokenCreator = tokenCreator;
        this.tokenVerificator = tokenVerificator;
        this.localUserRepository = localUserRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    LocalUserModel updateOAuth2UserDetails(LocalUserModel existUser, OAuth2UserInfo existUserInfo) {
        final Triplet<String, String, String> namesData = generateNickFirstLastNameFromOAuthName(
                existUserInfo.getUsername());
        existUser.setNickname(namesData.getValue0());
        existUser.setFirstName(namesData.getValue1());
        existUser.setLastName(namesData.getValue2());
        existUser.getLocalUserDetails().setHasPhoto(!existUserInfo.getUserImageUrl().equals(""));
        existUser.getLocalUserDetails().setPhotoEmbedLink(existUserInfo.getUserImageUrl());
        return localUserRepository.save(existUser);
    }

    RefreshTokenModel createRefreshToken(LocalUserModel newUser) {
        final Pair<String, Date> refreshToken = tokenCreator.createUserRefreshToken(newUser);
        final RefreshTokenModel refreshTokenModel = new RefreshTokenModel(
                refreshToken.getValue0(), refreshToken.getValue1());
        refreshTokenModel.setLocalUser(newUser);
        LOGGER.info("Refresh token was created for user: {}. Expired after {}", newUser, refreshToken.getValue1());
        return refreshTokenModel;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    LocalUserModel validateUserAndReturnUserData(String jwtToken) {
        final UserVerficationClaims claims = tokenVerificator.validateUserJwtFilter(jwtToken);
        final Optional<LocalUserModel> findingUser = localUserRepository
                .findUserByEmailAddressNicknameAndId(claims.getEmailAddress(), claims.getNickname(), claims.getId());
        if (findingUser.isEmpty()) {
            LOGGER.error("Unable to load user based bearer token req data. Bearer token: {}", jwtToken);
            throw new AuthException.UserNotFoundException("User based passed data not exist.");
        }
        return findingUser.get();
    }

    void sendEmailMessageForActivateAccount(LocalUserModel userModel) {
        final String token = tokenCreator.createActivateAccountToken(userModel);

        // TODO: Send email with verification link
    }
}
