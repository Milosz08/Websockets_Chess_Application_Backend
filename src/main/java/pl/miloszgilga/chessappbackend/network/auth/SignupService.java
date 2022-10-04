/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AuthLocalService.java
 * Last modified: 11/09/2022, 01:38
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

import ma.glasnost.orika.MapperFacade;

import org.springframework.stereotype.Service;

import java.util.Optional;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.oauth.*;
import pl.miloszgilga.chessappbackend.oauth.user_info.*;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;

import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;

import static pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier.LOCAL;
import static pl.miloszgilga.chessappbackend.token.OtaTokenType.ACTIVATE_ACCOUNT;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class SignupService implements ISignupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SignupService.class);

    private final AuthServiceHelper helper;
    private final MapperFacade mapperFacade;
    private final AuthUserBuilder userBuilder;
    private final StringManipulator manipulator;
    private final JsonWebTokenCreator tokenCreator;
    private final OAuth2UserInfoFactory userInfoFactory;
    private final ILocalUserRepository localUserRepository;

    //------------------------------------------------------------------------------------------------------------------

    SignupService(AuthUserBuilder userBuilder, MapperFacade mapperFacade, AuthServiceHelper helper,
                  StringManipulator manipulator, JsonWebTokenCreator tokenCreator, OAuth2UserInfoFactory userInfoFactory,
                  ILocalUserRepository localUserRepository) {
        this.helper = helper;
        this.userBuilder = userBuilder;
        this.mapperFacade = mapperFacade;
        this.manipulator = manipulator;
        this.tokenCreator = tokenCreator;
        this.userInfoFactory = userInfoFactory;
        this.localUserRepository = localUserRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SuccessedAttemptToFinishSignupResDto signupViaLocal(SignupViaLocalReqDto req) {
        final LocalUserModel userModel = mapperFacade.map(req, LocalUserModel.class);
        final LocalUserDetailsModel userDetailsModel = mapperFacade.map(req, LocalUserDetailsModel.class);

        userModel.setLocalUserDetails(userDetailsModel);
        userDetailsModel.setLocalUser(userModel);
        helper.addUserToNewsletter(userModel, req.getHasNewsletterAccept());
        localUserRepository.save(userModel);
        helper.sendEmailMessageForActivateAccount(userModel, ACTIVATE_ACCOUNT);

        LOGGER.info("Create new user in database via LOCAL interface. User data: {}", userModel);
        final SuccessedAttemptToFinishSignupResDto resDto = SuccessedAttemptToFinishSignupResDto.builder()
                .authSupplier(LOCAL.getName())
                .isDataFilled(true)
                .responseMessage("Your account was successfuly created, but not activated.")
                .build();
        mapperFacade.map(userModel, resDto);
        return resDto;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SuccessedAttemptToFinishSignupResDto attemptToFinishSignup(LoginSignupViaOAuth2ReqDto req, Long userId) {
        final LocalUserModel userModel = helper.findUserAndReturnUserData(userId);
        if (userModel.getIsActivated()) {
            LOGGER.warn("Attempt to re-activate account. Account data: {}", userModel);
            throw new AuthException.AccountIsAlreadyActivatedException("Your account has been already activated.");
        }

        if (!userModel.getIsActivated() && userModel.getLocalUserDetails().getIsDataFilled()) {
            helper.sendEmailMessageForActivateAccount(userModel, ACTIVATE_ACCOUNT);
        }
        final SuccessedAttemptToFinishSignupResDto resDto = SuccessedAttemptToFinishSignupResDto.builder()
                .authSupplier(userModel.getCredentialsSupplier().getName())
                .responseMessage("Your account has already filled with additional data, but not activated.")
                .isDataFilled(userModel.getLocalUserDetails().getIsDataFilled())
                .build();
        mapperFacade.map(userModel, resDto);
        return resDto;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto finishSignup(FinishSignupReqDto req, Long userId) {
        final LocalUserModel validateUser = helper.findUserAndReturnUserData(userId);
        mapperFacade.map(req, validateUser.getLocalUserDetails());
        validateUser.getLocalUserDetails().setIsDataFilled(true);
        helper.addUserToNewsletter(validateUser, req.getNewsletterAccept());
        localUserRepository.save(validateUser);
        helper.sendEmailMessageForActivateAccount(validateUser, ACTIVATE_ACCOUNT);
        LOGGER.info("Update new user data in database via OAUTH2 interface. User data: {}", req);
        return new SimpleServerMessageDto("Your account details information has been successfully updated.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public AuthUser registrationProcessingFactory(OAuth2RegistrationData data) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        final String supplierName = data.getSupplier().getName();
        if (userInfo.getUsername().equals("") || userInfo.getEmailAddress().equals("")) {
            throw new AuthException.OAuth2CredentialsSupplierMalformedException(
                    "Unable to authenticate via %s provider. Select other authentication method.", supplierName);
        }
        final Optional<LocalUserModel> user = localUserRepository.findUserByEmailAddress(userInfo.getEmailAddress());
        if (user.isEmpty()) return registerNewUserViaOAuth2(data, supplierName);
        return updateAlreadyExistUserViaOAuth2(data, user.get());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    AuthUser registerNewUserViaOAuth2(OAuth2RegistrationData data, String supplierName) {
        final LocalUserModel userModel = mapperFacade.map(data, LocalUserModel.class);
        final LocalUserDetailsModel userDetailsModel = mapperFacade.map(data, LocalUserDetailsModel.class);
        userDetailsModel.setLocalUser(userModel);
        userModel.setLocalUserDetails(userDetailsModel);
        localUserRepository.save(userModel);
        LOGGER.info("Create new user via {} OAuth2 provider. User data: {}", supplierName, userModel);
        return userBuilder.build(userModel, data);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    AuthUser updateAlreadyExistUserViaOAuth2(OAuth2RegistrationData data, LocalUserModel foundUser) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        final CredentialsSupplier supplier = foundUser.getCredentialsSupplier();
        final LocalUserDetailsModel userDetails = foundUser.getLocalUserDetails();
        if (!supplier.equals(data.getSupplier()) || supplier.equals(LOCAL)) {
            LOGGER.error("Attempt to create already existing user via OAuth2. Email: {}", foundUser.getEmailAddress());
            throw new AuthException.AccountAlreadyExistException("Account with email %s is already registered.",
                    foundUser.getEmailAddress());
        }

        foundUser.setFirstName(manipulator.extractUserDataFromUsername(userInfo.getUsername()).getValue0());
        foundUser.setLastName(manipulator.extractUserDataFromUsername(userInfo.getUsername()).getValue1());
        userDetails.setHasPhoto(!userInfo.getUserImageUrl().isEmpty());
        userDetails.setPhotoEmbedLink(userInfo.getUserImageUrl().isEmpty() ? null : userInfo.getUserImageUrl());

        localUserRepository.save(foundUser);
        LOGGER.info("Update user via {} OAuth2 provider. User data: {}", data.getSupplier().getName(), foundUser);
        return userBuilder.build(foundUser, data);
    }
}
