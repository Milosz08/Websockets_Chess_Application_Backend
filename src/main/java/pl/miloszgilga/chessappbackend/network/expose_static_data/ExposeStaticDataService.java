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

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.dto.SimpleTupleDto;
import pl.miloszgilga.chessappbackend.loader.gender_data.GenderPropertiesLoader;
import pl.miloszgilga.chessappbackend.loader.gender_data.GenderPropertiesModel;
import pl.miloszgilga.chessappbackend.loader.calendar_data.CalendarPropertiesModel;
import pl.miloszgilga.chessappbackend.loader.calendar_data.CalendarPropertiesLoader;

import pl.miloszgilga.chessappbackend.network.expose_static_data.dto.RegisterGenderDataResDto;
import pl.miloszgilga.chessappbackend.network.expose_static_data.dto.RegisterCalendarDataResDto;

//----------------------------------------------------------------------------------------------------------------------

@Service
class ExposeStaticDataService implements IExposeStaticDataService {

    private final TimeHelper timeHelper;
    private final GenderPropertiesLoader genderLoader;
    private final CalendarPropertiesLoader calendarLoader;

    ExposeStaticDataService(TimeHelper timeHelper, GenderPropertiesLoader genderLoader, CalendarPropertiesLoader loader) {
        this.timeHelper = timeHelper;
        this.genderLoader = genderLoader;
        this.calendarLoader = calendarLoader;
        this.countryLoader = countryLoader;
    }

    @Override
    public SignupCalendarDataResDto getSignupCalendarData() {
        final CalendarPropertiesModel loadedData = calendarLoader.getLoadedData();
        return new SignupCalendarDataResDto(
                generateData(1, 32, d -> String.format("%01d", d)),
                generateData(1, loadedData.getMonthsShort().size() + 1, m -> loadedData.getMonthsShort().get(m - 1)),
                generateData(1900, timeHelper.currentYearMinusAcceptableAge(), Integer::toString)
        );
    }

    @Override
    public SignupGenderDataResDto getSignupGenderData() {
        final GenderPropertiesModel loadedData = genderLoader.getLoadedData();
        return new SignupGenderDataResDto(loadedData.getGenders());
    }

    private List<SimpleTupleDto<Number>> generateData(int start, int end, ICalendarLambdaExpression expression) {
        return IntStream.range(start, end)
                .mapToObj(x -> new SimpleTupleDto<Number>(x, expression.filledData(x)))
                .collect(Collectors.toList());
    }
}
