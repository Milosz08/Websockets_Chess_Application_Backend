/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OtaTokenService.java
 * Last modified: 26/09/2022, 23:07
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

package pl.miloszgilga.chessappbackend.network.ota_token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.exception.custom.*;
import pl.miloszgilga.chessappbackend.token.OtaTokenType;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;

import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.network.ota_token.dto.*;
import pl.miloszgilga.chessappbackend.network.ota_token.domain.*;

import static pl.miloszgilga.chessappbackend.token.OtaTokenType.*;

//----------------------------------------------------------------------------------------------------------------------

@Service
class OtaTokenService implements IOtaTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtaTokenService.class);

    private final EnvironmentVars environment;
    private final OtaTokenServiceHelper helper;
    private final ILocalUserRepository userRepository;
    private final IOtaTokenRepository otaTokenRepository;

    //------------------------------------------------------------------------------------------------------------------

    OtaTokenService(EnvironmentVars environment, OtaTokenServiceHelper helper, ILocalUserRepository userRepository,
                    IOtaTokenRepository otaTokenRepository) {
        this.environment = environment;
        this.helper = helper;
        this.userRepository = userRepository;
        this.otaTokenRepository = otaTokenRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public SimpleServerMessageDto changePassword(final OtaTokenMultipleEmailsReqDto req) {

        // TODO: Implement checking password OTA token from email

        return new SimpleServerMessageDto("change password via ota token service");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public URI changePasswordViaLink(final String bearer) {
        final TokenLinkValidationData data = TokenLinkValidationData.builder()
                .bearer(bearer)
                .type(RESET_PASSWORD)
                .redirectUri(environment.getChangePasswordRedirectUri())
                .successMessage("Your password is successfuly changed. Now you can login via pressing the login button below.")
                .failureMessage("Unable to change password via passed token. Token probably is malformed.")
                .build();
        return helper.checkBearerTokenFromLinkWithOtaToken(data);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto activateAccount(final OtaTokenMultipleEmailsReqDto req) {
        final OtaTokenModel tokenModel = findTokenBasedEmailsAndTokenAndCompare(req, ACTIVATE_ACCOUNT);
        final LocalUserModel localUserModel = tokenModel.getLocalUser();
        if (localUserModel.getIsActivated()) {
            LOGGER.warn("Attempt to re-activate account. Account activation data: {}", req);
            throw new AuthException.AccountIsAlreadyActivatedException("Your account has been already activated.");
        }
        if (verifyOtaTokenDetails(tokenModel)) {
            localUserModel.setIsActivated(true);
            userRepository.save(localUserModel);
        }
        LOGGER.info("Successfuly activated user account via OTA token form. Activation data: {}", req);
        return new SimpleServerMessageDto("Your account is successful activated. Now you can login via pressing " +
                "the login button below.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public URI activateAccountViaLink(final String bearer) {
        final TokenLinkValidationData data = TokenLinkValidationData.builder()
                .bearer(bearer)
                .type(ACTIVATE_ACCOUNT)
                .redirectUri(environment.getActivateAccountRedirectUri())
                .successMessage("Your account is successful activated. Now you can login via pressing the login button below.")
                .failureMessage("Unable to activate account with passed token. Token probaby is malformed.")
                .build();
        return helper.checkBearerTokenFromLinkWithOtaToken(data);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    OtaTokenModel findTokenBasedEmailsAndTokenAndCompare(final OtaTokenMultipleEmailsReqDto req, final OtaTokenType tokenType) {
        return req.getEmailAddresses().stream().map(e -> otaTokenRepository
            .findTokenByUserEmailOrSecondEmailAddress(e, tokenType, req.getToken())
            .orElseThrow(() -> {
                LOGGER.error("Attempt to activate account with non existing token. Emails: {}", req.getEmailAddresses());
                throw new AuthException.UserNotFoundException("Unable to find OTA token. Please try again.");
            }))
            .collect(Collectors.toList())
            .get(0);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    boolean verifyOtaTokenDetails(final OtaTokenModel tokenModel) {
        if (tokenModel.getExpirationDate().before(new Date())) {
            LOGGER.error("OTA token is expired. Token: {}", tokenModel);
            throw new TokenException.OtaTokenExpiredException("Passed token is valid, but it has already expired.");
        }
        if (tokenModel.getAlreadyUsed()) {
            LOGGER.error("OTA token is already used. Token: {}", tokenModel);
            throw new TokenException.OtaTokenExpiredException("OTA tokens reusage is strictly prohibited.");
        }
        tokenModel.setAlreadyUsed(true);
        return true;
    }
}
