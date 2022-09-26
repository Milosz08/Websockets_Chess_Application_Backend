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

package pl.miloszgilga.chessappbackend.network.auth_local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;
import org.javatuples.Pair;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.oauth.AuthUser;
import pl.miloszgilga.chessappbackend.oauth.AuthUserBuilder;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfo;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfoFactory;

import pl.miloszgilga.chessappbackend.network.auth_local.dto.*;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.ILocalUserRepository;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class AuthLocalService implements IAuthLocalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthLocalService.class);

    private final AuthUserBuilder userBuilder;
    private final AuthenticationManager manager;
    private final AuthLocalServiceHelper helper;
    private final JsonWebTokenCreator tokenCreator;
    private final OAuth2UserInfoFactory userInfoFactory;
    private final ILocalUserRepository localUserRepository;
    private final AuthLocalFactoryMapper userFactoryMapper;

    AuthLocalService(AuthUserBuilder userBuilder, AuthenticationManager manager, AuthLocalServiceHelper helper,
                     JsonWebTokenCreator tokenCreator, OAuth2UserInfoFactory userInfoFactory,
                     ILocalUserRepository localUserRepository, AuthLocalFactoryMapper userFactoryMapper) {
        this.userBuilder = userBuilder;
        this.manager = manager;
        this.helper = helper;
        this.tokenCreator = tokenCreator;
        this.userInfoFactory = userInfoFactory;
        this.localUserRepository = localUserRepository;
        this.userFactoryMapper = userFactoryMapper;
    }

    @Override
    @Transactional
    public SuccessedLoginResDto loginViaLocal(LoginViaLocalReqDto req) {
        final var userCredentials = new UsernamePasswordAuthenticationToken(req.getUsernameEmail(), req.getPassword());
        final Authentication authentication = manager.authenticate(userCredentials);
        final AuthUser authUser = (AuthUser) authentication.getPrincipal();
        if (!authUser.getUserModel().getCredentialsSupplier().equals(CredentialsSupplier.LOCAL)) {
            LOGGER.error("Attempt to log in on OAuth2 account provider via local account login form. Req: {}", req);
            throw new AuthException.DifferentAuthenticationProviderException("This account is not managed locally. " +
                    "Try login via google or facebook.");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String jsonWebToken = tokenCreator.createUserCredentialsToken(authentication);
        LOGGER.info("User with id: {} and email: {} was successfuly logged.",
                authUser.getUserModel().getId(), authUser.getUserModel().getEmailAddress());
        return SuccessedLoginResDto.factoryBuilder(new Pair<>(authUser.getUserModel(), jsonWebToken));
    }

    @Override
    @Transactional
    public SuccessedLoginResDto loginViaOAuth2(LoginSignupViaOAuth2ReqDto req) {
        final Pair<LocalUserModel, String> validateUser = helper.validateUserAndReturnTokenWithUserData(req.getJwtToken());
        return SuccessedLoginResDto.factoryBuilder(validateUser);
    }

    @Override
    @Transactional
    public SimpleServerMessageDto signupViaLocal(SignupViaLocalReqDto req) {
        final LocalUserModel localUserModel = userFactoryMapper.mappedSignupLocalUserDtoToUserEntity(req);
        localUserModel.setRefreshToken(helper.createRefreshToken(localUserModel));
        final LocalUserModel newUser = localUserRepository.save(localUserModel);
        helper.sendEmailMessageForActivateAccount(newUser);
        LOGGER.info("Create new user in database via LOCAL interface. User data: {}", newUser);
        return new SimpleServerMessageDto(
                "Your account was successfuly created. To complete, activate your account using the link sent " +
                        "to your email address."
        );
    }

    @Override
    @Transactional
    public SuccessedAttemptToFinishSignupResDto attemptToFinishSignup(LoginSignupViaOAuth2ReqDto req) {
        final Pair<LocalUserModel, String> validateUser = helper.validateUserAndReturnTokenWithUserData(req.getJwtToken());
        if (validateUser.getValue0().isActivated()) {
            LOGGER.warn("Attempt to re-activate account. Account data: {}", validateUser.getValue0());
            throw new AuthException.AccountIsAlreadyActivatedException("Your account has been already activated.");
        }
        return SuccessedAttemptToFinishSignupResDto.factoryBuilder(validateUser);
    }

    @Override
    public SuccessedFinishSignupResDto finishSignup(FinishSignupReqDto req) {
        return null;
    }

    @Override
    @Transactional
    public AuthUser registrationProcessingFactory(OAuth2RegistrationData data) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        final String supplierName = data.getSupplier().getSupplier();
        if (userInfo.getUsername().equals("") || userInfo.getEmailAddress().equals("")) {
            throw new AuthException.OAuth2CredentialsSupplierMalformedException(
                    "Unable to authenticate via %s provider. Select other authentication method.", supplierName
            );
        }
        final Optional<LocalUserModel> user = localUserRepository.findUserByEmailAddress(userInfo.getEmailAddress());
        if (user.isEmpty()) {
            final LocalUserModel newUser = userFactoryMapper.mappedSignupOAuth2UserDtoToUserEntity(data);
            newUser.setRefreshToken(helper.createRefreshToken(newUser));
            localUserRepository.save(newUser);
            LOGGER.info("Create new user in database via {} OAuth2 interface. User data: {}", supplierName , newUser);
            return userBuilder.buildUser(newUser, data.getAttributes(), data.getIdToken(), data.getUserInfo());
        }

        final LocalUserModel userModel = user.get();
        final CredentialsSupplier supplier = userModel.getCredentialsSupplier();
        if (!supplier.equals(data.getSupplier()) || supplier.equals(CredentialsSupplier.LOCAL)) {
            LOGGER.error("Attempt to create already existing user account via OAuth2. Email: {}", userModel.getEmailAddress());
            throw new AuthException.AccountAlreadyExistException("Account with this email is already registered.");
        }

        final LocalUserModel updatedUser = helper.updateOAuth2UserDetails(userModel, userInfo);
        LOGGER.info("Update existing user in database via {} OAuth2 interface. User data: {}", supplierName, updatedUser);
        return userBuilder.buildUser(updatedUser, data.getAttributes(), data.getIdToken(), data.getUserInfo());
    }
}
