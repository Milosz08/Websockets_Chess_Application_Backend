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

import org.slf4j.*;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Date;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import pl.miloszgilga.lib.jmpsl.core.*;
import pl.miloszgilga.lib.jmpsl.gfx.sender.*;
import pl.miloszgilga.lib.jmpsl.gfx.generator.*;

import pl.miloszgilga.chessappbackend.exception.custom.*;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenVerificator;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.token.dto.ActivateServiceViaEmailTokenClaims;

import pl.miloszgilga.chessappbackend.network.ota_token.dto.*;
import pl.miloszgilga.chessappbackend.network.ota_token.domain.*;
import pl.miloszgilga.chessappbackend.network.user_images.domain.LocalUserImagesModel;

import static pl.miloszgilga.lib.jmpsl.gfx.GfxUtil.convertRgbToHex;
import static pl.miloszgilga.lib.jmpsl.oauth2.OAuth2Supplier.LOCAL;
import static pl.miloszgilga.chessappbackend.utils.ImageUniquePrefix.AVATAR;
import static pl.miloszgilga.chessappbackend.token.OtaTokenType.ACTIVATE_ACCOUNT;

//----------------------------------------------------------------------------------------------------------------------

@Component
class OtaTokenServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(OtaTokenServiceHelper.class);

    private final UserImageSftpSender imageSftpSender;
    private final IOtaTokenRepository otaTokenRepository;
    private final JsonWebTokenVerificator tokenVerificator;

    //------------------------------------------------------------------------------------------------------------------

    OtaTokenServiceHelper(UserImageSftpSender imageSftpSender, IOtaTokenRepository otaTokenRepository,
                          JsonWebTokenVerificator tokenVerificator) {
        this.imageSftpSender = imageSftpSender;
        this.otaTokenRepository = otaTokenRepository;
        this.tokenVerificator = tokenVerificator;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    Pair<URI, LocalUserModel> checkBearerTokenFromLinkWithOtaToken(final TokenLinkValidationData data) {
        String queryMessage;
        LocalUserModel userModel = null;
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
            userModel = otaToken.getLocalUser();
            queryMessage = data.getSuccessMessage();
        } catch (Exception ex) {
            queryMessage = ex.getMessage();
            ifError = true;
        }
        LOGGER.info("Successed activate service: {} via bearer token: {}. Redirect to: {}.",
                data.getType(), data.getBearer(), data.getRedirectUri());
        return new Pair<>(ServletPathUtil.redirectMessageUri(queryMessage, data.getRedirectUri(), ifError), userModel);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    OtaTokenModel findTokenBasedEmailsAndTokenAndCompare(final OtaTokenMultipleEmailsReqDto req) {
        return req.getEmailAddresses().stream().map(e -> otaTokenRepository
            .findTokenByUserEmailOrSecondEmailAddress(e, ACTIVATE_ACCOUNT, req.getToken())
            .orElseThrow(() -> {
                LOGGER.error("Attempt to activate account with non existing token. Emails: {}", req.getEmailAddresses());
                throw new AuthException.UserNotFoundException("Unable to find OTA token. Please try again.");
            }))
            .collect(Collectors.toList())
            .get(0);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    boolean verifyOtaTokenDetailsAndSetAlreadyUsed(final OtaTokenModel tokenModel) {
        if (tokenModel.getExpirationDate().before(new Date())) {
            LOGGER.error("OTA token is expired. Token: {}", tokenModel);
            throw new TokenException.OtaTokenExpiredException("Passed token is valid, but it has already expired.");
        }
        tokenModel.setAlreadyUsed(true);
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    void generateAndSaveInitialUserImage(final LocalUserModel userModel) {
        if (!userModel.getOAuth2Supplier().equals(LOCAL)) return;
        final BufferedImageGeneratorPayload payload = BufferedImageGeneratorPayload.builder()
                .id(userModel.getId())
                .imageUniquePrefix(AVATAR.getImagePrefixName())
                .size(200).fontSize(80)
                .initials(StringUtil.initialsAsCharsArray(userModel.getFirstName(), userModel.getLastName()))
                .build();
        final BufferedImageGeneratorRes response = imageSftpSender.generateAndSaveDefaultUserImage(payload);
        final LocalUserImagesModel userImagesModel = userModel.getLocalUserImages();
        userImagesModel.setUserHashCode(response.getBufferedImageRes().getUserHashCode());
        userImagesModel.setHasAvatarImage(true);
        userImagesModel.setAvatarImage(response.getBufferedImageRes().getLocation());
        userImagesModel.setDefAvatarColor(convertRgbToHex(response.getGenerateImageBackground()));
        LOGGER.info("Successful saved user default image data. Data: {}", userImagesModel);
    }
}
