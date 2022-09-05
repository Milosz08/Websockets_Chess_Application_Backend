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

import java.util.Optional;

import pl.miloszgilga.chessappbackend.token.JsonWebToken;
import pl.miloszgilga.chessappbackend.mail.MailOutService;
import pl.miloszgilga.chessappbackend.dao.SimpleServerMessage;
import pl.miloszgilga.chessappbackend.exception.custom.EmailException.*;

import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.EmailNewsletterReq;
import pl.miloszgilga.chessappbackend.network.newsletter_email.domain.NewsletterEmailModel;
import pl.miloszgilga.chessappbackend.network.newsletter_email.domain.INewsletterEmailRepository;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.UnsubscribeNewsletterViaOtaReq;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.UnsubscribeNewsletterViaJwtReq;

//----------------------------------------------------------------------------------------------------------------------

@Service
class NewsletterEmailService implements INewsletterEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterEmailService.class);

    private final JsonWebToken jsonWebToken;
    private final INewsletterEmailRepository repository;

    private final MailOutService mailService;
    private final UnsubscribeOtaTokenService unsubscribeService;

    NewsletterEmailService(
            INewsletterEmailRepository repository, UnsubscribeOtaTokenService unsubscribeService,
            JsonWebToken jsonWebToken, MailOutService mailService
    ) {
        this.repository = repository;
        this.jsonWebToken = jsonWebToken;
        this.unsubscribeService = unsubscribeService;
        this.mailService = mailService;
    }

    @Override
    public SimpleServerMessage subscribeNewsletter(final EmailNewsletterReq email) {
        final String emailAddress = email.getEmailAddress();
        if (checkIfEmailExist(emailAddress)) {
            LOGGER.error("Attempt to add already exist email: {} to newsletter list", emailAddress);
            throw new EmailAlreadyExistException(EXPECTATION_FAILED, "Email '%s' is already on the newsletter list.",
                    emailAddress);
        }
        NewsletterEmailModel saved = repository.save(new NewsletterEmailModel(emailAddress));
        LOGGER.info("Added to newsletter: {}", saved);
        return new SimpleServerMessage(String.format("Email '%s' was succesfully added to newsletter.", emailAddress));
    }

    @Override
    public SimpleServerMessage attemptToUnsubscribeNewsletter(final EmailNewsletterReq email) {
        final String emailAddress = email.getEmailAddress();
        if (!checkIfEmailExist(emailAddress)) {
            LOGGER.error("Attempt to remove not exsiting email: {} from newsletter list", emailAddress);
            throw new EmailNotFoundException("Email '%s' is not subscribing newsletter.", emailAddress);
        }

        String otaToken = unsubscribeService.generateAndSaveOtaToken(emailAddress);
        String bearerToken = jsonWebToken.createUnsubscribeNewsletterToken(emailAddress, otaToken);
        mailService.unsubscribeNewsletter(emailAddress, bearerToken, otaToken);

        return new SimpleServerMessage(String.format("Message has been send to the email '%s'.", emailAddress));
    }

    @Override
    public SimpleServerMessage unsubscribeNewsletterViaOta(final UnsubscribeNewsletterViaOtaReq token) {
        // via ota
        return new SimpleServerMessage("unsubscribe");
    }

    @Override
    public SimpleServerMessage unsubscribeNewsletterViaJwt(final UnsubscribeNewsletterViaJwtReq token) {
        // via jwt
        return new SimpleServerMessage("unsubscribe");
    }

    private boolean checkIfEmailExist(String email) {
        Optional<NewsletterEmailModel> findEmail = repository.findNewsletterModelsByEmail(email);
        return findEmail.isPresent();
    }
}