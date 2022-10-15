/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserToChangePasswordUserDetailsCustomizer.java
 * Last modified: 09/10/2022, 19:56
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

package pl.miloszgilga.chessappbackend.network.renew_credentials.mapper;

import ma.glasnost.orika.*;
import org.springframework.stereotype.Component;

import pl.miloszgilga.lib.jmpsl.util.StringUtil;

import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.renew_credentials.dto.ChangePasswordUserDetailsResDto;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class UserToChangePasswordUserDetailsCustomizer extends CustomMapper<LocalUserModel, ChangePasswordUserDetailsResDto> {

    private final StringManipulator manipulator;

    //----------------------------------------------------------------------------------------------------------------------

    public UserToChangePasswordUserDetailsCustomizer(StringManipulator manipulator) {
        this.manipulator = manipulator;
    }

    //----------------------------------------------------------------------------------------------------------------------

    @Override
    public void mapAtoB(LocalUserModel userModel, ChangePasswordUserDetailsResDto resDto, MappingContext context) {
        resDto.setInitials(StringUtil.initials(userModel.getFirstName(), userModel.getLastName()));
        resDto.setFullName(manipulator.generateFullName(userModel));
    }
}
