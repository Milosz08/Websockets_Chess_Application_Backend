/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: JsonWebTokenCreator.java
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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.security.LocalUserRole;
import pl.miloszgilga.chessappbackend.network.auth_local.domain.LocalUserModel;

import static pl.miloszgilga.chessappbackend.token.JwtClaim.*;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class JsonWebTokenCreator {

    private final TimeHelper timeHelper;
    private final EnvironmentVars environment;
    private final JsonWebToken jsonWebToken;

    public JsonWebTokenCreator(TimeHelper timeHelper, EnvironmentVars environment, JsonWebToken jsonWebToken) {
        this.timeHelper = timeHelper;
        this.environment = environment;
        this.jsonWebToken = jsonWebToken;
    }

    public String createUnsubscribeNewsletterToken(String email, String otaToken) {
        final Claims claims = Jwts.claims();
        claims.put(EMAIL.getClaimName(), email);
        claims.put(OTA_TOKEN.getClaimName(), otaToken);
        claims.put(IS_EXPIRED.getClaimName(), true);
        claims.setExpiration(timeHelper.addMinutesToCurrentDate(environment.getOtaTokenExpiredMinutes()));
        return basicJwtToken("unsubscribe-newsletter-token", claims);
    }

    public String createUserCredentialsToken(LocalUserModel user) {
        final Claims claims = Jwts.claims();
        claims.put(USER_ID.getClaimName(), user.getId());
        claims.put(NICKNAME.getClaimName(), user.getNickname());
        claims.put(EMAIL.getClaimName(), user.getEmailAddress());
        claims.put(ROLES.getClaimName(), LocalUserRole.simplifyUserRoles(user.getRoles()));
        claims.setExpiration(timeHelper.addMinutesToCurrentDate(environment.getBearerTokenExpiredMinutes()));
        return basicJwtToken("user-credentials", claims);
    }

    public String createNonExpUnsubscribeNewsletterToken(String email) {
        final Claims claims = Jwts.claims();
        claims.put(EMAIL.getClaimName(), email);
        claims.put(IS_EXPIRED.getClaimName(), false);
        return basicJwtToken("non-expired-unsubscribe-newsletter-token", claims);
    }

    private String basicJwtToken(String subject, Claims claims) {
        return Jwts.builder()
                .setIssuer(environment.getJwtIssuer())
                .setSubject(subject)
                .setClaims(claims)
                .signWith(jsonWebToken.getSignatureKey())
                .compact();
    }
}
