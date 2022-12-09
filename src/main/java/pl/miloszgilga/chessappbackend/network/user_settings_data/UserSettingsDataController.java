/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserSettingsDataController.java
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

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import pl.miloszgilga.lib.jmpsl.security.user.CurrentUser;
import pl.miloszgilga.lib.jmpsl.oauth2.user.OAuth2UserExtender;

import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.user_settings_data.dto.PersonalUserDataResDto;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(USER_SETTINGS_DATA)
public class UserSettingsDataController {

    private final UserPersonalSettingsDataService personalSettingsDataService;

    //------------------------------------------------------------------------------------------------------------------

    public UserSettingsDataController(UserPersonalSettingsDataService personalSettingsDataService) {
        this.personalSettingsDataService = personalSettingsDataService;
    }

    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(PERSONAL_SETTINGS_DATA)
    ResponseEntity<PersonalUserDataResDto> getPersonalUserSettingsData(@CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(personalSettingsDataService.getPersonalUserSettingsData(userId), HttpStatus.OK);
    }
}
