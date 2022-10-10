/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: NewsletterEmailServiceHelper.java
 * Last modified: 02/10/2022, 21:01
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

package pl.miloszgilga.chessappbackend.network.newsletter_email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.exception.custom.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.token.OneTimeAccessToken;
import pl.miloszgilga.chessappbackend.network.newsletter_email.domain.*;

//----------------------------------------------------------------------------------------------------------------------

@Component
class NewsletterEmailServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterEmailServiceHelper.class);

    private final TimeHelper timeHelper;
    private final EnvironmentVars environment;
    private final OneTimeAccessToken otaService;
    private final ILocalUserDetailsRepository detailsRepository;
    private final INewsletterEmailRepository newsletterRepository;
    private final IUnsubscribeOtaTokenRepository otaTokenRepository;

    //------------------------------------------------------------------------------------------------------------------

    public NewsletterEmailServiceHelper(TimeHelper timeHelper, EnvironmentVars environment, OneTimeAccessToken otaService,
                                        ILocalUserDetailsRepository detailsRepository, INewsletterEmailRepository newsletterRepository,
                                        IUnsubscribeOtaTokenRepository otaTokenRepository) {
        this.timeHelper = timeHelper;
        this.environment = environment;
        this.otaService = otaService;
        this.detailsRepository = detailsRepository;
        this.newsletterRepository = newsletterRepository;
        this.otaTokenRepository = otaTokenRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    NewsletterEmailModel checkIfRemovingEmailExist(String emailAddress) {
        final Optional<NewsletterEmailModel> emailModel = newsletterRepository.findNewsletterModelsByEmail(emailAddress);
        if (emailModel.isPresent()) return emailModel.get();

        LOGGER.error("Attempt to remove not exsiting email: {} from newsletter list", emailAddress);
        throw new EmailException.EmailNotFoundException("The email address provided does not subscribe to the" +
                "newsletter or has already been unsubscribed from it.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    void unsubscribeNewsletterFromUserDatatable(final String email) {
       detailsRepository.findDetailsByUserEmail(email).ifPresent(userDetails -> {
           userDetails.setHasNewsletterAccept(false);
           detailsRepository.save(userDetails);
       });
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public String generateAndSaveOtaToken(String email) {
        String token;
        do {
            token = otaService.generateToken();
        } while (otaTokenRepository.findExistingToken(token));

        final Date tokenExpired = timeHelper.addMinutesToCurrentDate(environment.getOtaTokenExpiredMinutes());
        final var otaToken = new UnsubscribeOtaTokenModel(email, token, tokenExpired);
        otaTokenRepository.save(otaToken);

        LOGGER.info("Successfully generated OTA ({} min exp) token for unsubscribe newsletter: {}",
                environment.getOtaTokenExpiredMinutes(), otaToken);
        return token;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public void validateOtaToken(String token, String email) {
        final Optional<UnsubscribeOtaTokenModel> probablyValidToken = otaTokenRepository.checkIfTokenExist(token, email);
        if (probablyValidToken.isEmpty() || !otaService.checkIsTokenIsValid(probablyValidToken.get().getToken())) {
            LOGGER.error("OTA token from JWT is not the same as from DB. Token: {}", token);
            throw new TokenException.JwtMalformedTokenException(
                    "Data could not be verified due to an incorrect, already used or corrupted token.");
        }

        final UnsubscribeOtaTokenModel validToken = probablyValidToken.get();
        if (validToken.getTokenExpired().before(new Date())) {
            LOGGER.error("OTA token is expired. Token: {}", token);
            throw new TokenException.OtaTokenExpiredException("Passed token is valid, but it has already expired.");
        }

        validToken.setAlreadyUsed(true);
        otaTokenRepository.save(validToken);
    }
}
