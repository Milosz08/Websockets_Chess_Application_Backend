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

import org.javatuples.Pair;
import org.springframework.stereotype.Service;

import java.util.Map;

import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

import static pl.miloszgilga.chessappbackend.mail.MailTemplate.*;
import static pl.miloszgilga.chessappbackend.config.RedirectEndpoints.*;
import static pl.miloszgilga.chessappbackend.mail.MailTemplate.ACTIVATE_ACCOUNT;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class MailOutService implements IMailOutService {

    private final MailService mailService;
    private final StringManipulator manipulator;

    //------------------------------------------------------------------------------------------------------------------

    public MailOutService(MailService mailService, StringManipulator manipulator) {
        this.mailService = mailService;
        this.manipulator = manipulator;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void unsubscribeNewsletter(Long id, String userName, String email, String bearer, String otaToken) {
        final Pair<MailRequestDto, Map<String, Object>> basicMailData = mailService.generateBasicMailParameters(
                String.format("(%s) Chess Online: unsubscribe newsletter for %s (%s)", id, userName, otaToken), email);
        final Map<String, Object> parameters = basicMailData.getValue1();
        parameters.put("userName", userName);
        parameters.put("emailAddress", email);
        parameters.put("otaToken", otaToken);
        parameters.put("bearerButtonLink", mailService.otaTokenBearerPath(bearer, NEWSLETTER_UNSUBSCRIBE_VIA_LINK));
        mailService.generalEmailSender(basicMailData.getValue0(), parameters, UNSUBSCRIBE_NEWSLETTER);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void activateAccount(Long id, String email, LocalUserModel userModel, String bearer, String otaToken) {
        final Pair<MailRequestDto, Map<String, Object>> basicMailData = mailService.generateBasicMailParameters(
                String.format("(%s) Chess Online: Activate account for %s (%s)", id,
                        manipulator.generateFullName(userModel), otaToken), email);
        final Map<String, Object> parameters = basicMailData.getValue1();
        parameters.put("userName", userModel.getFirstName());
        parameters.put("emailAddress", userModel.getEmailAddress());
        parameters.put("otaToken", otaToken);
        parameters.put("bearerButtonLink", mailService.otaTokenBearerPath(bearer, ACTIVATE_ACCOUNT_VIA_LINK));
        mailService.generalEmailSender(basicMailData.getValue0(), parameters, ACTIVATE_ACCOUNT);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void changePassword(Long id, String email, LocalUserModel userModel, String bearer, String otaToken) {
        final Pair<MailRequestDto, Map<String, Object>> basicMailData = mailService.generateBasicMailParameters(
                String.format("(%s) Chess Online: Change password for %s (%s)", id,
                        manipulator.generateFullName(userModel), otaToken), email);
        final Map<String, Object> parameters = basicMailData.getValue1();
        parameters.put("userName", userModel.getFirstName());
        parameters.put("emailAddress", email);
        parameters.put("otaToken", otaToken);
        parameters.put("bearerButtonLink", mailService.otaTokenChangePasswordPath(bearer));
        mailService.generalEmailSender(basicMailData.getValue0(), parameters, CHANGE_PASSWORD);
    }
}
