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

import freemarker.template.Template;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import pl.miloszgilga.chessappbackend.exception.custom.EmailException;

import java.util.Map;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

//----------------------------------------------------------------------------------------------------------------------

@Service
class MailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    private static final String APP_LOGO = "static/gfx/app-logo.png";

    private final JavaMailSender sender;
    private final Configuration freemakerConfig;

    public MailService(JavaMailSender sender, Configuration freemakerConfig) {
        this.sender = sender;
        this.freemakerConfig = freemakerConfig;
    }

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
}
