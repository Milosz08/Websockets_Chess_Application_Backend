/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ExposeStaticDataService.java
 * Last modified: 15/09/2022, 17:35
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

package pl.miloszgilga.chessappbackend.network.expose_static_data;

import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.*;

import pl.miloszgilga.lib.jmpsl.util.TimeUtil;

import pl.miloszgilga.chessappbackend.dto.SimpleTupleDto;
import pl.miloszgilga.chessappbackend.loader.gender_data.*;
import pl.miloszgilga.chessappbackend.loader.country_data.*;
import pl.miloszgilga.chessappbackend.loader.calendar_data.*;

import pl.miloszgilga.chessappbackend.network.expose_static_data.dto.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.auth.domain.ILocalUserRepository;

//----------------------------------------------------------------------------------------------------------------------

@Service
class ExposeStaticDataService implements IExposeStaticDataService {

    private final MapperFacade mapperFacade;
    private final GenderPropertiesLoader genderLoader;
    private final CountryPropertiesLoader countryLoader;
    private final CalendarPropertiesLoader calendarLoader;
    private final ILocalUserRepository localUserRepository;

    //------------------------------------------------------------------------------------------------------------------

    ExposeStaticDataService(MapperFacade mapperFacade, GenderPropertiesLoader genderLoader,
                            CalendarPropertiesLoader calendarLoader, CountryPropertiesLoader countryLoader,
                            ILocalUserRepository localUserRepository) {
        this.mapperFacade = mapperFacade;
        this.genderLoader = genderLoader;
        this.calendarLoader = calendarLoader;
        this.countryLoader = countryLoader;
        this.localUserRepository = localUserRepository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public SignupCalendarDataResDto getSignupCalendarData() {
        final CalendarPropertiesModel loadedData = calendarLoader.getLoadedData();
        return new SignupCalendarDataResDto(
                generateData(1, 32, d -> String.format("%01d", d)),
                generateData(1, loadedData.getMonthsShort().size() + 1, m -> loadedData.getMonthsShort().get(m - 1)),
                generateData(1900, TimeUtil.currYearMinusAcceptableAge(10), Integer::toString)
        );
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public SignupGenderDataResDto getSignupGenderData() {
        final GenderPropertiesModel loadedData = genderLoader.getLoadedData();
        return new SignupGenderDataResDto(loadedData.getGenders());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public SignupCountryDataResDto getSignupCountryData() {
        final CountryPropertiesModel loadedData = countryLoader.getLoadedData();
        final List<SimpleTupleDto<String>> allCountryNames = loadedData.getCountries().stream()
                .map(x -> new SimpleTupleDto<>(x.toLowerCase(Locale.ROOT), x))
                .sorted()
                .collect(Collectors.toList());
        return new SignupCountryDataResDto(allCountryNames);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public Set<RememberAccountResDto> getAllRememberAccounts(final RememberAccountsDataReqDto req) {
        final Set<RememberAccountResDto> allRememberAccounts = new HashSet<>();
        for (final RememberAccountReqDto account : req.getAccounts()) {
            final Optional<LocalUserModel> userModel = localUserRepository.findUserByNickname(account.getUserLogin());
            if (userModel.isEmpty()) break;
            allRememberAccounts.add(mapperFacade.map(userModel.get(), RememberAccountResDto.class));
        }
        return allRememberAccounts;
    }

    //------------------------------------------------------------------------------------------------------------------

    private List<SimpleTupleDto<Number>> generateData(int start, int end, ICalendarLambdaExpression expression) {
        return IntStream.range(start, end)
                .mapToObj(x -> new SimpleTupleDto<Number>(x, expression.filledData(x)))
                .collect(Collectors.toList());
    }
}
