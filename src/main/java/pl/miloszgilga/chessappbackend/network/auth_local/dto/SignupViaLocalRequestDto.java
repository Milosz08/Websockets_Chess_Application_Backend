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

package pl.miloszgilga.chessappbackend.network.auth_local.dto;

import lombok.Data;

import javax.validation.constraints.*;

import pl.miloszgilga.chessappbackend.validator.annotation.*;
import pl.miloszgilga.chessappbackend.utils.UserGenderSpecific;

import static pl.miloszgilga.chessappbackend.validator.RegexPattern.*;

//----------------------------------------------------------------------------------------------------------------------

@Data
@ValidatePasswordMatch
@ValidateSecondEmailNotRepeat
public class SignupViaLocalRequestDto {

    @NotBlank(message = "You should provide nickname.")
    @Pattern(regexp = NICKNAME_PATTERN, message = "Illegal characters in nickname form field.")
    private String nickname;

    @NotBlank(message = "You should provide first name.")
    @Pattern(regexp = NAME_SURNAME_PATTERN, message = "Illegal characters in first name form field.")
    private String firstName;

    @NotBlank(message = "You should provide last name.")
    @Pattern(regexp = NAME_SURNAME_PATTERN, message = "Illegal characters in last name form field.")
    private String lastName;

    @NotBlank(message = "You should provide email address.")
    @Email(message = "Passed email address is not valid.")
    @Size(max = 100, message = "Email address must be shorter from 100 characters.")
    private String emailAddress;

    @NotNull(message = "Second email address might be empty, but not null.")
    @Email(message = "Passed email address is not valid.")
    @Size(max = 100, message = "Email address must be shorter from 100 characters.")
    private String secondEmailAddress;

    @NotBlank(message = "You should provide birth date.")
    @Pattern(regexp = BIRTH_DATE_PATTERN, message = "Invalid format. Date format should be: DD/MM/YYYY")
    @ValidateFutureTimeDate(message = "Provided birth date should be before the current date")
    private String birthDate;

    @NotBlank(message = "You should provide country name.")
    @ValidateCountry(message = "Passed country name not exist or is invalid.")
    private String countryName;

    @NotBlank(message = "You should provide user gender data.")
    @ValidateEnum(enumClazz = UserGenderSpecific.class, message = "Available gender types: [male, female, other]")
    private String gender;

    @NotBlank(message = "You should provide password.")
    @Pattern(regexp = PASSWORD_PATTERN,
            message = "Password must have at least one big letter, one digit and one special character."
    )
    private String password;

    @NotBlank(message = "You should provide repeat password.")
    private String passwordRepeat;

    @NotNull(message = "Newsletter allows field might be empty but not null.")
    private boolean newsletterAccept;
}
