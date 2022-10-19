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

import io.jsonwebtoken.*;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;

import java.util.Date;

import pl.miloszgilga.lib.jmpsl.util.TimeUtil;
import pl.miloszgilga.lib.jmpsl.security.jwt.JwtServlet;
import pl.miloszgilga.lib.jmpsl.oauth2.user.OAuth2UserExtender;
import pl.miloszgilga.lib.jmpsl.oauth2.resolver.IOAuth2TokenGenerator;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.security.LocalUserRole;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

import static pl.miloszgilga.chessappbackend.token.JwtClaim.*;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class JsonWebTokenCreator implements IOAuth2TokenGenerator {

    private final JwtServlet jwtServlet;
    private final EnvironmentVars environment;

    //------------------------------------------------------------------------------------------------------------------

    public JsonWebTokenCreator(JwtServlet jwtServlet, EnvironmentVars environment) {
        this.jwtServlet = jwtServlet;
        this.environment = environment;
    }

    //------------------------------------------------------------------------------------------------------------------

    public String createAcitivateServiceViaEmailToken(String email, Long id, String otaToken) {
        final Claims claims = Jwts.claims();
        claims.put(USER_ID.getClaimName(), id);
        claims.put(EMAIL.getClaimName(), email);
        claims.put(OTA_TOKEN.getClaimName(), otaToken);
        claims.put(IS_EXPIRED.getClaimName(), true);
        claims.setExpiration(TimeUtil.addMinutes(environment.getOtaTokenExpiredMinutes()));
        return jwtServlet.generateToken("unsubscribe-newsletter-token", claims);
    }

    //------------------------------------------------------------------------------------------------------------------

    public String createAcitivateServiceViaEmailToken(LocalUserModel userModel, String otaToken) {
        return createAcitivateServiceViaEmailToken(userModel.getEmailAddress(), userModel.getId(), otaToken);
    }

    //------------------------------------------------------------------------------------------------------------------

    public String createUserCredentialsToken(LocalUserModel user) {
        final Claims claims = createBasicAuthTokenWithoutExpired(user);
        claims.setExpiration(TimeUtil.addMinutes(environment.getBearerTokenExpiredMinutes()));
        return jwtServlet.generateToken("user-credentials", claims);
    }

    //------------------------------------------------------------------------------------------------------------------

    public Pair<String, Date> createUserRefreshToken(LocalUserModel user) {
        final Claims claims = createBasicAuthTokenWithoutExpired(user);
        final Date expirationDate = TimeUtil.addMonths(environment.getRefreshTokenExpiredMonths());
        claims.setExpiration(expirationDate);
        return new Pair<>(jwtServlet.generateToken("user-refresh-token", claims), expirationDate);
    }

    //------------------------------------------------------------------------------------------------------------------

    private Claims createBasicAuthTokenWithoutExpired(LocalUserModel user) {
        final Claims claims = Jwts.claims();
        claims.put(USER_ID.getClaimName(), user.getId());
        claims.put(NICKNAME.getClaimName(), user.getNickname());
        claims.put(EMAIL.getClaimName(), user.getEmailAddress());
        claims.put(ROLES.getClaimName(), LocalUserRole.simplifyUserRoles(user.getRoles()));
        return claims;
    }

    //------------------------------------------------------------------------------------------------------------------

    public String createNonExpUnsubscribeNewsletterToken(String email) {
        final Claims claims = Jwts.claims();
        claims.put(EMAIL.getClaimName(), email);
        claims.put(IS_EXPIRED.getClaimName(), false);
        return jwtServlet.generateToken("non-expired-unsubscribe-newsletter-token", claims);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String generateToken(Authentication auth) {
        final OAuth2UserExtender localAuthUser = (OAuth2UserExtender) auth.getPrincipal();
        return createUserCredentialsToken((LocalUserModel) localAuthUser.getUserModel());
    }
}
