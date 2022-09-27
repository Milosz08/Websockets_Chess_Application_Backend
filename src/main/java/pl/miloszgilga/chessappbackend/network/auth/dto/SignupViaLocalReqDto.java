/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: RegisterRequestDto.java
 * Last modified: 10/09/2022, 19:20
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

package pl.miloszgilga.chessappbackend.network.auth.dto;

import lombok.Data;

import javax.validation.constraints.*;

import pl.miloszgilga.chessappbackend.validator.annotation.*;
import pl.miloszgilga.chessappbackend.utils.UserGenderSpecific;

import static pl.miloszgilga.chessappbackend.validator.RegexPattern.*;

//----------------------------------------------------------------------------------------------------------------------

@Data
@ValidatePasswordMatch(message = "{jpa.validator.passwordsMismatch.custom}")
@ValidateSecondEmailNotRepeat(message = "{jpa.validator.emailsAreTheSame.custom}")
public class SignupViaLocalReqDto {

    @ValidateNicknameNotRepeat(message = "{jpa.validator.nickname.notRepeat}")
    @NotBlank(message = "{jpa.validator.nickname.notBlank}")
    @Pattern(regexp = NICKNAME_PATTERN, message = "{jpa.validator.nickname.regex}")
    private String nickname;

    @NotBlank(message = "{jpa.validator.firstName.notBlank}")
    @Pattern(regexp = NAME_SURNAME_PATTERN, message = "{jpa.validator.firstName.regex}")
    private String firstName;

    @NotBlank(message = "{jpa.validator.lastName.notBlank}")
    @Pattern(regexp = NAME_SURNAME_PATTERN, message = "{jpa.validator.lastName.regex}")
    private String lastName;

    @NotBlank(message = "{jpa.validator.emailAddress.notBlank}")
    @Email(message = "{jpa.validator.emailAddress.pattern}")
    @Size(max = 100, message = "{jpa.validator.emailAddress.size}")
    @ValidateEmailAlreadyExist(message = "{jpa.validator.emailAddress.notRepeat}")
    private String emailAddress;

    @NotNull(message = "{jpa.validator.secondEmailAddress.notNull}")
    @Email(message = "{jpa.validator.secondEmailAddress.pattern}")
    @Size(max = 100, message = "{jpa.validator.secondEmailAddress.size}")
    private String secondEmailAddress;

    @NotBlank(message = "{jpa.validator.birthDate.notBlank}")
    @Pattern(regexp = BIRTH_DATE_PATTERN, message = "{jpa.validator.birthDate.regex}")
    @ValidateFutureTimeDate(message = "{jpa.validator.birthDate.futureDate}")
    private String birthDate;

    @NotBlank(message = "{jpa.validator.countryName.notBlank}")
    @ValidateCountry(message = "{jpa.validator.countryName.notExist}")
    private String countryName;

    @NotBlank(message = "{jpa.validator.gender.notBlank}")
    @ValidateEnum(enumClazz = UserGenderSpecific.class, message = "{jpa.validator.gender.notExist}")
    private String gender;

    @NotBlank(message = "{jpa.validator.password.notBlank}")
    @Pattern(regexp = PASSWORD_PATTERN, message = "{jpa.validator.password.regex}")
    private String password;

    @NotBlank(message = "{jpa.validator.passwordRepeat.notBlank}")
    private String passwordRepeat;

    @NotNull(message = "{jpa.validator.hasNewsletterAccept.notNull}")
    private Boolean hasNewsletterAccept;
}
