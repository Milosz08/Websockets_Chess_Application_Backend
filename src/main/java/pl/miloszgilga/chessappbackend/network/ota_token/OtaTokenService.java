/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OtaTokenService.java
 * Last modified: 26/09/2022, 23:07
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

import org.springframework.stereotype.Service;

import pl.miloszgilga.chessappbackend.dto.SimpleOtaTokenReqDto;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;

import pl.miloszgilga.chessappbackend.network.ota_token.domain.IOtaTokenRepository;

//----------------------------------------------------------------------------------------------------------------------

@Service
class OtaTokenService implements IOtaTokenService {

    private final IOtaTokenRepository otaTokenRepository;

    OtaTokenService(IOtaTokenRepository otaTokenRepository) {
        this.otaTokenRepository = otaTokenRepository;
    }

    @Override
    public SimpleServerMessageDto changePassword(SimpleOtaTokenReqDto req) {
        return new SimpleServerMessageDto("this is change password via ota token response");
    }

    @Override
    public SimpleServerMessageDto activateAccount(SimpleOtaTokenReqDto req) {
        return new SimpleServerMessageDto("this is activate account via ota token response");
    }
}
