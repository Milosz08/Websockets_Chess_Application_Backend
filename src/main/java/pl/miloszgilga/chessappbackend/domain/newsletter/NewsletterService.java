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

package pl.miloszgilga.chessappbackend.domain.newsletter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;

import java.util.Optional;

import pl.miloszgilga.chessappbackend.dao.SimpleServerMessage;
import pl.miloszgilga.chessappbackend.domain.newsletter.dto.EmailNewsletterReq;
import pl.miloszgilga.chessappbackend.exception.custom.EmailAlreadyExistException;

//----------------------------------------------------------------------------------------------------------------------

@Service
class NewsletterService implements INewsletterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterService.class);
    private final INewsletterRepository repository;

    NewsletterService(INewsletterRepository repository) {
        this.repository = repository;
    }

    @Override
    public SimpleServerMessage addNewEmailToNewsletter(final EmailNewsletterReq emailAddress) {
        Optional<NewsletterModel> findEmail = repository.findNewsletterModelsByEmail(emailAddress.getEmailAddress());
        if (findEmail.isPresent()) {
            LOGGER.error("Attempt to add already exist email: {} to newsletter list", emailAddress.getEmailAddress());
            throw new EmailAlreadyExistException(EXPECTATION_FAILED, "Email '%s' is already on the newsletter list.",
                    emailAddress.getEmailAddress());
        }
        NewsletterModel saved = repository.save(new NewsletterModel(emailAddress.getEmailAddress()));
        LOGGER.info("Added to newsletter: {}", saved);
        return new SimpleServerMessage(String.format(
                "Email '%s' was succesfully added to newsletter.", emailAddress.getEmailAddress())
        );
    }
}