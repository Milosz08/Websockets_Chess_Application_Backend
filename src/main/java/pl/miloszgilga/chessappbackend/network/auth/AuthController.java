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

import pl.miloszgilga.lib.jmpsl.security.jwt.JwtServlet;
import pl.miloszgilga.lib.jmpsl.security.user.CurrentUser;
import pl.miloszgilga.lib.jmpsl.oauth2.user.OAuth2UserExtender;
import pl.miloszgilga.lib.jmpsl.security.excluder.MethodSecurityPathExclude;

import pl.miloszgilga.chessappbackend.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(AUTH_ENDPOINT)
class AuthController {

    private final JwtServlet jwtServlet;
    private final LoginService loginService;
    private final SignupService signupService;

    //------------------------------------------------------------------------------------------------------------------

    AuthController(JwtServlet jwtServlet, LoginService loginService, SignupService signupService) {
        this.jwtServlet = jwtServlet;
        this.loginService = loginService;
        this.signupService = signupService;
    }

    //------------------------------------------------------------------------------------------------------------------

    @MethodSecurityPathExclude
    @PostMapping(LOGIN_VIA_LOCAL)
    ResponseEntity<SuccessedLoginResDto> loginViaLocal(@Valid @RequestBody LoginViaLocalReqDto req) {
        return new ResponseEntity<>(loginService.loginViaLocal(req), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(LOGIN_VIA_OAUTH2)
    ResponseEntity<SuccessedLoginResDto> loginViaOAuth2(@CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(loginService.loginViaOAuth2(userId), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @MethodSecurityPathExclude
    @PostMapping(AUTO_LOGIN)
    ResponseEntity<SuccessedLoginResDto> autoLogin(@Valid @RequestBody AutoLoginReqDto req) {
        return new ResponseEntity<>(loginService.autoLogin(req), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(ATTEMPT_ACTIVATE_ACCOUNT)
    ResponseEntity<SuccessedAttemptToFinishSignupResDto> attemptActivateAccount(@CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(signupService.attemptToActivateAccount(userId), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @DeleteMapping(LOGOUT)
    ResponseEntity<Void> logout(@CurrentUser OAuth2UserExtender user) {
        loginService.logout(((LocalUserModel) user.getUserModel()).getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //------------------------------------------------------------------------------------------------------------------

    @MethodSecurityPathExclude
    @PostMapping(REFRESH_TOKEN)
    ResponseEntity<RefreshTokenResDto> refreshToken(HttpServletRequest req) {
        final String token = jwtServlet.extractToken(req);
        return new ResponseEntity<>(loginService.refreshToken(token), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @MethodSecurityPathExclude
    @PostMapping(SIGNUP_VIA_LOCAL)
    ResponseEntity<SuccessedAttemptToFinishSignupResDto> signupViaLocal(@Valid @RequestBody SignupViaLocalReqDto req) {
        return new ResponseEntity<>(signupService.signupViaLocal(req), HttpStatus.CREATED);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(ATTEMPT_FINISH_SIGNUP_VIA_OAUTH2)
    ResponseEntity<SuccessedAttemptToFinishSignupResDto> attemptToFinishSignup(@CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(signupService.attemptToFinishSignup(userId), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(FINISH_SIGNUP_VIA_OAUTH2)
    ResponseEntity<SimpleServerMessageDto> finishSignup(@Valid @RequestBody FinishSignupReqDto req,
                                                        @CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(signupService.finishSignup(req, userId), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @MethodSecurityPathExclude
    @PostMapping(ATTEMPT_FINISH_SIGNUP_RESEND_EMAIL)
    ResponseEntity<SimpleServerMessageDto> resendEmailVerificationLink(@Valid @RequestBody ResendEmailMessageReqDto req) {
        return new ResponseEntity<>(signupService.resendVerificationEmailLink(req), HttpStatus.OK);
    }
}
