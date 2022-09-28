/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OtaTokenController.java
 * Last modified: 26/09/2022, 23:00
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

package pl.miloszgilga.chessappbackend.network.ota_token;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import pl.miloszgilga.chessappbackend.dto.SimpleOtaTokenReqDto;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(OTA_TOKEN_ENDPOINT)
class OtaTokenController {

    private final IOtaTokenService otaTokenService;

    OtaTokenController(IOtaTokenService otaTokenService) {
        this.otaTokenService = otaTokenService;
    }

    @PostMapping(CHANGE_PASSWORD)
    ResponseEntity<SimpleServerMessageDto> changePassword(@RequestBody SimpleOtaTokenReqDto req) {
        return new ResponseEntity<>(otaTokenService.changePassword(req), HttpStatus.OK);
    }

    @PostMapping(ACTIVATE_ACCOUNT)
    ResponseEntity<SimpleServerMessageDto> activateAccount(@RequestBody SimpleOtaTokenReqDto req) {
        return new ResponseEntity<>(otaTokenService.activateAccount(req), HttpStatus.OK);
    }
}
