/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OtaTokenServiceHelper.java
 * Last modified: 02/10/2022, 16:02
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.net.URI;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.utils.NetworkHelper;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenVerificator;
import pl.miloszgilga.chessappbackend.exception.custom.TokenException;
import pl.miloszgilga.chessappbackend.token.dto.ActivateServiceViaEmailTokenClaims;

import pl.miloszgilga.chessappbackend.network.ota_token.domain.*;
import pl.miloszgilga.chessappbackend.network.ota_token.dto.TokenLinkValidationData;

//----------------------------------------------------------------------------------------------------------------------

@Component
class OtaTokenServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtaTokenServiceHelper.class);

    private final NetworkHelper networkHelper;
    private final IOtaTokenRepository otaTokenRepository;
    private final JsonWebTokenVerificator tokenVerificator;

    //------------------------------------------------------------------------------------------------------------------

    OtaTokenServiceHelper(IOtaTokenRepository otaTokenRepository, JsonWebTokenVerificator tokenVerificator,
                          NetworkHelper networkHelper) {
        this.networkHelper = networkHelper;
        this.otaTokenRepository = otaTokenRepository;
        this.tokenVerificator = tokenVerificator;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    URI checkBearerTokenFromLinkWithOtaToken(final TokenLinkValidationData data) {
        String queryMessage;
        boolean ifError = false;
        try {
            final ActivateServiceViaEmailTokenClaims claims = tokenVerificator.validateActivatingServiceViaEmail(
                    data.getBearer());
            final OtaTokenModel otaToken = otaTokenRepository
                    .findTokenBasedValueAndUsed(claims.getOtaToken(), data.getType())
                    .orElseThrow(() -> {
                        LOGGER.error("Unable to activate service: {} via malformed bearer token. Token: {}",
                                data.getType(), data.getBearer());
                        throw new TokenException.JwtMalformedTokenException(data.getFailureMessage());
                    });
            otaToken.setAlreadyUsed(true);
            otaToken.getLocalUser().setIsActivated(true);
            queryMessage = data.getSuccessMessage();
        } catch (Exception ex) {
            queryMessage = ex.getMessage();
            ifError = true;
        }
        LOGGER.info("Successed activate service: {} via bearer token: {}. Redirect to: {}.",
                data.getType(), data.getBearer(), data.getRedirectUri());
        return networkHelper.generateRedirectUri(
                new Pair<>("message", queryMessage),
                data.getRedirectUri(),
                ifError);
    }
}
