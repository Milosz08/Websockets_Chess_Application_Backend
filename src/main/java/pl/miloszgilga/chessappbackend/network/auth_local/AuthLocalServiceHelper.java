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

package pl.miloszgilga.chessappbackend.network.auth_local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.security.SecurityHelper;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfo;

import pl.miloszgilga.chessappbackend.network.auth_local.domain.ILocalUserRepository;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.RefreshTokenModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class AuthLocalServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthLocalServiceHelper.class);

    private final StringManipulator manipulator;
    private final SecurityHelper securityHelper;
    private final JsonWebTokenCreator tokenCreator;
    private final ILocalUserRepository localUserRepository;

    AuthLocalServiceHelper(StringManipulator manipulator, SecurityHelper securityHelper, JsonWebTokenCreator tokenCreator,
                           ILocalUserRepository localUserRepository) {
        this.manipulator = manipulator;
        this.securityHelper = securityHelper;
        this.tokenCreator = tokenCreator;
        this.localUserRepository = localUserRepository;
    }

    Triplet<String, String, String> generateNickFirstLastNameFromOAuthName(String username) {
        final String nickname = username.toLowerCase().replaceAll(" ", "");
        final String[] firstNameWithLastName = username.contains(" ")
                ? username.split(" ") : new String[] { username, null };
        return new Triplet<>(
                nickname,
                manipulator.capitalised(firstNameWithLastName[0]),
                manipulator.capitalised(firstNameWithLastName[1])
        );
    }

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

    List<String> generateHashedActivationEmails(LocalUserModel newUser) {
        final List<String> hashedEmails = new ArrayList<>();
        hashedEmails.add(securityHelper.hashingStringValue(newUser.getEmailAddress(), '*'));
        final String secondEmailAddress = newUser.getLocalUserDetails().getSecondEmailAddress();
        if (!secondEmailAddress.equals("")) {
            hashedEmails.add(securityHelper.hashingStringValue(secondEmailAddress, '*'));
        }
        return hashedEmails;
    }

    RefreshTokenModel createRefreshToken(LocalUserModel newUser) {
        final Pair<String, Date> refreshToken = tokenCreator.createUserRefreshToken(newUser);
        final RefreshTokenModel refreshTokenModel = new RefreshTokenModel(
                refreshToken.getValue0(), refreshToken.getValue1());
        refreshTokenModel.setLocalUser(newUser);
        LOGGER.info("Refresh token was created for user: {}. Expired after {}", newUser, refreshToken.getValue1());
        return refreshTokenModel;
    }
}
