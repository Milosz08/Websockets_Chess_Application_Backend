/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: RenewCredentialsController.java
 * Last modified: 10/09/2022, 19:13
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

package pl.miloszgilga.chessappbackend.network.renew_credentials;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

import pl.miloszgilga.chessappbackend.dto.*;
import pl.miloszgilga.chessappbackend.utils.NetworkHelper;
import pl.miloszgilga.chessappbackend.network.renew_credentials.dto.*;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(RENEW_CREDETIALS_LOCAL)
class RenewCredentialsController {

    private final NetworkHelper networkHelper;
    private final IRenewCredentialsService service;

    //------------------------------------------------------------------------------------------------------------------

    RenewCredentialsController(NetworkHelper networkHelper, IRenewCredentialsService service) {
        this.networkHelper = networkHelper;
        this.service = service;
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(ATTEMPT_TO_CHANGE_PASSWORD)
    ResponseEntity<ChangePasswordEmaiAddressesResDto> attemptToChangePassword(@RequestBody @Valid AttemptToChangePasswordReqDto req) {
        return new ResponseEntity<>(service.attemptToChangePassword(req), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(CHANGE_PASSWORD_CHECK_JWT)
    ResponseEntity<ChangePasswordUserDetailsResDto> checkJwtBeforeChangePassword(HttpServletRequest req) {
        final String jwtToken = networkHelper.extractJwtTokenFromRequest(req);
        return new ResponseEntity<>(service.checkJwtBeforeChangePassword(jwtToken), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(CHANGE_FORGOTTEN_PASSWORD)
    ResponseEntity<SimpleServerMessageDto> changeForgottenPassword(@RequestBody @Valid ChangeForgottenPasswordReqDto reqDto,
                                                                   HttpServletRequest req) {
        final String jwtToken = networkHelper.extractJwtTokenFromRequest(req);
        return new ResponseEntity<>(service.changeForgottenPassword(reqDto, jwtToken), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(CHANGE_PASSWORD_RESEND_EMAIL)
    ResponseEntity<SimpleServerMessageDto> resendEmailVerificationLink(@RequestBody @Valid ResendEmailMessageReqDto req) {
        return new ResponseEntity<>(service.resendVerificationEmailLink(req), HttpStatus.OK);
    }
}
