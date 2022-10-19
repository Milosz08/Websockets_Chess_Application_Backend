/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MainService.java
 * Last modified: 05/09/2022, 15:27
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

package pl.miloszgilga.chessappbackend.mail;

import org.slf4j.*;
import org.javatuples.Pair;

import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.io.IOException;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;

import pl.miloszgilga.lib.jmpsl.mail.*;
import pl.miloszgilga.lib.jmpsl.util.TimeUtil;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;
import static pl.miloszgilga.chessappbackend.config.RedirectEndpoints.NEWSLETTER_UNSUBSCRIBE_VIA_LINK;

//----------------------------------------------------------------------------------------------------------------------

@Service
class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String APP_LOGO = "static/gfx/app-logo.png";

    private final JsonWebTokenCreator creator;
    private final EnvironmentVars environment;

    //------------------------------------------------------------------------------------------------------------------

    public MailService(JsonWebTokenCreator creator, EnvironmentVars environment) {
        this.creator = creator;
        this.environment = environment;
    }

    //------------------------------------------------------------------------------------------------------------------

    Pair<MailRequestDto, Map<String, Object>> generateBasicMailParameters(String title, String sender) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("servletTime", TimeUtil.serializedUTC());
        parameters.put("tokensExpiredMinutes", environment.getOtaTokenExpiredMinutes());
        parameters.put("unsubscribeEndlessLink", unsNewsPath(creator.createNonExpUnsubscribeNewsletterToken(sender)));
        parameters.put("mailHelpdeskAgent", environment.getMailHelpdeskAgent() + "@" + environment.getFrontendName());
        parameters.put("applicationLink", environment.getFrontEndUrl());
        parameters.put("applicationName", environment.getFrontendName());
        try {
            final var request = MailRequestDto.builder()
                    .sendTo(Set.of(sender))
                    .sendFrom(environment.getServerMailClient())
                    .messageSubject(title)
                    .attachments(new ArrayList<>())
                    .inlineResources(List.of(new ResourceDto("app-logo.png", new ClassPathResource(APP_LOGO).getFile())))
                    .build();
            return new Pair<>(request, parameters);
        } catch (IOException ex) {
            LOGGER.error("Unable to find inline email resource. {}", ex.getMessage());
            throw new MailException.UnableToSendEmailException();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private String unsNewsPath(String bearer) {
        return environment.getBaseUrl() + NEWSLETTER_EMAIL_ENDPOINT + NEWSLETTER_UNSUBSCRIBE_VIA_LINK + bearer;
    }

    //------------------------------------------------------------------------------------------------------------------

    String otaTokenBearerPath(String bearer, String subdomain) {
        return environment.getBaseUrl() + OTA_TOKEN_ENDPOINT + subdomain + bearer;
    }

    //------------------------------------------------------------------------------------------------------------------

    String otaTokenChangePasswordPath(String bearer) {
        return UriComponentsBuilder
                .fromPath(environment.getFrontEndUrl() + environment.getChangePasswordRedirectUri())
                .queryParam("token", bearer)
                .toUriString();
    }

    //------------------------------------------------------------------------------------------------------------------

    String generateMailTitle(Long id, String reason, String userName, String token) {
        return String.format("(%s) Chess Online: %s for %s (%s)", id, reason, userName, token);
    }
}
