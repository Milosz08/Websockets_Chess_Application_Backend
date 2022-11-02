/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: RenewCredentialsServiceHelper.java
 * Last modified: 09/10/2022, 16:50
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

package pl.miloszgilga.chessappbackend.network.renew_credentials;

import org.slf4j.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import pl.miloszgilga.lib.jmpsl.core.StringUtil;

import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenVerificator;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.network.auth.dto.EmailHashWithNormalDto;
import pl.miloszgilga.chessappbackend.token.dto.ActivateServiceViaEmailTokenClaims;

import static java.util.Objects.isNull;

//----------------------------------------------------------------------------------------------------------------------

@Component
class RenewCredentialsServiceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RenewCredentialsServiceHelper.class);

    private final JsonWebTokenVerificator tokenVerificator;
    private final ILocalUserRepository localUserRepository;

    //------------------------------------------------------------------------------------------------------------------

    RenewCredentialsServiceHelper(JsonWebTokenVerificator tokenVerificator, ILocalUserRepository localUserRepository) {
        this.tokenVerificator = tokenVerificator;
        this.localUserRepository = localUserRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    Set<String> extractUserEmails(LocalUserModel userModel) {
        final Set<String> emailAddresses = new HashSet<>();
        emailAddresses.add(userModel.getEmailAddress());
        if (!isNull(userModel.getLocalUserDetails().getSecondEmailAddress())) {
            emailAddresses.add(userModel.getLocalUserDetails().getSecondEmailAddress());
        }
        return emailAddresses;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    LocalUserModel findUserByJwtToken(final String jwtToken) {
        final ActivateServiceViaEmailTokenClaims claims = tokenVerificator.validateActivatingServiceViaEmail(jwtToken);
        return localUserRepository.findUserById(claims.getUserId()).orElseThrow(() -> {
            LOGGER.error("Attempt to change password for non existing user.");
            throw new AuthException.UserNotFoundException("User based passed data not exist. Try with another token.");
        });
    }

    //------------------------------------------------------------------------------------------------------------------

    Set<EmailHashWithNormalDto> hashUserEmails(LocalUserModel userModel) {
        return extractUserEmails(userModel).stream()
                .map(e -> new EmailHashWithNormalDto(StringUtil.hashValue(e), e))
                .collect(Collectors.toSet());
    }
}
