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

import java.net.URI;
import javax.validation.Valid;

import pl.miloszgilga.chessappbackend.dto.*;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.*;
import pl.miloszgilga.lib.jmpsl.security.excluder.ControllerSecurityPathExclude;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;
import static pl.miloszgilga.chessappbackend.config.RedirectEndpoints.NEWSLETTER_UNSUBSCRIBE_VIA_LINK;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@ControllerSecurityPathExclude
@RequestMapping(NEWSLETTER_EMAIL_ENDPOINT)
class NewsletterEmailController {

    private final NewsletterEmailService service;

    //------------------------------------------------------------------------------------------------------------------

    NewsletterEmailController(NewsletterEmailService service) {
        this.service = service;
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(NEWSLETTER_SUBSCRIBE)
    ResponseEntity<SimpleServerMessageDto> subscribeNewsletter(@Valid @RequestBody EmailNewsletterReqDto req) {
        return new ResponseEntity<>(service.subscribeNewsletter(req), HttpStatus.CREATED);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(NEWSLETTER_ATTEMPT_UNSUBSCRIBE)
    ResponseEntity<SimpleServerMessageDto> attemptToUnsubscribeNewsletter(@Valid @RequestBody AttemptToUnsubscribeReqDto req) {
        return new ResponseEntity<>(service.attemptToUnsubscribeNewsletter(req), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @DeleteMapping(NEWSLETTER_UNSUBSCRIBE_VIA_OTA)
    ResponseEntity<SimpleServerMessageDto> unsubscribeNewsletterViaOta(@Valid @RequestBody SimpleOtaTokenReqDto req) {
        return new ResponseEntity<>(service.unsubscribeNewsletterViaOta(req), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(NEWSLETTER_UNSUBSCRIBE_VIA_LINK + "{bearer}")
    ResponseEntity<Void> unsubscribeNewsletterViaJwt(@PathVariable String bearer) {
        final URI redirectUri = service.unsubscribeNewsletterViaLink(bearer);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(NEWSLETTER_UNSUBSCRIBE_RESEND_EMAIL)
    ResponseEntity<SimpleServerMessageDto> resendVerificationEmailLink(@Valid @RequestBody ResendEmailMessageReqDto req) {
        return new ResponseEntity<>(service.resendVerificationEmailLink(req), HttpStatus.OK);
    }
}
