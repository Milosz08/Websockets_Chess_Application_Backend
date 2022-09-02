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

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.TimeZone;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class TimeHelper {

    private static final SimpleDateFormat PRE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat POST_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    TimeHelper() {
        PRE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Date addMinutesToCurrentDate(int minutes) {
        return getCurrentUTC(new Date().getTime() + ((long) minutes * 60 * 1000));
    }

    public Date addDaysToCurrentDate(int days) {
        return getCurrentUTC(new Date().getTime() + ((long) days * 24 * 60 * 60 * 1000));
    }

    public String getCurrentUTC() {
        return PRE_FORMAT.format(getCurrentUTC(new Date().getTime()));
    }

    private Date getCurrentUTC(long nanoTime) {
        Date date = new Date();
        try {
            date = POST_FORMAT.parse(PRE_FORMAT.format(new Date(nanoTime)));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date;
    }
}