/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: NicknameNotRepeatValidator.java
 * Last modified: 23/09/2022, 01:14
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import pl.miloszgilga.chessappbackend.network.auth_local.domain.ILocalUserRepository;
import pl.miloszgilga.chessappbackend.validator.annotation.ValidateNicknameNotRepeat;

//----------------------------------------------------------------------------------------------------------------------

public class NicknameNotRepeatValidator implements ConstraintValidator<ValidateNicknameNotRepeat, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NicknameNotRepeatValidator.class);

    private final ILocalUserRepository userRepository;

    public NicknameNotRepeatValidator(ILocalUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        if (userRepository.checkIfUserByNicknameExist(nickname)) {
            LOGGER.error("Attempt to create user with already exist nickname. Nickname: {}", nickname);
            return false;
        }
        return true;
    }
}
