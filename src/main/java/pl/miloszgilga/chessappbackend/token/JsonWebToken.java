/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: JsonWebToken.java
 * Last modified: 02/09/2022, 22:22
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

import com.auth0.jwt.JWTCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTVerificationException;

import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.exception.custom.TokenException;

import static pl.miloszgilga.chessappbackend.token.JwtClaim.*;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class JsonWebToken {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonWebToken.class);

    private final Algorithm algorithm;

    private final TimeHelper timeHelper;
    private final EnvironmentVars environment;

    public JsonWebToken(EnvironmentVars environment, TimeHelper timeHelper) {
        this.environment = environment;
        this.timeHelper = timeHelper;
        this.algorithm = Algorithm.HMAC256(environment.getJwtSecretKey().getBytes());
    }

    public DecodedJWT verifyJsonWebToken(String token) {
        try {
            final JWTVerifier verifier = JWT.require(algorithm).withIssuer(environment.getJwtIssuer()).build();
            return verifier.verify(token);
        } catch (JWTVerificationException ex) {
            LOGGER.error("Passed JWT is malformed, expired or corrupted. Token: {}", token);
            throw new TokenException.JwtMalformedTokenException(
                    "Data could not be verified due to an incorrect, expired or corrupted token.");
        }
    }

    public String createUnsubscribeNewsletterToken(String email, String otaToken) {
        return basicJwtToken("unsubscribe-newsletter-token")
                .withClaim(EMAIL.getClaimName(), email)
                .withClaim(OTA_TOKEN.getClaimName(), otaToken)
                .withClaim(IS_EXPIRED.getClaimName(), true)
                .withExpiresAt(timeHelper.addMinutesToCurrentDate(environment.getOtaTokenExpiredMinutes()))
                .sign(algorithm);
    }

    public String createNonExpUnsubscribeNewsletterToken(String email) {
        return basicJwtToken("non-expired-unsubscribe-newsletter-token")
                .withClaim(EMAIL.getClaimName(), email)
                .withClaim(IS_EXPIRED.getClaimName(), false)
                .sign(algorithm);
    }

    private JWTCreator.Builder basicJwtToken(String subject) {
        return JWT.create()
                .withIssuer(environment.getJwtIssuer())
                .withSubject(subject);
    }
}
