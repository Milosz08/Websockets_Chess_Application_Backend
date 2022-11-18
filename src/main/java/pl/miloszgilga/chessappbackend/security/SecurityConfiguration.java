/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SecurityConfiguration.java
 * Last modified: 02/09/2022, 20:04
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

package pl.miloszgilga.chessappbackend.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.*;

import org.springframework.core.env.Environment;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import pl.miloszgilga.lib.jmpsl.oauth2.*;
import pl.miloszgilga.lib.jmpsl.security.*;
import pl.miloszgilga.lib.jmpsl.oauth2.service.*;
import pl.miloszgilga.lib.jmpsl.oauth2.resolver.*;
import pl.miloszgilga.lib.jmpsl.security.excluder.SecurityPathExcluder;
import pl.miloszgilga.lib.jmpsl.security.filter.MiddlewareExceptionFilter;

import pl.miloszgilga.chessappbackend.filter.*;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.network.auth.SignupService;

//----------------------------------------------------------------------------------------------------------------------

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private final Environment env;
    private final SignupService signupService;
    private final JsonWebTokenCreator tokenCreator;

    private final SecurityPathExcluder securityPathExcluder;
    private final OAuth2OnFailureResolver oAuth2OnFailureResolver;
    private final JwtTokenAuthenticationFilter authenticationFilter;
    private final MiddlewareExceptionFilter middlewareExceptionFilter;
    private final CookieOAuth2ReqRepository cookieOAuth2ReqRepository;
    private final AuthResolverForRestEntryPoint resolverForRestEntryPoint;

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SecurityUtil.enableH2ConsoleForDev(http, env);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(middlewareExceptionFilter, LogoutFilter.class)
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                    .authenticationEntryPoint(resolverForRestEntryPoint);

        http = securityPathExcluder.loadExcludedPathFromSpringSecurityContext(http);

        http.oauth2Login()
                .authorizationEndpoint()
                    .authorizationRequestRepository(cookieOAuth2ReqRepository)
                .and()
                    .redirectionEndpoint()
                .and()
                    .userInfoEndpoint()
                        .oidcUserService(new AppOidcUserService(signupService))
                        .userService(new AppOAuth2UserService(env, signupService))
                    .and()
                    .tokenEndpoint()
                        .accessTokenResponseClient(OAuth2Util.auth2AccessTokenResponseClient())
                    .and()
                        .successHandler(oAuth2OnSuccessfulResolver())
                        .failureHandler(oAuth2OnFailureResolver)
                    .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public OAuth2OnSuccessfulResolver oAuth2OnSuccessfulResolver() {
        return new OAuth2OnSuccessfulResolver(env, tokenCreator);
    }
}
