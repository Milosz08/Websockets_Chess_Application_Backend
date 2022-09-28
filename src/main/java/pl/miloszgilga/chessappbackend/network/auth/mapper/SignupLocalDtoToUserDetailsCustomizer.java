/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SignupLocalDtoToUserDetailsCustomizer.java
 * Last modified: 28/09/2022, 10:17
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

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.utils.UserGenderSpecific;
import pl.miloszgilga.chessappbackend.network.auth.dto.SignupViaLocalReqDto;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserDetailsModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class SignupLocalDtoToUserDetailsCustomizer extends CustomMapper<SignupViaLocalReqDto, LocalUserDetailsModel> {

    @Override
    public void mapAtoB(SignupViaLocalReqDto reqDto, LocalUserDetailsModel userDetailsModel, MappingContext context) {
        userDetailsModel.setGender(UserGenderSpecific.findGenderByString(reqDto.getGender()));
        userDetailsModel.setIsDataFilled(true);
        userDetailsModel.setHasPhoto(false);
    }
}
