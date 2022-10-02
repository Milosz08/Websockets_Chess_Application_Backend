/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LocalUserDetailService.java
 * Last modified: 19/09/2022, 21:51
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.*;

import javax.transaction.Transactional;

import java.util.Optional;

import pl.miloszgilga.chessappbackend.oauth.AuthUser;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.token.dto.UserVerficationClaims;

//----------------------------------------------------------------------------------------------------------------------

@Service
public class AuthUserDetailService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserDetailService.class);

    private final ILocalUserRepository repository;
    private final SecurityHelper securityHelper;

    //------------------------------------------------------------------------------------------------------------------

    public AuthUserDetailService(ILocalUserRepository repository, SecurityHelper securityHelper) {
        this.repository = repository;
        this.securityHelper = securityHelper;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public AuthUser loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        final Optional<LocalUserModel> foundUserByEmail = repository.findUserByEmailAddress(emailOrNickname);
        if (foundUserByEmail.isPresent()) {
            return securityHelper.generateSecurityUserByModelData(foundUserByEmail.get());
        }
        final Optional<LocalUserModel> foundUserByNickname = repository.findUserByNickname(emailOrNickname);
        if (foundUserByNickname.isEmpty()) {
            LOGGER.error("Unable to load user with credentials data (nickname/email): {}", emailOrNickname);
            throw new UsernameNotFoundException("Unable to load user via UserDetailsService class.");
        }
        return securityHelper.generateSecurityUserByModelData(foundUserByNickname.get());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    public AuthUser loadUserByNicknameEmailAndId(UserVerficationClaims cred) {
        final Optional<LocalUserModel> foundUserByCredentials = repository
                .findUserByEmailAddressNicknameAndId(cred.getEmailAddress(), cred.getNickname(), cred.getId());
        if (foundUserByCredentials.isPresent()) {
            return securityHelper.generateSecurityUserByModelData(foundUserByCredentials.get());
        }
        LOGGER.error("Unable to load user with credentials data: {}", cred);
        throw new AuthException.UserNotFoundException("User was not found based on the data provided.");
    }
}
