/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserSettingsDataMapper.java
 * Last modified: 08.12.2022, 23:48
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

import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFactory;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

import pl.miloszgilga.lib.jmpsl.core.mapper.MappingFacade;

import pl.miloszgilga.chessappbackend.network.user_settings_data.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.user_settings_data.mapper.UserToPersonalUserDataCustomizer;

//----------------------------------------------------------------------------------------------------------------------

@Component
@Primary
@AllArgsConstructor
public class UserSettingsDataMapper {

    private final MapperFactory mapperFactory;
    private final UserToPersonalUserDataCustomizer userToPersonalUserDataCustomizer;

    //------------------------------------------------------------------------------------------------------------------

    @MappingFacade
    public void userSettingsDataAutoMapperFacadeImplementation() {
        mapperFactory.classMap(LocalUserModel.class, PersonalUserDataResDto.class)
                .field("localUserDetails.hasNewsletterAccept", "hasNewsletterAccept")
                .customize(userToPersonalUserDataCustomizer)
                .byDefault().register();
        mapperFactory.classMap(LocalUserModel.class, ModifyFirstLastNameResDto.class)
                .byDefault().register();
    }
}
