/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SignupUserFactoryMapper.java
 * Last modified: 22/09/2022, 23:45
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

package pl.miloszgilga.chessappbackend.network.auth_local.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.javatuples.Triplet;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.security.LocalUserRole;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.utils.UserGenderSpecific;
import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfo;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfoFactory;

import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth_local.dto.SignupViaLocalReqDto;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserRoleModel;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserDetailsModel;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.ILocalUserRoleRepository;

import static pl.miloszgilga.chessappbackend.utils.UserGenderSpecific.findGenderByString;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class SignupUserFactoryMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignupUserFactoryMapper.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private final EnvironmentVars environment;
    private final StringManipulator manipulator;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2UserInfoFactory userInfoFactory;
    private final ILocalUserRoleRepository roleRepository;

    public SignupUserFactoryMapper(EnvironmentVars environment, StringManipulator manipulator,
                                   @Lazy PasswordEncoder passwordEncoder, OAuth2UserInfoFactory userInfoFactory,
                                   ILocalUserRoleRepository roleRepository) {
        this.environment = environment;
        this.manipulator = manipulator;
        this.passwordEncoder = passwordEncoder;
        this.userInfoFactory = userInfoFactory;
        this.roleRepository = roleRepository;
    }

    public LocalUserModel mappedLocalUserDtoToUserEntity(SignupViaLocalReqDto dto) throws ParseException {
        final String secondEmailAddress = dto.getSecondEmailAddress().equals("") ? null : dto.getSecondEmailAddress();
        final Date date = DATE_FORMAT.parse(dto.getBirthDate());
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

    public LocalUserModel mappedOAuth2UserDtoToUserEntity(OAuth2RegistrationData data) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        final Boolean ifUserHasImage = !userInfo.getUserImageUrl().isEmpty();
        final String userImageUri = ifUserHasImage ? userInfo.getUserImageUrl() : null;
        final Triplet<String, String, String> namesData = generateNickFirstLastNameFromOAuthName(userInfo.getUsername());
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

    public Triplet<String, String, String> generateNickFirstLastNameFromOAuthName(String username) {
        final String nickname = username.toLowerCase().replaceAll(" ", "");
        final String[] firstNameWithLastName = username.contains(" ")
                ? username.split(" ") : new String[] { username, null };
        return new Triplet<>(
                nickname,
                manipulator.capitalised(firstNameWithLastName[0]),
                manipulator.capitalised(firstNameWithLastName[1])
        );
    }
}
