/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AuthLocalServiceHelper.java
 * Last modified: 23/09/2022, 20:56
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

package pl.miloszgilga.chessappbackend.network.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.util.*;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.token.*;
import pl.miloszgilga.chessappbackend.mail.IMailOutService;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.network.newsletter_email.domain.*;

import static pl.miloszgilga.chessappbackend.security.LocalUserRole.USER;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class AuthServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceHelper.class);

    private final StringManipulator manipulator;
    private final IMailOutService mailOutService;
    private final JsonWebTokenCreator tokenCreator;
    private final ILocalUserRoleRepository roleRepository;
    private final OtaTokenUserService otaTokenUserService;
    private final ILocalUserRepository localUserRepository;
    private final INewsletterEmailRepository newsletterEmailRepository;

    //------------------------------------------------------------------------------------------------------------------

    AuthServiceHelper(StringManipulator manipulator, IMailOutService mailOutService, JsonWebTokenCreator tokenCreator,
                      ILocalUserRoleRepository roleRepository, OtaTokenUserService otaTokenUserService,
                      ILocalUserRepository localUserRepository, INewsletterEmailRepository newsletterEmailRepository) {
        this.manipulator = manipulator;
        this.tokenCreator = tokenCreator;
        this.mailOutService = mailOutService;
        this.roleRepository = roleRepository;
        this.otaTokenUserService = otaTokenUserService;
        this.localUserRepository = localUserRepository;
        this.newsletterEmailRepository = newsletterEmailRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public Set<LocalUserRoleModel> findAndGenerateUserRole() {
        final Set<LocalUserRoleModel> roles = new HashSet<>();
        final Optional<LocalUserRoleModel> foundRole = roleRepository.findRoleByType(USER);
        if (foundRole.isEmpty()) {
            LOGGER.error("Role for user is not present in database.");
            throw new AuthException.RoleNotFoundException("Unable to create new account. Try again later.");
        }
        roles.add(foundRole.get());
        return roles;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    LocalUserModel findUserAndReturnUserData(Long userId) {
        if (userId == null) {
            LOGGER.error("Passed user ID is null. Nulls in indexes are strictly prohibited.");
            throw new AuthException.UserNotFoundException("Unable to load user data. Try again later.");
        }
        final Optional<LocalUserModel> findingUser = localUserRepository.findById(userId);
        if (findingUser.isEmpty()) {
            LOGGER.error("Unable to load user based id req data. Id: {}", userId);
            throw new AuthException.UserNotFoundException("User based passed data not exist.");
        }
        return findingUser.get();
    }

    //------------------------------------------------------------------------------------------------------------------

    void sendEmailMessageForActivateAccount(LocalUserModel user, OtaTokenType tokenType) {
        if (user.getIsActivated()) return;
        final String otaToken = otaTokenUserService.generateAndSaveUserOtaToken(tokenType, user);
        final String token = tokenCreator.createAcitivateServiceViaEmailToken(user, otaToken);
        mailOutService.activateAccount(user.getId(), user.getEmailAddress(), user, token, otaToken);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    void addUserToNewsletter(LocalUserModel userModel, boolean hasNewsletterAccept) {
        if (!hasNewsletterAccept) return;
        final String primaryEmail = userModel.getEmailAddress();
        final Optional<NewsletterEmailModel> newsletterSaveEmailModel = newsletterEmailRepository
                .findNewsletterModelsByEmail(primaryEmail);
        if (newsletterSaveEmailModel.isPresent()) {
            LOGGER.warn("User with followed email has already in newsletter list. Email: {}", primaryEmail);
            return;
        }
        final var newsletterModel = new NewsletterEmailModel(userModel.getFirstName(), primaryEmail);
        userModel.getLocalUserDetails().setHasNewsletterAccept(true);
        newsletterEmailRepository.save(newsletterModel);
    }
}
