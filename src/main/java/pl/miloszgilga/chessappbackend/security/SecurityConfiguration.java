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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.filter.JwtTokenAuthenticationFilter;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final EnvironmentVars environment;
    private final JwtTokenAuthenticationFilter authenticationFilter;
    private final AuthenticationRestEntryPoint restEntryPoint;

    public static final String[] DISABLE_PATHS_FOR_JWT_FILTERING = {
            "/", "/error", "/oauth2/**",
            NEWSLETTER_EMAIL_ENDPOINT + "/**",
            EXPOSE_STATIC_DATA_ENDPOINT + "/**",
    };

    public SecurityConfiguration(
            EnvironmentVars environment, JwtTokenAuthenticationFilter authenticationFilter,
            AuthenticationRestEntryPoint restEntryPoint
    ) {
        this.environment = environment;
        this.authenticationFilter = authenticationFilter;
        this.restEntryPoint = restEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        enableH2ConsoleForDevelopment(http);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
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
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CookieOAuth2ReqRepository cookieOAuth2ReqRepository() {
        return new CookieOAuth2ReqRepository(cookieHelper, environment);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(environment.getPasswordEncoderStrength());
    }

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
