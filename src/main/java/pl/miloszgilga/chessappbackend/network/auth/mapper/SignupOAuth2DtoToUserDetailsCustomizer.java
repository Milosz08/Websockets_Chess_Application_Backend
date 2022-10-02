/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SignupOAuth2DtoToUserDetailsCustomizer.java
 * Last modified: 28/09/2022, 11:49
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

import pl.miloszgilga.chessappbackend.oauth.user_info.*;
import pl.miloszgilga.chessappbackend.oauth.dto.OAuth2RegistrationData;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserDetailsModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class SignupOAuth2DtoToUserDetailsCustomizer extends CustomMapper<OAuth2RegistrationData, LocalUserDetailsModel> {

    private final OAuth2UserInfoFactory userInfoFactory;

    //------------------------------------------------------------------------------------------------------------------

    public SignupOAuth2DtoToUserDetailsCustomizer(OAuth2UserInfoFactory userInfoFactory) {
        this.userInfoFactory = userInfoFactory;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void mapAtoB(OAuth2RegistrationData data, LocalUserDetailsModel detModel, MappingContext context) {
        final OAuth2UserInfo userInfo = userInfoFactory.getOAuth2UserInfo(data.getSupplier(), data.getAttributes());
        detModel.setHasPhoto(!userInfo.getUserImageUrl().isEmpty());
        detModel.setHasNewsletterAccept(detModel.getHasNewsletterAccept() != null && detModel.getHasNewsletterAccept());
        detModel.setIsDataFilled(detModel.getIsDataFilled() != null && detModel.getIsDataFilled());
        if (userInfo.getUserImageUrl().isEmpty()) return;
        detModel.setPhotoEmbedLink(userInfo.getUserImageUrl());
    }
}
