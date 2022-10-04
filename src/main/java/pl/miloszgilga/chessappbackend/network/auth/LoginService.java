/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LoginService.java
 * Last modified: 03/10/2022, 13:00
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

import org.javatuples.Pair;
import ma.glasnost.orika.MapperFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.token.*;
import pl.miloszgilga.chessappbackend.oauth.AuthUser;
import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.exception.custom.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;

import static pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier.LOCAL;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class LoginService implements ILoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private final AuthServiceHelper helper;
    private final MapperFacade mapperFacade;
    private final AuthenticationManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JsonWebTokenCreator tokenCreator;
    private final JsonWebTokenVerificator tokenVerificator;
    private final ILocalUserRepository localUserRepository;
    private final IRefreshTokenRepository refreshTokenRepository;

    //------------------------------------------------------------------------------------------------------------------

    public LoginService(AuthServiceHelper helper, MapperFacade mapperFacade, AuthenticationManager manager,
                        PasswordEncoder passwordEncoder, JsonWebTokenCreator tokenCreator,
                        JsonWebTokenVerificator tokenVerificator, ILocalUserRepository localUserRepository,
                        IRefreshTokenRepository refreshTokenRepository) {
        this.helper = helper;
        this.mapperFacade = mapperFacade;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.tokenCreator = tokenCreator;
        this.tokenVerificator = tokenVerificator;
        this.localUserRepository = localUserRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SuccessedLoginResDto loginViaLocal(final LoginViaLocalReqDto req) {
        final Optional<LocalUserModel> userOrNull = localUserRepository.findUserByNickOrEmail(req.getUsernameEmail());
        if (userOrNull.isEmpty() || !passwordEncoder.matches(req.getPassword(), userOrNull.get().getPassword())) {
            LOGGER.error("Attemt to login on local account with non existing user data. Login data: {}", req);
            throw new AuthException.UserNotFoundException("User with this credentials does not exist.");
        }
        final LocalUserModel user = userOrNull.get();
        if (!user.getIsActivated()) {
            LOGGER.info("User with email: {} attempt to login on not activated account.", user.getEmailAddress());
            return SuccessedLoginResDto.builder()
                    .jwtToken(tokenCreator.createUserCredentialsToken(user))
                    .build();
        }
        final var userCredentials = new UsernamePasswordAuthenticationToken(req.getUsernameEmail(), req.getPassword());
        final Authentication authentication = manager.authenticate(userCredentials);
        final AuthUser authUser = (AuthUser) authentication.getPrincipal();
        final LocalUserModel userModel = authUser.getUserModel();
        final SuccessedLoginResDto res = mapperFacade.map(userModel, SuccessedLoginResDto.class);
        if (!userModel.getCredentialsSupplier().equals(LOCAL)) {
            LOGGER.error("Attempt to log in on OAuth2 account provider via local account login form. Req: {}", req);
            throw new AuthException.DifferentAuthenticationProviderException("This account is not managed locally. " +
                    "Try login via outside supplier.");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LOGGER.info("User with email: {} successfuly logged via {} credentials supplier.", userModel.getEmailAddress(),
                userModel.getCredentialsSupplier());
        return res;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SuccessedLoginResDto loginViaOAuth2(final Long userId) {
        final LocalUserModel userModel = helper.findUserAndReturnUserData(userId);
        LOGGER.info("User with email: {} successfuly logged via local credentials supplier.", userModel.getEmailAddress());
        return mapperFacade.map(userModel, SuccessedLoginResDto.class);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public void logout(final Long userId) {
        final Optional<RefreshTokenModel> refreshTokenModel = refreshTokenRepository.findRefreshTokenByUserId(userId);
        refreshTokenModel.ifPresent(refreshTokenRepository::delete);
        SecurityContextHolder.getContext().setAuthentication(null);
        LOGGER.info("User with id {} was successfully logout.", userId);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public RefreshTokenResDto refreshToken(final String expiredBearer) {
        final Long userId = tokenVerificator.validateExpiredTokenToRefreshToken(expiredBearer);
        final RefreshTokenModel refreshToken = refreshTokenRepository.findRefreshTokenByUserId(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to extract refresh token from non existing token. User id {}", userId);
            throw new TokenException.RefreshTokenNotExistException("Unable to find refresh token for provided user.");
        });
        String finalRefreshToken = refreshToken.getRefreshToken();
        if (refreshToken.getExpiredAt().after(new Date())) {
            final Pair<String, Date> regeneratedToken = tokenCreator.createUserRefreshToken(refreshToken.getLocalUser());
            finalRefreshToken = regeneratedToken.getValue0();
            refreshToken.setRefreshToken(regeneratedToken.getValue0());
            refreshToken.setExpiredAt(regeneratedToken.getValue1());
            refreshTokenRepository.save(refreshToken);
        }
        return RefreshTokenResDto.builder()
                .token(tokenCreator.createUserCredentialsToken(refreshToken.getLocalUser()))
                .refreshToken(finalRefreshToken)
                .build();
    }
}
