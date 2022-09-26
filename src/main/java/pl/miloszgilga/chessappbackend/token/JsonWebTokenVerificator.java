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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.*;

import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.exception.custom.TokenException;
import pl.miloszgilga.chessappbackend.token.dto.UserVerficationClaims;
import pl.miloszgilga.chessappbackend.token.dto.NewsletterUnsubscribeClaims;

import static pl.miloszgilga.chessappbackend.token.JwtClaim.*;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class JsonWebTokenVerificator {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonWebTokenVerificator.class);
    private final JsonWebToken jsonWebToken;

    public JsonWebTokenVerificator(JsonWebToken jsonWebToken1) {
        this.jsonWebToken = jsonWebToken1;
    }

    public NewsletterUnsubscribeClaims validateUnsubscriveNewsletterJwt(String token) {
        if (basicTokenIsMalformed(token)) {
            throw new TokenException.JwtMalformedTokenException(
                    "Data could not be verified due to an incorrect, expired or corrupted token.");
        }
        Claims claims = extractClaimsFromRawToken(token);
        return new NewsletterUnsubscribeClaims(
                claims.get(EMAIL.getClaimName(), String.class),
                claims.get(IS_EXPIRED.getClaimName(), Boolean.class),
                claims.get(OTA_TOKEN.getClaimName(), String.class)
        );
    }

    public UserVerficationClaims validateUserJwtFilter(String token) {
        if (basicTokenIsMalformed(token)) return new UserVerficationClaims();
        Claims claims = extractClaimsFromRawToken(token);
        return new UserVerficationClaims(
                claims.get(USER_ID.getClaimName(), Long.class),
                claims.get(NICKNAME.getClaimName(), String.class),
                claims.get(EMAIL.getClaimName(), String.class)
        );
    }

    private Claims extractClaimsFromRawToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jsonWebToken.getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean basicTokenIsMalformed(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jsonWebToken.getSignatureKey()).build().parseClaimsJws(token);
            return false;
        } catch (MalformedJwtException ex) {
            LOGGER.error("Passed JSON Web Token is malformed. Token: {}", token);
        } catch (ExpiredJwtException ex) {
            LOGGER.error("Passed JSON Web Token is expired. Token: {}", token);
            throw new TokenException
                    .JwtTokenExpiredException("Session is expired. Before insert any changes please login again.");
        } catch (JwtException ex) {
            LOGGER.error("Passed JSON Web Token is invalid. Token: {}", token);
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Some of the JSON Web Token claims are nullable. Token: {}", token);
        }
        return true;
    }
}
