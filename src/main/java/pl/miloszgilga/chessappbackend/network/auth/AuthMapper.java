/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AuthMapper.java
 * Last modified: 28/09/2022, 12:03
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

package pl.miloszgilga.chessappbackend.network.auth;

import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFactory;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

import pl.miloszgilga.lib.jmpsl.util.mapper.MappingFacade;
import pl.miloszgilga.lib.jmpsl.oauth2.service.OAuth2RegistrationDataDto;
import pl.miloszgilga.chessappbackend.network.user_images.domain.LocalUserImagesModel;

import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.mapper.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;

import static pl.miloszgilga.lib.jmpsl.util.mapper.converter.Converter.*;
import static pl.miloszgilga.chessappbackend.mapper.Converter.HASH_PASSWORD;

//----------------------------------------------------------------------------------------------------------------------

@Component
@Primary
@AllArgsConstructor
public class AuthMapper {

    private final MapperFactory mapperFactory;
    private final SignupLocalDtoToUserCustomizer signupLocalDtoToUserCustomizer;
    private final SignupLocalDtoToUserDetailsCustomizer signupLocalDtoToUserDetailsCustomizer;
    private final UserToAttemptSignupResDtoCustomizer userToAttemptSignupResDtoCustomizer;
    private final SignupOAuth2DtoToUserImagesCustomizer signupOAuth2DtoToUserImagesCustomizer;
    private final SignupOAuth2DtoToUserCustomizer signupOAuth2DtoToUserCustomizer;
    private final SignupOAuth2DtoToUserDetailsCustomizer signupOAuth2DtoToUserDetailsCustomizer;
    private final UserToSuccessedLoginResDtoCustomizer userToSuccessedLoginResDtoCustomizer;
    private final FilledSignupDataToUserDetailsCustomizer filledSignupDataToUserDetailsCustomizer;

    //------------------------------------------------------------------------------------------------------------------

    @MappingFacade
    public void authAutoMapperFacadeImplementation() {
        mapperFactory.classMap(SignupViaLocalReqDto.class, LocalUserModel.class)
                .fieldMap("nickname").converter(CHANGE_ALL_LETTERS_TO_LOWER.getName()).add()
                .fieldMap("firstName").converter(CAPITALIZED_FIRST_LETTER.getName()).add()
                .fieldMap("lastName").converter(CAPITALIZED_FIRST_LETTER.getName()).add()
                .fieldMap("emailAddress").converter(CHANGE_ALL_LETTERS_TO_LOWER.getName()).add()
                .fieldMap("password").converter(HASH_PASSWORD.getName()).add()
                .customize(signupLocalDtoToUserCustomizer)
                .byDefault().register();

        mapperFactory.classMap(SignupViaLocalReqDto.class, LocalUserDetailsModel.class)
                .exclude("gender")
                .customize(signupLocalDtoToUserDetailsCustomizer)
                .field("countryName", "country")
                .fieldMap("birthDate").converter(DATE_FROM_STRING_TO_OBJECT.getName()).add()
                .fieldMap("secondEmailAddress").converter(INSERT_NULL_IF_STRING_IS_EMPTY.getName()).add()
                .byDefault().register();

        mapperFactory.classMap(LocalUserModel.class, SuccessedAttemptToFinishSignupResDto.class)
                .field("localUserImages.avatarImage", "photoUrl")
                .field("localUserDetails.isDataFilled", "isDataFilled")
                .field("emailAddress", "userPrimaryEmailAddress")
                .customize(userToAttemptSignupResDtoCustomizer)
                .byDefault()
                .register();

        mapperFactory.classMap(OAuth2RegistrationDataDto.class, LocalUserModel.class)
                .customize(signupOAuth2DtoToUserCustomizer)
                .byDefault()
                .register();

        mapperFactory.classMap(OAuth2RegistrationDataDto.class, LocalUserDetailsModel.class)
                .customize(signupOAuth2DtoToUserDetailsCustomizer)
                .byDefault()
                .register();

        mapperFactory.classMap(FinishSignupReqDto.class, LocalUserDetailsModel.class)
                .exclude("gender")
                .customize(filledSignupDataToUserDetailsCustomizer)
                .fieldMap("birthDate").converter(DATE_FROM_STRING_TO_OBJECT.getName()).add()
                .field("countryName", "country")
                .byDefault()
                .register();

        mapperFactory.classMap(LocalUserModel.class, SuccessedLoginResDto.class)
                .field("id", "userId")
                .field("localUserImages.hasAvatarImage", "ifHasPhoto")
                .field("localUserImages.avatarImage", "photoUrl")
                .customize(userToSuccessedLoginResDtoCustomizer)
                .byDefault()
                .register();
    }
}
