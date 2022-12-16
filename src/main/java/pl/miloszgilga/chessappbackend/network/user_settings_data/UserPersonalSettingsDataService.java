/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserPersonalSettingsDataService.java
 * Last modified: 08.12.2022, 23:36
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

package pl.miloszgilga.chessappbackend.network.user_settings_data;

import org.slf4j.*;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Date;
import java.time.format.DateTimeFormatter;

import javax.transaction.Transactional;
import pl.miloszgilga.lib.jmpsl.core.TimeUtil;

import pl.miloszgilga.chessappbackend.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.utils.UserGenderSpecific;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.network.user_settings_data.dto.*;

import static java.util.Locale.ENGLISH;
import static java.time.format.TextStyle.SHORT;

import static pl.miloszgilga.chessappbackend.utils.UserGenderSpecific.findGenderByString;

//----------------------------------------------------------------------------------------------------------------------

@Service
class UserPersonalSettingsDataService implements IUserPersonalSettingsDataService {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy").localizedBy(ENGLISH);
    private static final Logger LOGGER = LoggerFactory.getLogger(UserPersonalSettingsDataService.class);

    private final MapperFacade mapperFacade;
    private final ILocalUserRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    UserPersonalSettingsDataService(MapperFacade mapperFacade, ILocalUserRepository repository) {
        this.mapperFacade = mapperFacade;
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public PersonalUserDataResDto getPersonalUserSettingsData(final Long userId) {
        return mapperFacade.map(getLocalUser(userId), PersonalUserDataResDto.class);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public ModifyFirstLastNameResDto modifyFirstLastName(final Long userId, final ModifyFirstLastNameReqDto req) {
        final LocalUserModel userModel = getLocalUser(userId);
        userModel.setFirstName(req.getFirstName());
        userModel.setLastName(req.getLastName());
        final ModifyFirstLastNameResDto res = mapperFacade.map(userModel, ModifyFirstLastNameResDto.class);
        res.setResponseMessage("Successful modified your first and last name.");
        return res;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto deleteFirstLastName(final Long userId) {
        final LocalUserModel userModel = getLocalUser(userId);
        userModel.setFirstName(null);
        userModel.setLastName(null);
        return new SimpleServerMessageDto("Successful removed first and last name.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public ModifyGenderResDto modifyGender(final Long userId, final ModifyGenderReqDto req) {
        final LocalUserModel userModel = getLocalUser(userId);
        userModel.getLocalUserDetails().setGender(findGenderByString(req.getGender()));
        return ModifyGenderResDto.builder()
                .gender(UserGenderSpecific.findGenderAndReturnTuple(req.getGender()))
                .responseMessage("Successful modified your gender info.")
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto deleteGender(final Long userId) {
        final LocalUserModel userModel = getLocalUser(userId);
        userModel.getLocalUserDetails().setGender(null);
        return new SimpleServerMessageDto("Successful removed your gender info.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public ModifyCountryResDto modifyCountry(final Long userId, final ModifyCountryReqDto req) {
        final LocalUserModel userModel = getLocalUser(userId);
        userModel.getLocalUserDetails().setCountry(req.getCountryName());
        final String countryName = userModel.getLocalUserDetails().getCountry();
        return ModifyCountryResDto.builder()
                .countryName(new SimpleTupleDto<>(countryName, countryName))
                .responseMessage("Successful modified your country info.")
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto deleteCountry(final Long userId) {
        final LocalUserModel userModel = getLocalUser(userId);
        userModel.getLocalUserDetails().setCountry(null);
        return new SimpleServerMessageDto("Successful removed your country info.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public ModifyBirthDateResDto modifyBirthDate(final Long userId, final ModifyBirthDateReqDto req) {
        final LocalUserModel userModel = getLocalUser(userId);
        final Date modifyDate = TimeUtil.deserialize(req.getBirthDate()).orElse(new Date());
        userModel.getLocalUserDetails().setBirthDate(modifyDate);
        final LocalDate date = modifyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ModifyBirthDateResDto.builder()
                .birthDateString(date.format(DTF))
                .birthDateDay(new SimpleTupleDto<>(date.getDayOfMonth(), String.format("%01d", date.getDayOfMonth())))
                .birthDateMonth(new SimpleTupleDto<>(date.getMonthValue(), date.getMonth().getDisplayName(SHORT, ENGLISH)))
                .birthDateYear(new SimpleTupleDto<>(date.getYear(), Integer.toString(date.getYear())))
                .responseMessage("Successful modified your birth date info.")
                .build();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public SimpleServerMessageDto deleteBirthDate(final Long userId) {
        final LocalUserModel userModel = getLocalUser(userId);
        userModel.getLocalUserDetails().setBirthDate(null);
        return new SimpleServerMessageDto("Successful removed your birth date info.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    LocalUserModel getLocalUser(final Long userId) {
        return repository.findUserById(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to get user account details from non-existing user. User id: {}", userId);
            throw new AuthException.UserNotFoundException("User was not found based on provided data.");
        });
    }
}
