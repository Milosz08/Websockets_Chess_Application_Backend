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

package pl.miloszgilga.chessappbackend.network.newsletter_email;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import pl.miloszgilga.chessappbackend.dao.SimpleServerMessage;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.EmailNewsletterReq;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.UnsubscribeNewsletterViaJwtReq;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.UnsubscribeNewsletterViaOtaReq;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(NEWSLETTER_EMAIL_ENDPOINT)
class NewsletterEmailController {

    private final NewsletterEmailService service;

    NewsletterEmailController(NewsletterEmailService service) {
        this.service = service;
    }

    @PostMapping(NEWSLETTER_SUBSCRIBE)
    ResponseEntity<SimpleServerMessage> subscribeNewsletter(@RequestBody @Valid EmailNewsletterReq email) {
        return new ResponseEntity<>(service.subscribeNewsletter(email), HttpStatus.CREATED);
    }

    @PostMapping(NEWSLETTER_ATTEMPT_UNSUBSCRIBE)
    ResponseEntity<SimpleServerMessage> attemptToUnsubscribeNewsletter(@RequestBody @Valid EmailNewsletterReq email) {
        return new ResponseEntity<>(service.attemptToUnsubscribeNewsletter(email), HttpStatus.OK);
    }

    @DeleteMapping(NEWSLETTER_UNSUBSCRIBE_VIA_OTA)
    ResponseEntity<SimpleServerMessage> unsubscribeNewsletterViaOta(@RequestBody @Valid UnsubscribeNewsletterViaOtaReq token) {
        return new ResponseEntity<>(service.unsubscribeNewsletterViaOta(token), HttpStatus.OK);
    }

    @DeleteMapping(NEWSLETTER_UNSUBSCRIBE_VIA_JWT)
    ResponseEntity<SimpleServerMessage> unsubscribeNewsletterViaJwt(@RequestBody @Valid UnsubscribeNewsletterViaJwtReq token) {
        return new ResponseEntity<>(service.unsubscribeNewsletterViaJwt(token), HttpStatus.OK);
    }
}
