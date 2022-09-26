/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AuthLocalController.java
 * Last modified: 10/09/2022, 19:10
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

package pl.miloszgilga.chessappbackend.network.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(AUTH_LOCAL_ENDPOINT)
class AuthController {

    private final AuthService service;

    AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping(LOGIN_VIA_LOCAL)
    ResponseEntity<SuccessedLoginResDto> loginViaLocal(@Valid @RequestBody LoginViaLocalReqDto req) {
        return new ResponseEntity<>(service.loginViaLocal(req), HttpStatus.OK);
    }

    @PostMapping(LOGIN_VIA_OAUTH2)
    ResponseEntity<SuccessedLoginResDto> loginViaOAuth2(@Valid @RequestBody LoginSignupViaOAuth2ReqDto req) {
        return new ResponseEntity<>(service.loginViaOAuth2(req), HttpStatus.OK);
    }

    @PostMapping(SIGNUP_VIA_LOCAL)
    ResponseEntity<SimpleServerMessageDto> signupViaLocal(@Valid @RequestBody SignupViaLocalReqDto req) {
        return new ResponseEntity<>(service.signupViaLocal(req), HttpStatus.CREATED);
    }

    @PostMapping(ATTEMPT_FINISH_SIGNUP_VIA_OAUTH2)
    ResponseEntity<SuccessedAttemptToFinishSignupResDto> attemptToFinishSignup(
            @Valid @RequestBody LoginSignupViaOAuth2ReqDto req) {
        return new ResponseEntity<>(service.attemptToFinishSignup(req), HttpStatus.OK);
    }

    @PostMapping(FINISH_SIGNUP_VIA_OAUTH2)
    ResponseEntity<SimpleServerMessageDto> finishSignup(@Valid @RequestBody FinishSignupReqDto req) {
        return new ResponseEntity<>(service.finishSignup(req), HttpStatus.OK);
    }
}
