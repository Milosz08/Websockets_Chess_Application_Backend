/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserManipulatorService.java
 * Last modified: 23.11.2022, 01:48
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

package pl.miloszgilga.chessappbackend.network.user_description;

import org.slf4j.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;
import pl.miloszgilga.chessappbackend.network.user_description.dto.*;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException.UserNotFoundException;

import static pl.miloszgilga.lib.jmpsl.core.StringUtil.*;

//----------------------------------------------------------------------------------------------------------------------

@Service
class UserDescriptionService implements IUserDescriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDescriptionService.class);

    private final ILocalUserDetailsRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    UserDescriptionService(ILocalUserDetailsRepository repository) {
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public AccountDescriptionResDto getLoggedUserAccountDescription(final Long userId) {
        final LocalUserDetailsModel foundUserDetails = repository.findDetailsByUserId(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to get user account description from non-existing user. User id: {}", userId);
            throw new UserNotFoundException("User was not found based on provided data.");
        });
        return new AccountDescriptionResDto(ifNullDefault(foundUserDetails.getAccountDescription(), EMPTY));
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto setLoggerUserAccountDescription(final AccountDescriptionReqDto req, final Long userId) {
        final LocalUserDetailsModel foundUserDetails = repository.findDetailsByUserId(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to get user account description from non-existing user. User id: {}", userId);
            throw new UserNotFoundException("User was not found based on provided data.");
        });
        if (req.getDescription().isEmpty()) {
            foundUserDetails.setAccountDescription(null);
        } else {
            final String parsedNewLinesToHtmlCode = req.getDescription().replaceAll("(\r\n|\n)", "<br>");
            foundUserDetails.setAccountDescription(parsedNewLinesToHtmlCode);
        }
        LOGGER.info("Description for user with id {} was successfully updated.", userId);
        return new SimpleServerMessageDto("Successful set account description.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto removeLoggedUserAccountDescription(Long userId) {
        final LocalUserDetailsModel foundUserDetails = repository.findDetailsByUserId(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to get user account description from non-existing user. User id: {}", userId);
            throw new UserNotFoundException("User was not found based on provided data.");
        });
        foundUserDetails.setAccountDescription(null);
        LOGGER.info("Description for user with id {} was successfully removed.", userId);
        return new SimpleServerMessageDto("Successful removed account description.");
    }
}
