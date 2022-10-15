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

import ma.glasnost.orika.*;
import org.springframework.stereotype.Component;

import java.util.*;

import pl.miloszgilga.lib.jmpsl.util.StringUtil;

import pl.miloszgilga.chessappbackend.network.auth.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class UserToAttemptSignupResDtoCustomizer extends CustomMapper<LocalUserModel, SuccessedAttemptToFinishSignupResDto> {

    @Override
    public void mapAtoB(LocalUserModel user, SuccessedAttemptToFinishSignupResDto resDto, MappingContext context) {
        resDto.setFullName(StringUtil.capitalize(user.getFirstName()) + " " + StringUtil.capitalize(user.getLastName()));
        resDto.setInitials(StringUtil.initials(user.getFirstName(), user.getLastName()));
        resDto.setUserEmailAddresses(generatedHashedEmails(user));
    }

    //------------------------------------------------------------------------------------------------------------------

    private Set<EmailHashWithNormalDto> generatedHashedEmails(LocalUserModel user) {
        final Set<EmailHashWithNormalDto> hashedEmails = new HashSet<>();
        if (user.getLocalUserDetails().getSecondEmailAddress() != null) {
            final String hashed = StringUtil.hashValue(user.getLocalUserDetails().getSecondEmailAddress());
            hashedEmails.add(new EmailHashWithNormalDto(hashed, user.getLocalUserDetails().getSecondEmailAddress()));
        }
        hashedEmails.add(new EmailHashWithNormalDto(StringUtil.hashValue(user.getEmailAddress()), user.getEmailAddress()));
        return hashedEmails;
    }
}
