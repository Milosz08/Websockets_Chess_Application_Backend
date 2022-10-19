/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: JsonWebTokenVerificator.java
 * Last modified: 19/09/2022, 23:15
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

import io.jsonwebtoken.*;
import java.util.Optional;

import org.springframework.stereotype.Component;
import pl.miloszgilga.lib.jmpsl.security.jwt.JwtServlet;

import pl.miloszgilga.chessappbackend.token.dto.*;
import pl.miloszgilga.chessappbackend.exception.custom.TokenException;

import static pl.miloszgilga.chessappbackend.token.JwtClaim.*;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class JsonWebTokenVerificator {

    private final JwtServlet jwtServlet;

    //------------------------------------------------------------------------------------------------------------------

    public JsonWebTokenVerificator(JwtServlet jwtServlet) {
        this.jwtServlet = jwtServlet;
    }

    //------------------------------------------------------------------------------------------------------------------

    public ActivateServiceViaEmailTokenClaims validateActivatingServiceViaEmail(final String token) {
        final Claims extractedClaims = jwtServlet.extractClaims(token).orElseThrow(() -> {
            throw new TokenException.JwtMalformedTokenException(
                    "Data could not be verified due to an incorrect, expired or corrupted token.");
        });
        return ActivateServiceViaEmailTokenClaims.builder()
                .userId(extractedClaims.get(USER_ID.getClaimName(), Long.class))
                .emailAddress(extractedClaims.get(EMAIL.getClaimName(), String.class))
                .otaToken(extractedClaims.get(OTA_TOKEN.getClaimName(), String.class))
                .isExpired(extractedClaims.get(IS_EXPIRED.getClaimName(), Boolean.class))
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    public UserVerficationClaims validateUserJwtFilter(String token) {
        final Optional<Claims> extractedClaims = jwtServlet.extractClaims(token);
        if (extractedClaims.isEmpty()) return new UserVerficationClaims();
        final Claims claims = extractedClaims.get();
        return UserVerficationClaims.builder()
                .id(claims.get(USER_ID.getClaimName(), Long.class))
                .nickname(claims.get(NICKNAME.getClaimName(), String.class))
                .emailAddress(claims.get(EMAIL.getClaimName(), String.class))
                .build();
    }
}
