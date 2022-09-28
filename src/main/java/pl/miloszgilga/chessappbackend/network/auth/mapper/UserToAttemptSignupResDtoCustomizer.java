/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserToAttemptSignupResDtoCustomizer.java
 * Last modified: 28/09/2022, 10:18
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

package pl.miloszgilga.chessappbackend.network.auth.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.HashSet;

import pl.miloszgilga.chessappbackend.security.SecurityHelper;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth.dto.SuccessedAttemptToFinishSignupResDto;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class UserToAttemptSignupResDtoCustomizer extends CustomMapper<LocalUserModel, SuccessedAttemptToFinishSignupResDto> {

    private static final char DELIMITER_HASH = '*';

    private final StringManipulator manipulator;
    private final SecurityHelper securityHelper;

    public UserToAttemptSignupResDtoCustomizer(StringManipulator manipulator, SecurityHelper securityHelper) {
        this.manipulator = manipulator;
        this.securityHelper = securityHelper;
    }

    @Override
    public void mapAtoB(LocalUserModel user, SuccessedAttemptToFinishSignupResDto resDto, MappingContext context) {
        resDto.setFullName(manipulator.capitalised(user.getFirstName()) + " " + manipulator.capitalised(user.getLastName()));
        resDto.setInitials(manipulator.generateInitials(user));
        resDto.setHashedEmails(generatedHashedEmails(user));
    }

    private Set<String> generatedHashedEmails(LocalUserModel user) {
        final Set<String> hashedEmails = new HashSet<>();
        if (user.getLocalUserDetails().getSecondEmailAddress() != null) {
            hashedEmails.add(hashValue(user.getLocalUserDetails().getSecondEmailAddress()));
        }
        hashedEmails.add(hashValue(user.getEmailAddress()));
        return hashedEmails;
    }

    private String hashValue(String value) {
        return securityHelper.hashingStringValue(value, DELIMITER_HASH);
    }
}
