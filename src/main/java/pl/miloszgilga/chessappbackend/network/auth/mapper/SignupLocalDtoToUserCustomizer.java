/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SignupLocalDtoToUserCustomizer.java
 * Last modified: 28/09/2022, 10:30
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

package pl.miloszgilga.chessappbackend.network.auth.mapper;

import ma.glasnost.orika.*;
import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.network.auth.AuthServiceHelper;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth.dto.SignupViaLocalReqDto;

import static pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier.LOCAL;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class SignupLocalDtoToUserCustomizer extends CustomMapper<SignupViaLocalReqDto, LocalUserModel> {

    private final AuthServiceHelper helper;

    //------------------------------------------------------------------------------------------------------------------

    public SignupLocalDtoToUserCustomizer(AuthServiceHelper helper) {
        this.helper = helper;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void mapAtoB(SignupViaLocalReqDto reqDto, LocalUserModel userModel, MappingContext context) {
        userModel.setCredentialsSupplier(LOCAL);
        userModel.setIsActivated(false);
        userModel.setIsBlocked(false);
        userModel.setRoles(helper.findAndGenerateUserRole());
    }
}
