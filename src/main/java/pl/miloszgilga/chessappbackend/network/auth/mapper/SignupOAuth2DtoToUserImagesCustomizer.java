/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SignupOAuth2DtoToUserImagesCustomizer.java
 * Last modified: 27/10/2022, 03:02
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

import pl.miloszgilga.lib.jmpsl.oauth2.user.*;
import pl.miloszgilga.lib.jmpsl.oauth2.service.OAuth2RegistrationDataDto;
import pl.miloszgilga.chessappbackend.network.user_images.domain.LocalUserImagesModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class SignupOAuth2DtoToUserImagesCustomizer extends CustomMapper<OAuth2RegistrationDataDto, LocalUserImagesModel> {

    @Override
    public void mapAtoB(OAuth2RegistrationDataDto dto, LocalUserImagesModel userImages, MappingContext context) {
        final OAuth2UserInfoBase userInfo = OAuth2UserInfoFactory.getInstance(dto.getSupplier(), dto.getAttributes());
        userImages.setHasProfileImage(userInfo.getUserImageUrl().isBlank());
        if (userInfo.getUserImageUrl().isEmpty()) return;
        userImages.setProfileImage(userInfo.getUserImageUrl());
        userImages.setHasBannerImage(false);
    }
}
