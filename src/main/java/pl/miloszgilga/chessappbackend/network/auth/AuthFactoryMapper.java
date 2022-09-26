/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SignupUserFactoryMapper.java
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

import org.javatuples.Triplet;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

import java.util.*;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.security.LocalUserRole;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.utils.UserGenderSpecific;
import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfo;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfoFactory;

import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth.dto.FinishSignupReqDto;
import pl.miloszgilga.chessappbackend.network.auth.dto.SignupViaLocalReqDto;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserRoleModel;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserDetailsModel;
import pl.miloszgilga.chessappbackend.network.auth.domain.ILocalUserRoleRepository;

import static pl.miloszgilga.chessappbackend.utils.UserGenderSpecific.findGenderByString;

//----------------------------------------------------------------------------------------------------------------------

@Component
class AuthFactoryMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthFactoryMapper.class);

    private final TimeHelper timeHelper;
    private final EnvironmentVars environment;
    private final StringManipulator manipulator;
    private final PasswordEncoder passwordEncoder;
    private final ILocalUserRoleRepository roleRepository;
    private final OAuth2UserInfoFactory userInfoFactory;
    private final AuthServiceHelper authServiceHelper;

    AuthFactoryMapper(TimeHelper timeHelper, EnvironmentVars environment, StringManipulator manipulator,
                      @Lazy PasswordEncoder passwordEncoder, OAuth2UserInfoFactory userInfoFactory,
                      ILocalUserRoleRepository roleRepository, AuthServiceHelper authServiceHelper) {
        this.timeHelper = timeHelper;
        this.environment = environment;
        this.manipulator = manipulator;
        this.passwordEncoder = passwordEncoder;
        this.userInfoFactory = userInfoFactory;
        this.roleRepository = roleRepository;
        this.authServiceHelper = authServiceHelper;
    }

    LocalUserModel mappedSignupLocalUserDtoToUserEntity(SignupViaLocalReqDto dto) {
        final String secondEmailAddress = dto.getSecondEmailAddress().equals("") ? null : dto.getSecondEmailAddress();
        final Date date = timeHelper.convertStringDateToDateObject(dto.getBirthDate());
        final UserGenderSpecific genderSpecific = findGenderByString(dto.getGender());
        final LocalUserDetailsModel userDetailsModel = new LocalUserDetailsModel(
                secondEmailAddress, date, dto.getCountryName(), genderSpecific, false, null, dto.getNewsletterAccept()
        );
        final LocalUserModel userModel = new LocalUserModel(
                dto.getNickname(), manipulator.capitalised(dto.getFirstName()), manipulator.capitalised(dto.getLastName()),
                dto.getEmailAddress(), passwordEncoder.encode(dto.getPassword()), CredentialsSupplier.LOCAL,
                null, false, false
        );
        userDetailsModel.setLocalUser(userModel);
        userModel.setLocalUserDetails(userDetailsModel);
        userModel.setRoles(generateUserRoles());
        return userModel;
    }

    LocalUserModel mappedSignupOAuth2UserDtoToUserEntity(OAuth2RegistrationData data) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        final Boolean ifUserHasImage = !userInfo.getUserImageUrl().isEmpty();
        final String userImageUri = ifUserHasImage ? userInfo.getUserImageUrl() : null;
        final Triplet<String, String, String> namesData = authServiceHelper
                .generateNickFirstLastNameFromOAuthName(userInfo.getUsername());
        final LocalUserDetailsModel userDetailsModel = new LocalUserDetailsModel(ifUserHasImage, userImageUri, false);
        final LocalUserModel userModel = new LocalUserModel(
                namesData.getValue0(), namesData.getValue1(), namesData.getValue2(), userInfo.getEmailAddress(),
                passwordEncoder.encode(environment.getOauth2PasswordReplacer()), data.getSupplier(), userInfo.getId(),
                false, false
        );
        userDetailsModel.setLocalUser(userModel);
        userModel.setLocalUserDetails(userDetailsModel);
        userModel.setRoles(generateUserRoles());
        return userModel;
    }

    LocalUserModel mappedFinishSignupReqDtoToUserEntity(LocalUserModel userModel, FinishSignupReqDto dto) {
        final LocalUserDetailsModel userDetailsModel = userModel.getLocalUserDetails();
        userDetailsModel.setBirthDate(timeHelper.convertStringDateToDateObject(dto.getBirthDate()));
        userDetailsModel.setCountry(dto.getCountryName());
        userDetailsModel.setGender(UserGenderSpecific.findGenderByString(dto.getGender()));
        userDetailsModel.setHasNewsletterAccept(dto.getNewsletterAccept());
        return userModel;
    }

    @Transactional
    Set<LocalUserRoleModel> generateUserRoles() {
        final Set<LocalUserRoleModel> roles = new HashSet<>();
        final Optional<LocalUserRoleModel> foundRole = roleRepository.findRoleByType(LocalUserRole.USER);
        if (foundRole.isEmpty()) {
            LOGGER.error("Role for user is not present in database.");
            throw new AuthException.RoleNotFoundException("Unable to signup new account. Try again later.");
        }
        roles.add(foundRole.get());
        return roles;
    }
}
