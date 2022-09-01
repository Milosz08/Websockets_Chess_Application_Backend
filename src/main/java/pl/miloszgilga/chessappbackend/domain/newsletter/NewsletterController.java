/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: NewsletterController.java
 * Last modified: 31/08/2022, 15:05
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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import pl.miloszgilga.chessappbackend.dao.SimpleServerMessage;
import pl.miloszgilga.chessappbackend.domain.newsletter.dto.EmailNewsletterReq;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.NEWSLETTER_MAIN_ENDPOINT;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(NEWSLETTER_MAIN_ENDPOINT)
class NewsletterController {

    private final NewsletterService service;

    NewsletterController(NewsletterService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<SimpleServerMessage> addEmailToNewsletter(@RequestBody @Valid EmailNewsletterReq emailValue) {
        return new ResponseEntity<>(service.addNewEmailToNewsletter(emailValue), HttpStatus.CREATED);
    }
}