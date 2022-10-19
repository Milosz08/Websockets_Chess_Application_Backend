/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: RenewCredentialsService.java
 * Last modified: 02/10/2022, 21:14
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

package pl.miloszgilga.chessappbackend.network.renew_credentials;

import ma.glasnost.orika.MapperFacade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import javax.transaction.Transactional;

import static pl.miloszgilga.lib.jmpsl.oauth2.OAuth2Supplier.LOCAL;

import pl.miloszgilga.chessappbackend.token.*;
import pl.miloszgilga.chessappbackend.exception.custom.*;
import pl.miloszgilga.chessappbackend.mail.IMailOutService;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;
import pl.miloszgilga.chessappbackend.dto.ResendEmailMessageReqDto;
import pl.miloszgilga.chessappbackend.token.dto.ActivateServiceViaEmailTokenClaims;

import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.network.ota_token.domain.*;
import pl.miloszgilga.chessappbackend.network.renew_credentials.dto.*;

import static pl.miloszgilga.chessappbackend.token.OtaTokenType.RESET_PASSWORD;

//----------------------------------------------------------------------------------------------------------------------

@Service
class RenewCredentialsService implements IRenewCredentialsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenewCredentialsService.class);

    private final MapperFacade mapperFacade;
    private final IMailOutService mailOutService;
    private final PasswordEncoder passwordEncoder;
    private final JsonWebTokenCreator tokenCreator;
    private final RenewCredentialsServiceHelper helper;
    private final OtaTokenUserService otaTokenUserService;
    private final IOtaTokenRepository otaTokenRepository;
    private final JsonWebTokenVerificator tokenVerificator;
    private final ILocalUserRepository localUserRepository;

    //------------------------------------------------------------------------------------------------------------------

    RenewCredentialsService(MapperFacade mapperFacade, IMailOutService mailOutService, PasswordEncoder passwordEncoder,
                            JsonWebTokenCreator tokenCreator, RenewCredentialsServiceHelper helper,
                            OtaTokenUserService otaTokenUserService, IOtaTokenRepository otaTokenRepository,
                            JsonWebTokenVerificator tokenVerificator, ILocalUserRepository localUserRepository) {
        this.helper = helper;
        this.mapperFacade = mapperFacade;
        this.tokenCreator = tokenCreator;
        this.mailOutService = mailOutService;
        this.passwordEncoder = passwordEncoder;
        this.otaTokenUserService = otaTokenUserService;
        this.otaTokenRepository = otaTokenRepository;
        this.tokenVerificator = tokenVerificator;
        this.localUserRepository = localUserRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public ChangePasswordEmaiAddressesResDto attemptToChangePassword(final AttemptToChangePasswordReqDto req) {
        final String nickEmail = req.getUsernameEmailAddress();
        final LocalUserModel user = localUserRepository.findUserByNickOrEmail(nickEmail).orElseThrow(() -> {
            LOGGER.error("Attempt to change password for not existing user. Nickname or email: {}", nickEmail);
            throw new AuthException.UserNotFoundException("User with passed nickname/email does not exist.");
        });
        if (!user.getOAuth2Supplier().equals(LOCAL)) {
            LOGGER.warn("Attempt to change password in account which managed via external {} service. Account: {}",
                    user.getOAuth2Supplier().getSupplierName(), user);
            throw new AuthException.ChangePasswordProhibitedActionException(
                    "Changing password in accounts managed via external service is not supported.");
        }

        final String otaToken = otaTokenUserService.generateAndSaveUserOtaToken(RESET_PASSWORD, user);
        for (String email : helper.extractUserEmails(user)) {
            final String token = tokenCreator.createAcitivateServiceViaEmailToken(user, otaToken);
            mailOutService.changePassword(user.getId(), email, user, token, otaToken);
        }

        LOGGER.info("Successful send request to reset password for user: {}", user);
        return ChangePasswordEmaiAddressesResDto.builder()
                .primaryEmailAddress(user.getEmailAddress())
                .responseMessage("A message verifying your account was sent to your mailbox. Click on " +
                        "the link or rewrite the code in the form below to continue.")
                .emailAddresses(helper.hashUserEmails(user))
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public ChangePasswordUserDetailsResDto checkJwtBeforeChangePassword(final String jwtToken) {
        final ActivateServiceViaEmailTokenClaims claims = tokenVerificator.validateActivatingServiceViaEmail(jwtToken);
        final LocalUserModel user = helper.findUserByJwtToken(jwtToken);

        otaTokenRepository
                .findTokenBasedValueAndUsed(claims.getOtaToken(), RESET_PASSWORD)
                .ifPresent(otaTokenModel -> {
                    otaTokenModel.setAlreadyUsed(true);
                    otaTokenRepository.save(otaTokenModel);
                });

        return mapperFacade.map(user, ChangePasswordUserDetailsResDto.class);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto changeForgottenPassword(final ChangeForgottenPasswordReqDto req, final String jwtToken) {
        final LocalUserModel user = helper.findUserByJwtToken(jwtToken);
        if (!req.getPassword().equals(req.getPasswordRepeat())) {
            LOGGER.error("Attempt to change forgotten password with two different passwords. User: {}", user);
            throw new PasswordException.PasswordAndRepeatPassowordNotTheSameException(
                    "Password and repeat password must be the same.");
        }
        if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            LOGGER.error("attempt to change the password to a password already in use. User: {}", user);
            throw new PasswordException.PasswordHasAlreadyUsedException("This password is already used. Please " +
                    "type different password.");
        }
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        localUserRepository.save(user);
        LOGGER.info("Successful change forgotten password for user {}", user);
        return new SimpleServerMessageDto("Your password has been successfully changed. You can proceed to login " +
                "using the button below.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto resendVerificationEmailLink(final ResendEmailMessageReqDto req) {
        final OtaTokenModel token = otaTokenRepository.findTokenByUserEmail(RESET_PASSWORD, req.getEmailAddress())
                .stream()
                .filter(t -> t.getExpirationDate().after(new Date()))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("Attempt to resend verification email for reset password without OTA token");
                    throw new TokenException.OtaTokenNotExistException("Unable to find token. Please regenerate token.");
                });

        final LocalUserModel user = token.getLocalUser();
        final String bearerToken = tokenCreator.createAcitivateServiceViaEmailToken(user, token.getOtaToken());
        mailOutService.changePassword(user.getId(), req.getEmailAddress(), user, bearerToken, token.getOtaToken());

        LOGGER.info("Successful resend verification email for change password for user: {}", user);
        return new SimpleServerMessageDto("Successful resend verification email message for change password. " +
                "Check your mailbox account.");
    }
}
