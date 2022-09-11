/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MailCustomService.java
 * Last modified: 05/09/2022, 20:38
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

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.token.JsonWebToken;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class MailOutService implements IMailOutService {

    private final MailService mailService;
    private final TimeHelper timeHelper;
    private final JsonWebToken webToken;
    private final EnvironmentVars environment;

    public MailOutService(
            MailService mailService, TimeHelper timeHelper, JsonWebToken webToken, EnvironmentVars environment
    ) {
        this.mailService = mailService;
        this.timeHelper = timeHelper;
        this.webToken = webToken;
        this.environment = environment;
    }

    @Override
    public void unsubscribeNewsletter(String email, String bearer, String otaToken) {
        final String messageTitle = "Chess Online: unsubscribe newsletter for " + email;
        final var request = new MailRequestDto(List.of(email), environment.getServerMailClient(), messageTitle);
        final Map<String, Object> parameters = new HashMap<>(extendsParametersWithDef(email));
        parameters.put("emailAddress", email);
        parameters.put("otaToken", otaToken);
        parameters.put("mailHelpdeskAgent", environment.getMailHelpdeskAgent() + "@" + environment.getFrontendName());
        parameters.put("buttonLink", computeBearer(bearer));
        mailService.generalEmailSender(request, parameters, MailTemplate.UNSUBSCRIBE_NEWSLETTER);
    }

    private Map<String, Object> extendsParametersWithDef(String email) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("servletTime", timeHelper.getCurrentUTC());
        parameters.put("unsubscribeEndlessLink", computeBearer(webToken.createNonExpUnsubscribeNewsletterToken(email)));
        parameters.put("applicationLink", environment.getFrontEndUrl());
        parameters.put("applicationName", environment.getFrontendName());
        return parameters;
    }

    private String computeBearer(String bearer) {
        return environment.getFrontEndUrl() + environment.getNewsletterUnsubscribePath() + bearer;
    }
}
