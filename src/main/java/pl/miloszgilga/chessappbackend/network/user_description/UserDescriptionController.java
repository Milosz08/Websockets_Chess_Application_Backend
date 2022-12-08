/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserManipulatorController.java
 * Last modified: 23.11.2022, 01:47
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

package pl.miloszgilga.chessappbackend.network.user_description;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import pl.miloszgilga.lib.jmpsl.security.user.CurrentUser;
import pl.miloszgilga.lib.jmpsl.oauth2.user.OAuth2UserExtender;

import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;
import pl.miloszgilga.chessappbackend.network.user_description.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(USER_DESCRIPTION_ENDPOINT)
class UserDescriptionController {

    private final IUserDescriptionService service;

    //------------------------------------------------------------------------------------------------------------------

    UserDescriptionController(IUserDescriptionService service) {
        this.service = service;
    }

    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(LOGGED_ACCOUNT_DESCRIPTION)
    ResponseEntity<AccountDescriptionResDto> getLoggedUserAccountDescription(@CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(service.getLoggedUserAccountDescription(userId), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(LOGGED_ACCOUNT_DESCRIPTION)
    ResponseEntity<SimpleServerMessageDto> setLoggedUserAccountDescription(@Valid @RequestBody AccountDescriptionReqDto req,
                                                                           @CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(service.setLoggerUserAccountDescription(req, userId), HttpStatus.OK);
    }
}
