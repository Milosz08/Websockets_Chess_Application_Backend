/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: PasswordMatchValidator.java
 * Last modified: 11/09/2022, 19:15
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

package pl.miloszgilga.chessappbackend.validator.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.*;

import pl.miloszgilga.chessappbackend.exception.custom.PasswordException;
import pl.miloszgilga.chessappbackend.network.auth.dto.SignupViaLocalReqDto;
import pl.miloszgilga.chessappbackend.validator.annotation.ValidatePasswordMatch;

//----------------------------------------------------------------------------------------------------------------------

public class PasswordMatchValidator implements ConstraintValidator<ValidatePasswordMatch, SignupViaLocalReqDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordMatchValidator.class);

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean isValid(final SignupViaLocalReqDto req, final ConstraintValidatorContext context) {
        if (!req.getPassword().equals(req.getPasswordRepeat())) {
            LOGGER.error("Attempt to create new account with different passwords. Request data: {}", req);
            throw new PasswordException
                    .PasswordAndRepeatPassowordNotTheSameException("Password and repeated password should be the same.");
        }
        return true;
    }
}
