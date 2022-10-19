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

import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.dto.*;
import pl.miloszgilga.chessappbackend.oauth.*;
import pl.miloszgilga.chessappbackend.oauth.user_info.*;
import pl.miloszgilga.chessappbackend.exception.custom.*;
import pl.miloszgilga.chessappbackend.mail.IMailOutService;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;

import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.network.ota_token.domain.*;

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
    private final IMailOutService mailOutService;
    private final JsonWebTokenCreator tokenCreator;
    private final OAuth2UserInfoFactory userInfoFactory;
    private final IOtaTokenRepository otaTokenRepository;
    private final ILocalUserRepository localUserRepository;

    //------------------------------------------------------------------------------------------------------------------

    SignupService(AuthUserBuilder userBuilder, MapperFacade mapperFacade, AuthServiceHelper helper,
                  StringManipulator manipulator, IMailOutService mailOutService, JsonWebTokenCreator tokenCreator,
                  OAuth2UserInfoFactory userInfoFactory, IOtaTokenRepository otaTokenRepository,
                  ILocalUserRepository localUserRepository) {
        this.helper = helper;
        this.userBuilder = userBuilder;
        this.mapperFacade = mapperFacade;
        this.manipulator = manipulator;
        this.mailOutService = mailOutService;
        this.tokenCreator = tokenCreator;
        this.userInfoFactory = userInfoFactory;
        this.otaTokenRepository = otaTokenRepository;
        this.localUserRepository = localUserRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SuccessedAttemptToFinishSignupResDto signupViaLocal(final SignupViaLocalReqDto req) {
        final LocalUserModel userModel = mapperFacade.map(req, LocalUserModel.class);
        final LocalUserDetailsModel userDetailsModel = mapperFacade.map(req, LocalUserDetailsModel.class);

        userModel.setLocalUserDetails(userDetailsModel);
        userDetailsModel.setLocalUser(userModel);
        helper.addUserToNewsletter(userModel, req.getHasNewsletterAccept());
        localUserRepository.save(userModel);
        helper.sendEmailMessageForActivateAccount(userModel, ACTIVATE_ACCOUNT);

        LOGGER.info("Create new user in database via LOCAL interface. User data: {}", userModel);
        final SuccessedAttemptToFinishSignupResDto resDto = SuccessedAttemptToFinishSignupResDto.builder()
                .authSupplier(LOCAL.getSupplierName())
                .jwtToken(tokenCreator.createUserCredentialsToken(userModel))
                .isDataFilled(true)
                .responseMessage("Your account was successfuly created, but not activated.")
                .build();
        mapperFacade.map(userModel, resDto);
        return resDto;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SuccessedAttemptToFinishSignupResDto attemptToFinishSignup(final Long userId) {
        final LocalUserModel userModel = helper.checkIfAccountIsAlreadyActivated(userId);
        if (!userModel.getIsActivated() && userModel.getLocalUserDetails().getIsDataFilled()) {
            helper.sendEmailMessageForActivateAccount(userModel, ACTIVATE_ACCOUNT);
        }
        final SuccessedAttemptToFinishSignupResDto resDto = SuccessedAttemptToFinishSignupResDto.builder()
                .authSupplier(userModel.getOAuth2Supplier().getSupplierName())
                .responseMessage("Your account has already filled with additional data, but not activated.")
                .isDataFilled(userModel.getLocalUserDetails().getIsDataFilled())
                .build();
        mapperFacade.map(userModel, resDto);
        return resDto;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SuccessedAttemptToFinishSignupResDto attemptToActivateAccount(final Long userId) {
        final LocalUserModel userModel = helper.checkIfAccountIsAlreadyActivated(userId);
        final SuccessedAttemptToFinishSignupResDto resDto = SuccessedAttemptToFinishSignupResDto.builder()
                .jwtToken(tokenCreator.createUserCredentialsToken(userModel))
                .authSupplier(LOCAL.getSupplierName())
                .responseMessage("Before you will be using, please activate your account.")
                .isDataFilled(true)
                .build();
        helper.sendEmailMessageForActivateAccount(userModel, ACTIVATE_ACCOUNT);
        mapperFacade.map(userModel, resDto);
        return resDto;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto finishSignup(final FinishSignupReqDto req, final Long userId) {
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
    public AuthUser registrationProcessingFactory(final OAuth2RegistrationData data) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        final String supplierName = data.getSupplier().getName();
        if (userInfo.getUsername().equals("") || userInfo.getEmailAddress().equals("")) {
            throw new AuthException.OAuth2CredentialsSupplierMalformedException(
                    "Unable to authenticate via %s provider. Select other authentication method.", supplierName);
        }
        final Optional<LocalUserModel> user = localUserRepository.findUserByEmailAddress(userInfo.getEmailAddress());
        if (user.isEmpty()) {
            return registerNewUserViaOAuth2(data, supplierName);
        }
        return updateAlreadyExistUserViaOAuth2(data, user.get());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto resendVerificationEmailLink(final ResendEmailMessageReqDto req) {
        final OtaTokenModel token = otaTokenRepository.findTokenByUserEmail(ACTIVATE_ACCOUNT, req.getEmailAddress())
                .stream()
                .filter(t -> t.getExpirationDate().after(new Date()))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("Attempt to resend verification email for activate account without OTA token");
                    throw new TokenException.OtaTokenNotExistException("Unable to find token. Please regenerate token.");
                });

        final LocalUserModel user = token.getLocalUser();
        final String bearerToken = tokenCreator.createAcitivateServiceViaEmailToken(user, token.getOtaToken());
        mailOutService.activateAccount(user.getId(), req.getEmailAddress(), user, bearerToken, token.getOtaToken());

        LOGGER.info("Successful resend verification email for activate account for user: {}", user);
        return new SimpleServerMessageDto("Successful resend verification email message for activate account. " +
                "Check your mailbox account.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    OAuth2UserExtender registerNewUserViaOAuth2(final OAuth2RegistrationDataDto data, final String supplierName) {
        final LocalUserModel userModel = mapperFacade.map(data, LocalUserModel.class);
        final LocalUserDetailsModel userDetailsModel = mapperFacade.map(data, LocalUserDetailsModel.class);
        userDetailsModel.setLocalUser(userModel);
        userModel.setLocalUserDetails(userDetailsModel);
        localUserRepository.save(userModel);
        LOGGER.info("Create new user via {} OAuth2 provider. User data: {}", supplierName, userModel);
        return OAuth2Util.fabricateUser(userModel, data);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    OAuth2UserExtender updateAlreadyExistUserViaOAuth2(final OAuth2RegistrationDataDto data, final LocalUserModel foundUser) {
        final OAuth2UserInfoBase userInfo = OAuth2UserInfoFactory.getInstance(data.getSupplier(), data.getAttributes());
        final OAuth2Supplier supplier = foundUser.getOAuth2Supplier();
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
        LOGGER.info("Update user via {} OAuth2 provider. User data: {}", data.getSupplier().getSupplierName(), foundUser);
        return OAuth2Util.fabricateUser(foundUser, data);
    }
}
