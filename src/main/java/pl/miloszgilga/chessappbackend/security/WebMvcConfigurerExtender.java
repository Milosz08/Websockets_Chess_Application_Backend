/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: WebMvcConfigurerExtender.java
 * Last modified: 01/09/2022, 20:06
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

import org.springframework.validation.Validator;
import org.springframework.context.annotation.*;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.validation.beanvalidation.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.springframework.http.HttpMethod.*;

import java.util.Locale;
import java.nio.charset.StandardCharsets;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

//----------------------------------------------------------------------------------------------------------------------

@Configuration
public class WebMvcConfigurerExtender implements WebMvcConfigurer {

    private final EnvironmentVars environment;

    //------------------------------------------------------------------------------------------------------------------

    public WebMvcConfigurerExtender(EnvironmentVars environment) {
        this.environment = environment;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(environment.getFrontEndUrl())
                .allowedMethods(GET.name(), POST.name(), OPTIONS.name(), PUT.name(), PATCH.name(), DELETE.name())
                .allowCredentials(true)
                .allowedHeaders("*")
                .maxAge(environment.getCorsMaxAgeSeconds());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean("messageSource")
    public MessageSource messageSource() {
        final var messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("locale/messages");
        messageSource.setDefaultEncoding(String.valueOf(StandardCharsets.UTF_8));
        return messageSource;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public LocaleResolver localeResolver() {
        final var cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public Validator getValidator() {
        final var validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource());
        return validator;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
