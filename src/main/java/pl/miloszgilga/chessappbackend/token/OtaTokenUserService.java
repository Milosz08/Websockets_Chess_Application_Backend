/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OtaTokenUserService.java
 * Last modified: 01/10/2022, 21:00
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

package pl.miloszgilga.chessappbackend.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

import pl.miloszgilga.chessappbackend.network.ota_token.domain.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class OtaTokenUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtaTokenUserService.class);

    private final TimeHelper timeHelper;
    private final EnvironmentVars environment;
    private final OneTimeAccessToken otaService;
    private final IOtaTokenRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    public OtaTokenUserService(TimeHelper timeHelper, EnvironmentVars environment, OneTimeAccessToken otaService,
                               IOtaTokenRepository repository) {
        this.timeHelper = timeHelper;
        this.environment = environment;
        this.otaService = otaService;
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public String generateAndSaveUserOtaToken(OtaTokenType tokenType, LocalUserModel userModel) {
        String token;
        do {
            token = otaService.generateToken();
        } while (repository.checkIfOtaTokenExist(token, tokenType) || token.equals(""));

        final OtaTokenModel otaTokenModel = OtaTokenModel.builder()
                .otaToken(token)
                .alreadyUsed(false)
                .expirationDate(timeHelper.addMinutesToCurrentDate(environment.getOtaTokenExpiredMinutes()))
                .usedFor(tokenType)
                .localUser(userModel)
                .build();

        repository.save(otaTokenModel);
        LOGGER.info("Successfully generated OTA ({} min exp) token for {}: {}",
                environment.getOtaTokenExpiredMinutes(), tokenType.getOtaTokenTypeName(), token);
        return token;
    }
}
