/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: NewsletterService.java
 * Last modified: 31/08/2022, 15:21
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

import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;

import java.net.URI;
import java.util.Date;
import javax.transaction.Transactional;

import pl.miloszgilga.lib.jmpsl.util.StringUtil;
import pl.miloszgilga.lib.jmpsl.util.ServletPathUtil;

import pl.miloszgilga.chessappbackend.dto.*;
import pl.miloszgilga.chessappbackend.token.*;
import pl.miloszgilga.chessappbackend.exception.custom.*;
import pl.miloszgilga.chessappbackend.mail.IMailOutService;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.token.dto.ActivateServiceViaEmailTokenClaims;

import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.*;
import pl.miloszgilga.chessappbackend.network.newsletter_email.domain.*;

//----------------------------------------------------------------------------------------------------------------------

@Service
class NewsletterEmailService implements INewsletterEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterEmailService.class);

    private final JsonWebTokenCreator creator;
    private final IMailOutService mailService;
    private final EnvironmentVars environment;
    private final NewsletterEmailServiceHelper helper;
    private final JsonWebTokenVerificator verificator;
    private final INewsletterEmailRepository repository;
    private final IUnsubscribeOtaTokenRepository otaTokenRepository;

    //------------------------------------------------------------------------------------------------------------------

    NewsletterEmailService(INewsletterEmailRepository repository, JsonWebTokenCreator creator,
                           JsonWebTokenVerificator verificator, IMailOutService mailService, EnvironmentVars environment,
                           NewsletterEmailServiceHelper helper, IUnsubscribeOtaTokenRepository otaTokenRepository) {
        this.helper = helper;
        this.creator = creator;
        this.repository = repository;
        this.verificator = verificator;
        this.mailService = mailService;
        this.environment = environment;
        this.otaTokenRepository = otaTokenRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto subscribeNewsletter(final EmailNewsletterReqDto req) {
        final String userName = StringUtil.capitalize(req.getUserName());
        final String emailAddress = req.getEmailAddress();
        if (repository.findNewsletterModelsByEmail(emailAddress).isPresent()) {
            LOGGER.error("Attempt to add already exist email: {} to newsletter list", emailAddress);
            throw new EmailException.EmailAlreadyExistException(EXPECTATION_FAILED,
                    "Email '%s' is already on the newsletter list.", emailAddress);
        }
        final NewsletterEmailModel saved = repository.save(new NewsletterEmailModel(userName, emailAddress));
        LOGGER.info("Added to newsletter: {}", saved);
        return new SimpleServerMessageDto(String.format("Email %s was succesfully added to newsletter.", emailAddress));
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto attemptToUnsubscribeNewsletter(final AttemptToUnsubscribeReqDto req) {
        final NewsletterEmailModel model = helper.checkIfRemovingEmailExist(req.getEmailAddress());
        final String email = model.getUserEmail();

        final String otaToken = helper.generateAndSaveOtaToken(email);
        final String bearerToken = creator.createAcitivateServiceViaEmailToken(email, model.getId(), otaToken);
        mailService.unsubscribeNewsletter(model.getId(), model.getUserName(), email, bearerToken, otaToken);

        return new SimpleServerMessageDto(String.format("Message has been send to the email %s.", email));
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto unsubscribeNewsletterViaOta(final SimpleOtaTokenReqDto token) {
        final String emailAddress = token.getEmailAddress();
        final NewsletterEmailModel model = helper.checkIfRemovingEmailExist(emailAddress);
        helper.validateOtaToken(token.getToken(), emailAddress);

        helper.unsubscribeNewsletterFromUserDatatable(emailAddress);
        repository.delete(model);
        LOGGER.info("Email address {} was successfully removed from newsletter via OTA form", emailAddress);
        return new SimpleServerMessageDto(String.format("The newsletter subscription service from the email " +
                        "address %s has been successfully deleted.", emailAddress));
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public URI unsubscribeNewsletterViaLink(final String bearer) {
        String queryMessage;
        boolean ifError = false;
        try {
            final ActivateServiceViaEmailTokenClaims claims = verificator.validateActivatingServiceViaEmail(bearer);
            final String emailAddress = claims.getEmailAddress();

            final NewsletterEmailModel model = helper.checkIfRemovingEmailExist(emailAddress);
            if (claims.isExpired()) {
                helper.validateOtaToken(claims.getOtaToken(), emailAddress);
            }
            helper.unsubscribeNewsletterFromUserDatatable(emailAddress);
            repository.delete(model);
            LOGGER.info("Email address {} was successfully removed from newsletter via email message", emailAddress);
            queryMessage = String.format("The newsletter subscription service from the email address %s " +
                    "has been successfully deleted. You can return to the start page using the button below.", emailAddress);
        } catch (EmailException.EmailNotFoundException ex) {
            queryMessage = ex.getMessage();
            ifError = true;
        }
        return ServletPathUtil.redirectMessageUri(queryMessage, environment.getFrontEndUrl() +
                environment.getUnsubscribeNewsletterUri(), ifError);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto resendVerificationEmailLink(final ResendEmailMessageReqDto req) {
        final UnsubscribeOtaTokenModel token = otaTokenRepository.findTokenBasedEmail(req.getEmailAddress())
                .stream()
                .filter(t -> t.getTokenExpired().after(new Date()))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("Attempt to resend verification email for activate account without OTA token");
                    throw new TokenException.OtaTokenNotExistException("Unable to find token. Please regenerate token.");
                });

        final String email = req.getEmailAddress();
        final NewsletterEmailModel user = token.getNewsletterUser();
        final String bearerToken = creator.createAcitivateServiceViaEmailToken(email, user.getId(), token.getToken());
        mailService.unsubscribeNewsletter(user.getId(), user.getUserName(), email, bearerToken, token.getToken());

        LOGGER.info("Successful resend verification email for unsubscribe newsletter for user: {}", user);
        return new SimpleServerMessageDto("Successful resend verification email message for unsubscribe newsletter. " +
                "Check your mailbox account.");
    }
}
