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

import org.slf4j.*;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import javax.transaction.Transactional;

import pl.miloszgilga.lib.jmpsl.security.jwt.JwtServlet;
import pl.miloszgilga.lib.jmpsl.oauth2.user.OAuth2UserExtender;
import static pl.miloszgilga.lib.jmpsl.oauth2.OAuth2Supplier.LOCAL;

import pl.miloszgilga.chessappbackend.token.*;
import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.exception.custom.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;

import static pl.miloszgilga.chessappbackend.token.JwtClaim.USER_ID;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class LoginService implements ILoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    private final JwtServlet jwtServlet;
    private final AuthServiceHelper helper;
    private final MapperFacade mapperFacade;
    private final AuthenticationManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JsonWebTokenCreator tokenCreator;
    private final ILocalUserRepository localUserRepository;
    private final IRefreshTokenRepository refreshTokenRepository;

    //------------------------------------------------------------------------------------------------------------------

    public LoginService(JwtServlet jwtServlet, AuthServiceHelper helper, MapperFacade mapperFacade, AuthenticationManager manager,
                        PasswordEncoder passwordEncoder, JsonWebTokenCreator tokenCreator,
                        ILocalUserRepository localUserRepository, IRefreshTokenRepository refreshTokenRepository) {
        this.jwtServlet = jwtServlet;
        this.helper = helper;
        this.mapperFacade = mapperFacade;
        this.manager = manager;
        this.passwordEncoder = passwordEncoder;
        this.tokenCreator = tokenCreator;
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
                    .isActivated(false)
                    .build();
        }
        final var userCredentials = new UsernamePasswordAuthenticationToken(req.getUsernameEmail(), req.getPassword());
        final Authentication authentication = manager.authenticate(userCredentials);
        final OAuth2UserExtender authUser = (OAuth2UserExtender) authentication.getPrincipal();
        final LocalUserModel userModel = (LocalUserModel) authUser.getUserModel();
        final SuccessedLoginResDto res = mapperFacade.map(userModel, SuccessedLoginResDto.class);
        if (!userModel.getOAuth2Supplier().equals(LOCAL)) {
            LOGGER.error("Attempt to log in on OAuth2 account provider via local account login form. Req: {}", req);
            throw new AuthException.DifferentAuthenticationProviderException("This account is not managed locally. " +
                    "Try login via outside supplier.");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LOGGER.info("User with email: {} successfuly logged via {} credentials supplier.", userModel.getEmailAddress(),
                userModel.getOAuth2Supplier());
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
    public SuccessedLoginResDto autoLogin(final AutoLoginReqDto req) {
        final LocalUserModel userModel = refreshTokenRepository
                .findUserByMatchedRefreshToken(req.getRefreshToken())
                .orElseThrow(() -> {
                    LOGGER.error("Attempt to auto login user with no initialized session before. Bearer: {}", req);
                    throw new AuthException.SessionIsNotStartedException("Unable to auto login. Please login again.");
                });
        LOGGER.info("User with email: {} was logged automatically. User data: {}", userModel.getEmailAddress(), userModel);
        return mapperFacade.map(userModel, SuccessedLoginResDto.class);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public void logout(final Long userId) {
        final Optional<RefreshTokenModel> refreshTokenModel = refreshTokenRepository.findRefreshTokenByUserIdAndNotNullable(userId);
        refreshTokenModel.ifPresent(token -> {
            token.setRefreshToken(null);
            refreshTokenRepository.save(token);
        });
        SecurityContextHolder.getContext().setAuthentication(null);
        LOGGER.info("User with id {} was successfully logout.", userId);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public RefreshTokenResDto refreshToken(final String token) {
        final Long userId = jwtServlet.validateRefreshToken(token, USER_ID.getClaimName()).orElseThrow(() -> {
            LOGGER.error("Attempt to extract refresh token with malformed expired bearer token. Token {}", token);
            throw new TokenException.JwtMalformedTokenException("Expired jwt token is malformed. Try again with another.");
        });
        final RefreshTokenModel refreshToken = refreshTokenRepository.findRefreshTokenByUserIdAndNotNullable(userId).orElseThrow(() -> {
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
