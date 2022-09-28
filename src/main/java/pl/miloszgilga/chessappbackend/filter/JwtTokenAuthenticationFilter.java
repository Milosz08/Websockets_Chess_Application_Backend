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

import com.nimbusds.common.contenttype.ContentType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.io.IOException;

import pl.miloszgilga.chessappbackend.oauth.AuthUser;
import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.exception.ServerExceptionRes;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenVerificator;
import pl.miloszgilga.chessappbackend.security.AuthUserDetailService;
import pl.miloszgilga.chessappbackend.exception.BasicDataExceptionRes;
import pl.miloszgilga.chessappbackend.token.dto.UserVerficationClaims;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static pl.miloszgilga.chessappbackend.security.SecurityConfiguration.DISABLE_PATHS_FOR_JWT_FILTERING;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final TimeHelper timeHelper;
    private final StringManipulator manipulator;
    private final JsonWebTokenVerificator verificator;
    private final AuthUserDetailService userDetailService;

    public JwtTokenAuthenticationFilter(TimeHelper timeHelper, AuthUserDetailService userDetailService,
                                        JsonWebTokenVerificator verificator, StringManipulator manipulator) {
        this.timeHelper = timeHelper;
        this.manipulator = manipulator;
        this.verificator = verificator;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        final String token = extractJwtTokenFromRequest(req);
        try {
            if (StringUtils.hasText(token) && !verificator.basicTokenIsMalformed(token)) {
                final UserVerficationClaims verficationClaims = verificator.validateUserJwtFilter(token);
                final AuthUser authUser = userDetailService.loadUserByNicknameEmailAndId(verficationClaims);
                final var authToken = new UsernamePasswordAuthenticationToken(authUser, authUser.getUserModel().getId());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception ex) {
            final var serverExceptionRes = ServerExceptionRes.builder()
                    .path(req.getServletPath())
                    .method(req.getMethod())
                    .statusCode(UNAUTHORIZED.value())
                    .statusText(UNAUTHORIZED.name())
                    .servletTimestampUTC(timeHelper.getCurrentUTC())
                    .build();
            final var basicDataExceptionRes = new BasicDataExceptionRes(serverExceptionRes, ex.getMessage());
            SecurityContextHolder.clearContext();

            res.setContentType(ContentType.APPLICATION_JSON.getType());
            res.setStatus(UNAUTHORIZED.value());
            res.getWriter().write(manipulator.convertObjectToJson(basicDataExceptionRes));
        }
        filterChain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        final AntPathMatcher matcher = new AntPathMatcher();
        return Arrays.stream(DISABLE_PATHS_FOR_JWT_FILTERING).anyMatch(p -> matcher.match(p, req.getServletPath()));
    }

    private String extractJwtTokenFromRequest(HttpServletRequest req) {
        final String bearerToken = req.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return "";
    }
}
