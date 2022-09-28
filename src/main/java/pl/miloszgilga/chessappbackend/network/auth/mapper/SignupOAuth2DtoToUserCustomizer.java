/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SignupOAuth2DtoToUserCustomizer.java
 * Last modified: 28/09/2022, 11:18
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

package pl.miloszgilga.chessappbackend.network.auth.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

import org.javatuples.Triplet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.network.auth.AuthServiceHelper;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfo;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfoFactory;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class SignupOAuth2DtoToUserCustomizer extends CustomMapper<OAuth2RegistrationData, LocalUserModel> {

    private final AuthServiceHelper helper;
    private final EnvironmentVars environment;
    private final StringManipulator manipulator;
    private final PasswordEncoder passwordEncoder;

    private final OAuth2UserInfoFactory userInfoFactory;

    public SignupOAuth2DtoToUserCustomizer(AuthServiceHelper helper, EnvironmentVars environment,
                                           StringManipulator manipulator, PasswordEncoder passwordEncoder,
                                           OAuth2UserInfoFactory userInfoFactory) {
        this.helper = helper;
        this.environment = environment;
        this.manipulator = manipulator;
        this.passwordEncoder = passwordEncoder;
        this.userInfoFactory = userInfoFactory;
    }

    @Override
    public void mapAtoB(OAuth2RegistrationData data, LocalUserModel userModel, MappingContext context) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        final Triplet<String, String, String> userExtractedData = extractUserDataFromUsername(userInfo.getUsername());
        userModel.setNickname(userExtractedData.getValue0());
        userModel.setFirstName(userExtractedData.getValue1());
        userModel.setLastName(userExtractedData.getValue2());
        userModel.setEmailAddress(userInfo.getEmailAddress());
        userModel.setPassword(passwordEncoder.encode(environment.getOauth2PasswordReplacer()));
        userModel.setCredentialsSupplier(data.getSupplier());
        userModel.setSupplierUserId(userInfo.getId());
        userModel.setIsActivated(false);
        userModel.setIsBlocked(false);
        userModel.setRoles(helper.findAndGenerateUserRole());
    }

    private Triplet<String, String, String> extractUserDataFromUsername(String username) {
        final String nickname = username.toLowerCase().replaceAll(" ", "");
        final String[] firstWithLast = username.contains(" ") ? username.split(" ") : new String[] { username, null };
        return new Triplet<>(
                manipulator.generateUserDefNickname(nickname),
                manipulator.capitalised(firstWithLast[0]),
                manipulator.capitalised(firstWithLast[1])
        );
    }
}
