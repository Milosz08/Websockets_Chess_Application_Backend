/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MultipleEmailsValidator.java
 * Last modified: 01/10/2022, 12:57
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

import java.util.Set;
import java.util.regex.*;

import pl.miloszgilga.chessappbackend.network.auth.domain.ILocalUserRepository;
import pl.miloszgilga.chessappbackend.validator.annotation.ValidateMultipleEmails;

import static pl.miloszgilga.chessappbackend.validator.RegexPattern.EMAIL_PATTERN;

//----------------------------------------------------------------------------------------------------------------------

public class MultipleEmailsValidator implements ConstraintValidator<ValidateMultipleEmails, Set<String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipleEmailsValidator.class);
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final int MAX_EMAIL_ADDRESSES = 2;

    private final ILocalUserRepository userRepository;

    //------------------------------------------------------------------------------------------------------------------

    public MultipleEmailsValidator(ILocalUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean isValid(Set<String> emails, ConstraintValidatorContext context) {
        if (emails.size() > MAX_EMAIL_ADDRESSES) {
            LOGGER.error("Attempt to pass too much email addresses. Max addresses count is: {}", MAX_EMAIL_ADDRESSES);
            return false;
        }
        final boolean isNotExist = emails.stream().noneMatch(userRepository::checkIfUserHasEmailOrSecondEmail);
        final boolean isInvalid = emails.stream().noneMatch(this::validateEmail);
        if (isNotExist || isInvalid) {
            LOGGER.error("Attempt to pass ota token with non existing email value or malformed email data");
            return false;
        }
        return true;
    }

    //------------------------------------------------------------------------------------------------------------------

    private boolean validateEmail(String email) {
        final Matcher matcher = EMAIL_REGEX.matcher(email);
        return matcher.find();
    }
}
