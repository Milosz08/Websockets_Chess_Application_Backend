/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserToPersonalUserDataCustomizer.java
 * Last modified: 08.12.2022, 23:51
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

package pl.miloszgilga.chessappbackend.network.user_settings_data.mapper;

import ma.glasnost.orika.*;
import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.dto.*;
import pl.miloszgilga.chessappbackend.utils.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.network.user_settings_data.dto.PersonalUserDataResDto;

import java.time.*;
import java.time.format.DateTimeFormatter;

import static java.util.Locale.ENGLISH;
import static java.time.format.TextStyle.SHORT;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class UserToPersonalUserDataCustomizer extends CustomMapper<LocalUserModel, PersonalUserDataResDto> {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy").localizedBy(ENGLISH);

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void mapAtoB(LocalUserModel userModel, PersonalUserDataResDto resDto, MappingContext context) {
        final LocalUserDetailsModel details = userModel.getLocalUserDetails();
        final LocalDate date = details.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final UserBirthDateDto birthDateDto = UserBirthDateDto.builder()
                .birthDateString(date.format(DTF))
                .birthDateDay(new SimpleTupleDto<>(date.getDayOfMonth(), String.format("%01d", date.getDayOfMonth())))
                .birthDateMonth(new SimpleTupleDto<>(date.getMonthValue(), date.getMonth().getDisplayName(SHORT, ENGLISH)))
                .birthDateYear(new SimpleTupleDto<>(date.getYear(), Integer.toString(date.getYear())))
                .build();
        resDto.setGender(UserGenderSpecific.findGenderAndReturnTuple(details.getGender().getGender()));
        resDto.setCountryName(new SimpleTupleDto<>(details.getCountry(), details.getCountry()));
        resDto.setBirthDate(birthDateDto);
    }
}
