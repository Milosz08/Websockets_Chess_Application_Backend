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

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

import pl.miloszgilga.chessappbackend.oauth.AuthUser;
import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.utils.NetworkHelper;
import pl.miloszgilga.chessappbackend.security.CurrentUser;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(AUTH_LOCAL_ENDPOINT)
class AuthController {

    private final LoginService loginService;
    private final NetworkHelper networkHelper;
    private final SignupService signupService;

    //------------------------------------------------------------------------------------------------------------------

    AuthController(LoginService loginService, NetworkHelper networkHelper, SignupService signupService) {
        this.loginService = loginService;
        this.networkHelper = networkHelper;
        this.signupService = signupService;
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(LOGIN_VIA_LOCAL)
    ResponseEntity<SuccessedLoginResDto> loginViaLocal(@Valid @RequestBody LoginViaLocalReqDto req) {
        return new ResponseEntity<>(loginService.loginViaLocal(req), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(LOGIN_VIA_OAUTH2)
    ResponseEntity<SuccessedLoginResDto> loginViaOAuth2(@Valid @RequestBody LoginSignupViaOAuth2ReqDto req,
                                                        @CurrentUser AuthUser user) {
        return new ResponseEntity<>(loginService.loginViaOAuth2(req, user.getUserModel().getId()), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(LOGOUT)
    ResponseEntity<Void> logout(@CurrentUser AuthUser user) {
        loginService.logout(user.getUserModel().getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(REFRESH_TOKEN)
    ResponseEntity<RefreshTokenResDto> refreshToken(HttpServletRequest req) {
        final String token = networkHelper.extractJwtTokenFromRequest(req);
        return new ResponseEntity<>(loginService.refreshToken(token), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(SIGNUP_VIA_LOCAL)
    ResponseEntity<SuccessedAttemptToFinishSignupResDto> signupViaLocal(@Valid @RequestBody SignupViaLocalReqDto req) {
        return new ResponseEntity<>(signupService.signupViaLocal(req), HttpStatus.CREATED);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(ATTEMPT_FINISH_SIGNUP_VIA_OAUTH2)
    ResponseEntity<SuccessedAttemptToFinishSignupResDto> attemptToFinishSignup(
            @Valid @RequestBody LoginSignupViaOAuth2ReqDto req, @CurrentUser AuthUser user
    ) {
        return new ResponseEntity<>(signupService.attemptToFinishSignup(req, user.getUserModel().getId()), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(FINISH_SIGNUP_VIA_OAUTH2)
    ResponseEntity<SimpleServerMessageDto> finishSignup(@Valid @RequestBody FinishSignupReqDto req,
                                                        @CurrentUser AuthUser user) {
        return new ResponseEntity<>(signupService.finishSignup(req, user.getUserModel().getId()), HttpStatus.OK);
    }
}
