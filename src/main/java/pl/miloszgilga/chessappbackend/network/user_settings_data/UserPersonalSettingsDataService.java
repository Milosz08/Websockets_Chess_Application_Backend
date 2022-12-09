/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserPersonalSettingsDataService.java
 * Last modified: 08.12.2022, 23:36
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

package pl.miloszgilga.chessappbackend.network.user_settings_data;

import org.slf4j.*;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.network.user_settings_data.dto.PersonalUserDataResDto;

//----------------------------------------------------------------------------------------------------------------------

@Service
class UserPersonalSettingsDataService implements IUserPersonalSettingsDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPersonalSettingsDataService.class);

    private final MapperFacade mapperFacade;
    private final ILocalUserRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    UserPersonalSettingsDataService(MapperFacade mapperFacade, ILocalUserRepository repository) {
        this.mapperFacade = mapperFacade;
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public PersonalUserDataResDto getPersonalUserSettingsData(final Long userId) {
        final LocalUserModel foundUser = repository.findUserById(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to get user account details from non-existing user. User id: {}", userId);
            throw new AuthException.UserNotFoundException("User was not found based on provided data.");
        });
        return mapperFacade.map(foundUser, PersonalUserDataResDto.class);
    }
}
