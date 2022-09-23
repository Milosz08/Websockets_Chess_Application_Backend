/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: TimeHelper.java
 * Last modified: 01/09/2022, 19:10
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

package pl.miloszgilga.chessappbackend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import pl.miloszgilga.chessappbackend.exception.custom.BasicServerException;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class TimeHelper {

    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    public Date addMinutesToCurrentDate(int minutes) {
        return new Date(new Date().getTime() + ((long) minutes * 60 * 1000));
    }

    public Date addMonthsToCurrentDate(int months) {
        return new Date(new Date().getTime() + ((long) months * 31 * 24 * 60 * 60 * 1000));
    }

    public int currentYearMinusAcceptableAge() {
        return Year.now().getValue() - 10;
    }

    public Date convertStringDateToDateObject(String date) {
        try {
            return dateFormatter.parse(date);
        } catch (ParseException ex) {
            throw new BasicServerException(HttpStatus.EXPECTATION_FAILED, "Unhandled date format.");
        }
    }

    public String getCurrentUTC() {
        return dateTimeFormatter.format(new Date());
    }
}
