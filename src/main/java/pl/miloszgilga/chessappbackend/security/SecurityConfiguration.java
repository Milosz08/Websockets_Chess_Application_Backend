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

import org.springframework.context.annotation.*;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import pl.miloszgilga.chessappbackend.oauth.*;
import pl.miloszgilga.chessappbackend.filter.*;
import pl.miloszgilga.chessappbackend.oauth.resolver.*;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final EnvironmentVars environment;
    private final SecurityHelper securityHelper;
    private final AuthenticationRestEntryPoint restEntryPoint;
    private final JwtTokenAuthenticationFilter authenticationFilter;
    private final MiddlewareExceptionsFilter middlewareExceptionsFilter;

    private final AppOidcUserService appOidcUserService;
    private final AppOAuth2UserService appOAuth2UserService;
    private final OAuth2AuthFailureResolver oAuth2AuthFailureResolver;
    private final OAuth2AuthSuccessfulResolver oAuth2AuthSuccessfulResolver;

    //------------------------------------------------------------------------------------------------------------------

    public static final String[] DISABLE_PATHS_FOR_JWT_FILTERING = {
            "/", "/error", "/oauth2/**", "/h2-console/**",
            NEWSLETTER_EMAIL_ENDPOINT + "/**",
            AUTH_ENDPOINT + LOGIN_VIA_LOCAL + "/**",
            AUTH_ENDPOINT + SIGNUP_VIA_LOCAL + "/**",
            AUTH_ENDPOINT + AUTO_LOGIN + "/**",
            AUTH_ENDPOINT + REFRESH_TOKEN + "/**",
            AUTH_ENDPOINT + ATTEMPT_FINISH_SIGNUP_RESEND_EMAIL + "/**",
            OTA_TOKEN_ENDPOINT + "/**",
            RENEW_CREDETIALS_LOCAL + "/**",
            EXPOSE_STATIC_DATA_ENDPOINT + "/**",
    };

    //------------------------------------------------------------------------------------------------------------------

    public SecurityConfiguration(EnvironmentVars environment, JwtTokenAuthenticationFilter authFilter,
                                 AuthenticationRestEntryPoint restPoint, AppOidcUserService oidc,
                                 MiddlewareExceptionsFilter exFilter, AppOAuth2UserService oauth2,
                                 SecurityHelper security, OAuth2AuthSuccessfulResolver successfulResolver,
                                 OAuth2AuthFailureResolver failureResolver) {
        this.environment = environment;
        this.middlewareExceptionsFilter = exFilter;
        this.securityHelper = security;
        this.authenticationFilter = authFilter;
        this.restEntryPoint = restPoint;
        this.appOidcUserService = oidc;
        this.appOAuth2UserService = oauth2;
        this.oAuth2AuthFailureResolver = failureResolver;
        this.oAuth2AuthSuccessfulResolver = successfulResolver;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        enableH2ConsoleForDevelopment(http);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(middlewareExceptionsFilter, LogoutFilter.class)
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                    .authenticationEntryPoint(restEntryPoint)
                    .and()
                .authorizeRequests()
                    .antMatchers(DISABLE_PATHS_FOR_JWT_FILTERING).permitAll()
                    .anyRequest().authenticated()
                    .and()
                .oauth2Login()
                    .authorizationEndpoint()
                        .authorizationRequestRepository(cookieOAuth2ReqRepository())
                        .and()
                    .redirectionEndpoint()
                        .and()
                    .userInfoEndpoint()
                        .oidcUserService(appOidcUserService)
                        .userService(appOAuth2UserService)
                        .and()
                    .tokenEndpoint()
                        .accessTokenResponseClient(securityHelper.authTokenCodeResponseToTheClient())
                        .and()
                    .successHandler(oAuth2AuthSuccessfulResolver)
                    .failureHandler(oAuth2AuthFailureResolver)
                    .and()
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public CookieOAuth2ReqRepository cookieOAuth2ReqRepository() {
        return new CookieOAuth2ReqRepository(environment);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(environment.getPasswordEncoderStrength());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void enableH2ConsoleForDevelopment(HttpSecurity http) throws Exception {
        if (!environment.getApplicationMode().equals(ApplicationMode.DEV.getModeName())) return;
        http.authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                    .and()
                .csrf().ignoringAntMatchers("/h2-console/**")
                    .and()
                .headers().frameOptions().sameOrigin();
    }
}
