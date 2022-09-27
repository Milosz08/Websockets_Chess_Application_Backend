/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SimpleOtaTokenReqDto.java
 * Last modified: 26/09/2022, 23:13
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

package pl.miloszgilga.chessappbackend.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotBlank;

import static pl.miloszgilga.chessappbackend.validator.RegexPattern.OTA_TOKEN_PATTERN;

//----------------------------------------------------------------------------------------------------------------------

@Data
public class SimpleOtaTokenReqDto {

    @NotBlank(message = "{jpa.validator.otaToken.notBlank}")
    @Pattern(regexp = OTA_TOKEN_PATTERN, message = "{jpa.validator.otaToken.regex}")
    private String token;

    @NotBlank(message = "{jpa.validator.emailAddress.notBlank}")
    @Email(message = "{jpa.validator.emailAddress.pattern}")
    @Size(max = 100, message = "{jpa.validator.emailAddress.size}")
    private String emailAddress;
}
