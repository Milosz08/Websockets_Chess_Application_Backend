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

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;

import java.util.Optional;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.token.JsonWebToken;
import pl.miloszgilga.chessappbackend.mail.MailOutService;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.exception.custom.EmailException.*;

import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.EmailNewsletterReqDto;
import pl.miloszgilga.chessappbackend.network.newsletter_email.domain.NewsletterEmailModel;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.AttemptToUnsubscribeReqDto;
import pl.miloszgilga.chessappbackend.network.newsletter_email.domain.INewsletterEmailRepository;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.UnsubscribeNewsletterViaOtaReqDto;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.UnsubscribeNewsletterViaJwtReqDto;

import static pl.miloszgilga.chessappbackend.token.JwtClaim.*;

//----------------------------------------------------------------------------------------------------------------------

@Service
class NewsletterEmailService implements INewsletterEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterEmailService.class);

    private final JsonWebToken jsonWebToken;
    private final INewsletterEmailRepository repository;

    private final MailOutService mailService;
    private final StringManipulator manipulator;
    private final UnsubscribeOtaTokenService unsubscribeService;

    NewsletterEmailService(
            INewsletterEmailRepository repository, UnsubscribeOtaTokenService unsubscribeService,
            JsonWebToken jsonWebToken, MailOutService mailService, StringManipulator manipulator
    ) {
        this.repository = repository;
        this.jsonWebToken = jsonWebToken;
        this.unsubscribeService = unsubscribeService;
        this.mailService = mailService;
        this.manipulator = manipulator;
    }

    @Override
    @Transactional
    public SimpleServerMessageDto subscribeNewsletter(final EmailNewsletterReqDto req) {
        final String userName = manipulator.capitalised(req.getUserName());
        final String emailAddress = req.getEmailAddress();
        if (repository.findNewsletterModelsByEmail(emailAddress).isPresent()) {
            LOGGER.error("Attempt to add already exist email: {} to newsletter list", emailAddress);
            throw new EmailAlreadyExistException(EXPECTATION_FAILED, "Email '%s' is already on the newsletter list.",
                    emailAddress);
        }
        final NewsletterEmailModel saved = repository.save(new NewsletterEmailModel(userName, emailAddress));
        LOGGER.info("Added to newsletter: {}", saved);
        return new SimpleServerMessageDto(String.format("Email '%s' was succesfully added to newsletter.", emailAddress));
    }

    @Override
    @Transactional
    public SimpleServerMessageDto attemptToUnsubscribeNewsletter(final AttemptToUnsubscribeReqDto req) {
        final NewsletterEmailModel model = checkIfRemovingEmailExist(req.getEmailAddress());
        final String email = model.getUserEmail();

        final String otaToken = unsubscribeService.generateAndSaveOtaToken(email);
        final String bearerToken = jsonWebToken.createUnsubscribeNewsletterToken(email, otaToken);
        mailService.unsubscribeNewsletter(model.getId(), model.getUserName(), email, bearerToken, otaToken);

        return new SimpleServerMessageDto(String.format("Message has been send to the email '%s'.", email));
    }

    @Override
    @Transactional
    public SimpleServerMessageDto unsubscribeNewsletterViaOta(final UnsubscribeNewsletterViaOtaReqDto token) {
        final String emailAddress = token.getEmailAddress();
        final NewsletterEmailModel model = checkIfRemovingEmailExist(emailAddress);
        unsubscribeService.validateOtaToken(token.getToken(), emailAddress);

        repository.delete(model);
        LOGGER.info("Email address {} was successfully removed from newsletter via OTA form", emailAddress);
        return new SimpleServerMessageDto(String.format("The newsletter subscription service from the email " +
                        "address '%s' has been successfully deleted.", emailAddress));
    }

    @Override
    @Transactional
    public SimpleServerMessageDto unsubscribeNewsletterViaJwt(final UnsubscribeNewsletterViaJwtReqDto token) {
        final DecodedJWT jwtDecoded = jsonWebToken.verifyJsonWebToken(token.getToken());

        final String emailAddress = jwtDecoded.getClaim(EMAIL.getClaimName()).asString();
        final NewsletterEmailModel model = checkIfRemovingEmailExist(emailAddress);

        final boolean isExpired = jwtDecoded.getClaim(IS_EXPIRED.getClaimName()).asBoolean();
        if (isExpired) {
            final String otaToken = jwtDecoded.getClaim(OTA_TOKEN.getClaimName()).asString();
            unsubscribeService.validateOtaToken(otaToken, emailAddress);
        }

        repository.delete(model);
        LOGGER.info("Email address {} was successfully removed from newsletter via email message", emailAddress);
        return new SimpleServerMessageDto(String.format("The newsletter subscription service from the email " +
                "address '%s' has been successfully deleted. You can return to the start page using the button below.",
                emailAddress));
    }

    private NewsletterEmailModel checkIfRemovingEmailExist(String emailAddress) {
        final Optional<NewsletterEmailModel> emailModel = repository.findNewsletterModelsByEmail(emailAddress);
        if (emailModel.isPresent()) return emailModel.get();

        LOGGER.error("Attempt to remove not exsiting email: {} from newsletter list", emailAddress);
        throw new EmailNotFoundException("The email address provided does not subscribe to the newsletter or has " +
                "already been unsubscribed from it.", emailAddress);
    }
}
