/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OtaTokenMultipleEmailsReqDto.java
 * Last modified: 01/10/2022, 12:55
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

package pl.miloszgilga.chessappbackend.network.ota_token.dto;

import lombok.Data;

import java.util.Set;
import javax.validation.constraints.*;

import pl.miloszgilga.chessappbackend.validator.annotation.ValidateMultipleEmails;

import static pl.miloszgilga.chessappbackend.validator.RegexPattern.OTA_TOKEN_PATTERN;

//----------------------------------------------------------------------------------------------------------------------

@Data
public class OtaTokenMultipleEmailsReqDto {

    @NotBlank(message = "{jpa.validator.otaToken.notBlank}")
    @Pattern(regexp = OTA_TOKEN_PATTERN, message = "{jpa.validator.otaToken.regex}")
    private String token;

    //------------------------------------------------------------------------------------------------------------------

    @NotEmpty(message = "{jpa.validator.multipleEmails.notEmpty}")
    @ValidateMultipleEmails(message = "{jpa.validator.multipleEmails.malformedOrNotExist}")
    private Set<String> emailAddresses;
}
