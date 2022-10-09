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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.javatuples.Pair;
import freemarker.template.*;

import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.exception.custom.EmailException;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.NEWSLETTER_EMAIL_ENDPOINT;
import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.OTA_TOKEN_ENDPOINT;
import static pl.miloszgilga.chessappbackend.config.RedirectEndpoints.NEWSLETTER_UNSUBSCRIBE_VIA_LINK;

//----------------------------------------------------------------------------------------------------------------------

@Service
class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String APP_LOGO = "static/gfx/app-logo.png";

    private final JavaMailSender sender;
    private final TimeHelper timeHelper;
    private final JsonWebTokenCreator creator;
    private final EnvironmentVars environment;
    private final Configuration freemakerConfig;

    //------------------------------------------------------------------------------------------------------------------

    public MailService(JavaMailSender sender, JsonWebTokenCreator creator, EnvironmentVars environment,
                       Configuration freemakerConfig, TimeHelper timeHelper) {
        this.sender = sender;
        this.creator = creator;
        this.timeHelper = timeHelper;
        this.environment = environment;
        this.freemakerConfig = freemakerConfig;
    }

    //------------------------------------------------------------------------------------------------------------------

    void generalEmailSender(MailRequestDto request, Map<String, Object> model, MailTemplate template) {
        final MimeMessage mimeMessage = sender.createMimeMessage();
        try {
            final var helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            final Template mailTemplate = freemakerConfig.getTemplate(template.getTemplateName());
            String parsedHTML = FreeMarkerTemplateUtils.processTemplateIntoString(mailTemplate, model);

            for (String client : request.getSendTo()) {
                helper.setTo(client);
            }
            helper.setText(parsedHTML, true);
            helper.addInline("app-logo.png", new ClassPathResource(APP_LOGO).getFile());
            helper.setSubject(request.getMessageSubject());
            helper.setFrom(request.getSendFrom());

            sender.send(mimeMessage);
            LOGGER.info("Email message from template {} was successfully send. Request parameters: {}",
                    template.getTemplateName(), request);

        } catch (MessagingException | IOException ex) {
            LOGGER.error("Unexpected issue on sending email. Request parameters: {}", request);
            throw new EmailException.EmailSenderException("Unable connect to SMTP mail server. Try again later.");
        } catch (TemplateException ex) {
            LOGGER.error("Email sender malfunction: template {} is not valid or not exist. Request parameters: {}",
                    template.getTemplateName(), request);
            throw new EmailException.EmailSenderException("Unable connect to SMTP mail server. Try again later.");
        } catch (Exception ex) {
            LOGGER.error("Email sender malfunction: {}", ex.getMessage());
            throw new EmailException.EmailSenderException("Unable connect to SMTP mail server. Try again later.");
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    Pair<MailRequestDto, Map<String, Object>> generateBasicMailParameters(String title, String sender) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("servletTime", timeHelper.getCurrentUTC());
        parameters.put("tokensExpiredMinutes", environment.getOtaTokenExpiredMinutes());
        parameters.put("unsubscribeEndlessLink", unsNewsPath(creator.createNonExpUnsubscribeNewsletterToken(sender)));
        parameters.put("mailHelpdeskAgent", environment.getMailHelpdeskAgent() + "@" + environment.getFrontendName());
        parameters.put("applicationLink", environment.getFrontEndUrl());
        parameters.put("applicationName", environment.getFrontendName());
        final var request = new MailRequestDto(List.of(sender), environment.getServerMailClient(), title);
        final Map<String, Object> extendsParameters = new HashMap<>(parameters);
        return new Pair<>(request, extendsParameters);
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
}
