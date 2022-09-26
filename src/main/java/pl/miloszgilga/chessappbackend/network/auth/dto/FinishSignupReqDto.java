/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: FinishSignupReqDto.java
 * Last modified: 25/09/2022, 02:23
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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotBlank;

import pl.miloszgilga.chessappbackend.utils.UserGenderSpecific;
import pl.miloszgilga.chessappbackend.validator.annotation.ValidateEnum;
import pl.miloszgilga.chessappbackend.validator.annotation.ValidateCountry;
import pl.miloszgilga.chessappbackend.validator.annotation.ValidateFutureTimeDate;

import static pl.miloszgilga.chessappbackend.validator.RegexPattern.BIRTH_DATE_PATTERN;

//----------------------------------------------------------------------------------------------------------------------

@Data
public class FinishSignupReqDto {

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

    @NotNull(message = "{jpa.validator.newsletterAccept.notNull}")
    private Boolean newsletterAccept;

    @NotBlank(message = "{jpa.validator.jwtToken.notBlank}")
    private String jwtToken;
}
