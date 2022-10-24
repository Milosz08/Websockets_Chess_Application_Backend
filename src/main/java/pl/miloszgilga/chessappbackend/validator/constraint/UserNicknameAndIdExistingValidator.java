/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserNicknameExistingValidator.java
 * Last modified: 07/10/2022, 21:08
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
import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.network.expose_static_data.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.ILocalUserRepository;
import pl.miloszgilga.chessappbackend.validator.annotation.ValidateUserNicknameAndIdExisting;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class UserNicknameAndIdExistingValidator
        implements ConstraintValidator<ValidateUserNicknameAndIdExisting, RememberAccountsDataReqDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserNicknameAndIdExistingValidator.class);
    private final ILocalUserRepository repository;

    public UserNicknameAndIdExistingValidator(ILocalUserRepository repository) {
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean isValid(RememberAccountsDataReqDto accounts, ConstraintValidatorContext context) {
        for (final RememberAccountReqDto user : accounts.getAccounts()) {
            if (!repository.checkIfUserByIdAndNicknameExist(user.getUserId(), user.getUserLogin())) {
                LOGGER.error("User with id: {} and nickname: {} does not exist.", user.getUserId(), user.getUserLogin());
                return false;
            }
        }
        return true;
    }
}
