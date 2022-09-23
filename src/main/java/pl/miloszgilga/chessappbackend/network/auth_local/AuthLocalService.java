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

import org.javatuples.Pair;
import org.javatuples.Triplet;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.transaction.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.text.ParseException;

import pl.miloszgilga.chessappbackend.oauth.AuthUser;
import pl.miloszgilga.chessappbackend.oauth.AuthUserBuilder;
import pl.miloszgilga.chessappbackend.security.SecurityHelper;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfo;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;
import pl.miloszgilga.chessappbackend.oauth.user_info.OAuth2UserInfoFactory;

import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth_local.dto.LoginViaLocalReqDto;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.RefreshTokenModel;
import pl.miloszgilga.chessappbackend.network.auth_local.dto.SignupViaLocalReqDto;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.ILocalUserRepository;
import pl.miloszgilga.chessappbackend.network.auth_local.mapper.SignupUserFactoryMapper;
import pl.miloszgilga.chessappbackend.network.auth_local.dto.SuccessedLoginViaLocalResDto;
import pl.miloszgilga.chessappbackend.network.auth_local.dto.SuccessedSignupViaLocalResDto;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class AuthLocalService implements IAuthLocalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthLocalService.class);

    private final AuthUserBuilder userBuilder;
    private final AuthenticationManager manager;
    private final SecurityHelper securityHelper;
    private final JsonWebTokenCreator tokenCreator;
    private final OAuth2UserInfoFactory userInfoFactory;
    private final ILocalUserRepository localUserRepository;
    private final SignupUserFactoryMapper userFactoryMapper;

    AuthLocalService(AuthUserBuilder userBuilder, AuthenticationManager manager, SecurityHelper securityHelper,
                     JsonWebTokenCreator tokenCreator, OAuth2UserInfoFactory userInfoFactory,
                     ILocalUserRepository localUserRepository, SignupUserFactoryMapper userFactoryMapper) {
        this.userBuilder = userBuilder;
        this.manager = manager;
        this.securityHelper = securityHelper;
        this.tokenCreator = tokenCreator;
        this.userInfoFactory = userInfoFactory;
        this.localUserRepository = localUserRepository;
        this.userFactoryMapper = userFactoryMapper;
    }

    @Override
    @Transactional
    public SuccessedLoginViaLocalResDto loginViaLocal(LoginViaLocalReqDto req) {
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
        return new SuccessedLoginViaLocalResDto(
                authUser.getUserModel().getFirstName() + " " + authUser.getUserModel().getLastName(),
                jsonWebToken, authUser.getUserModel().getRefreshToken().getRefreshToken()
        );
    }

    @Override
    @Transactional
    public SuccessedSignupViaLocalResDto signupViaLocal(SignupViaLocalReqDto req) {
        try {
            final LocalUserModel localUserModel = userFactoryMapper.mappedLocalUserDtoToUserEntity(req);
            localUserModel.setRefreshToken(createRefreshToken(localUserModel));
            final LocalUserModel newUser = localUserRepository.save(localUserModel);
            LOGGER.info("Create new user in database via LOCAL interface. User data: {}", newUser);
            final String fullName = newUser.getFirstName() + " " + newUser.getLastName();

            // TODO: Send email with verification code

            return new SuccessedSignupViaLocalResDto(
                    fullName, String.format("Account for user %s was successfuly created.", fullName),
                    generateHashedActivationEmails(newUser)
            );
        } catch (ParseException ex) {
            LOGGER.error("Unable to parsed data via java date formatter. Passed date: {}", req.getBirthDate());
            throw new AuthException.MalformedBirthDateException("Passed birth date value is not valid.");
        }
    }

    @Override
    @Transactional
    public AuthUser registrationProcessingFactory(OAuth2RegistrationData data) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        final String supplierName = data.getSupplier().getSupplier();
        if (userInfo.getUsername().equals("") || userInfo.getEmailAddress().equals("")) {
            throw new AuthException.OAuth2CredentialsSupplierMalformedException(String.format(
                    "Unable to authenticate via %s provider. Select other authentication method.", supplierName
            ));
        }
        final Optional<LocalUserModel> user = localUserRepository.findUserByEmailAddress(userInfo.getEmailAddress());
        if (user.isEmpty()) {
            final LocalUserModel newUser = userFactoryMapper.mappedOAuth2UserDtoToUserEntity(data);
            newUser.setRefreshToken(createRefreshToken(newUser));
            localUserRepository.save(newUser);
            LOGGER.info("Create new user in database via {} OAuth2 interface. User data: {}", supplierName , newUser);
            return userBuilder.buildUser(newUser, data.getAttributes(), data.getIdToken(), data.getUserInfo());
        }

        final LocalUserModel userModel = user.get();
        if (userModel.getCredentialsSupplier().equals(CredentialsSupplier.LOCAL)) {
            LOGGER.error("Attempt to create already existing user account via OAuth2. Email: {}", userModel.getEmailAddress());
            throw new AuthException.AccountAlreadyExistException("Account with this email is already registered.");
        }

        final LocalUserModel updatedUser = updateOAuth2UserDetails(userModel, userInfo);
        LOGGER.info("Update existing user in database via {} OAuth2 interface. User data: {}", supplierName, updatedUser);
        return userBuilder.buildUser(updatedUser, data.getAttributes(), data.getIdToken(), data.getUserInfo());
    }

    private LocalUserModel updateOAuth2UserDetails(LocalUserModel existUser, OAuth2UserInfo existUserInfo) {
        final Triplet<String, String, String> namesData = userFactoryMapper
                .generateNickFirstLastNameFromOAuthName(existUserInfo.getUsername());
        existUser.setNickname(namesData.getValue0());
        existUser.setFirstName(namesData.getValue1());
        existUser.setLastName(namesData.getValue2());
        existUser.getLocalUserDetails().setHasPhoto(!existUserInfo.getUserImageUrl().equals(""));
        existUser.getLocalUserDetails().setPhotoEmbedLink(existUserInfo.getUserImageUrl());
        return localUserRepository.save(existUser);
    }

    private List<String> generateHashedActivationEmails(LocalUserModel newUser) {
        final List<String> hashedEmails = new ArrayList<>();
        hashedEmails.add(securityHelper.hashingStringValue(newUser.getEmailAddress(), '*'));
        final String secondEmailAddress = newUser.getLocalUserDetails().getSecondEmailAddress();
        if (!secondEmailAddress.equals("")) {
            hashedEmails.add(securityHelper.hashingStringValue(secondEmailAddress, '*'));
        }
        return hashedEmails;
    }

    private RefreshTokenModel createRefreshToken(LocalUserModel newUser) {
        final Pair<String, Date> refreshToken = tokenCreator.createUserRefreshToken(newUser);
        final RefreshTokenModel refreshTokenModel = new RefreshTokenModel(
                refreshToken.getValue0(), refreshToken.getValue1());
        refreshTokenModel.setLocalUser(newUser);
        LOGGER.info("Refresh token was created for user: {}. Expired after {}", newUser, refreshToken.getValue1());
        return refreshTokenModel;
    }
}
