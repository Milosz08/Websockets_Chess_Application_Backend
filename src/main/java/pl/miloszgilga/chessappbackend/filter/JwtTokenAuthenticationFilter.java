/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: JwtTokenAuthenticationFilter.java
 * Last modified: 19/09/2022, 07:48
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

package pl.miloszgilga.chessappbackend.filter;

import org.javatuples.Pair;

import org.springframework.util.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.*;
import javax.servlet.http.*;

import java.util.Arrays;
import java.io.IOException;

import pl.miloszgilga.lib.jmpsl.auth.jwt.JwtServlet;
import pl.miloszgilga.lib.jmpsl.auth.jwt.JwtValidationType;
import static pl.miloszgilga.lib.jmpsl.auth.jwt.JwtValidationType.EXPIRED;

import pl.miloszgilga.chessappbackend.token.JsonWebTokenVerificator;
import pl.miloszgilga.chessappbackend.security.AuthUserDetailService;
import pl.miloszgilga.chessappbackend.exception.custom.TokenException;
import pl.miloszgilga.chessappbackend.token.dto.UserVerficationClaims;

import static pl.miloszgilga.chessappbackend.security.SecurityConfiguration.DISABLE_PATHS_FOR_JWT_FILTERING;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtServlet jwtServlet;
    private final JsonWebTokenVerificator verificator;
    private final AuthUserDetailService userDetailService;

    //------------------------------------------------------------------------------------------------------------------

    public JwtTokenAuthenticationFilter(JwtServlet jwtServlet, AuthUserDetailService userDetailService,
                                        JsonWebTokenVerificator verificator) {
        this.jwtServlet = jwtServlet;
        this.verificator = verificator;
        this.userDetailService = userDetailService;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        final String token = jwtServlet.extractToken(req);
        if (StringUtils.hasText(token)) {
            final Pair<Boolean, JwtValidationType> validatedToken = jwtServlet.isValid(token);
            if (!validatedToken.getValue0() && validatedToken.getValue1().equals(EXPIRED)) {
                throw new TokenException.JwtTokenExpiredException("Token is expired. Before insert any changes please " +
                        "enter new token.");
            } else if(validatedToken.getValue0()) {
                final UserVerficationClaims verficationClaims = verificator.validateUserJwtFilter(token);
                final UserDetails authUser = userDetailService.loadUserByNicknameEmailAndId(verficationClaims);
                final var authToken = new UsernamePasswordAuthenticationToken(authUser, null, authUser.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(req, res);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        final AntPathMatcher matcher = new AntPathMatcher();
        return Arrays.stream(DISABLE_PATHS_FOR_JWT_FILTERING).anyMatch(p -> matcher.match(p, req.getServletPath()));
    }
}
