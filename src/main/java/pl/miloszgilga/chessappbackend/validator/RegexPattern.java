/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: RegexPattern.java
 * Last modified: 11/09/2022, 01:13
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

package pl.miloszgilga.chessappbackend.validator;

//----------------------------------------------------------------------------------------------------------------------

public class RegexPattern {
    public static final String NAME_SURNAME_PATTERN = "^[a-zA-Z]{2,30}";
    public static final String OTA_TOKEN_PATTERN = "^[a-zA-Z\\d]{10}";
    public static final String NICKNAME_PATTERN = "^[a-z\\d]{5,20}";
    public static final String BIRTH_DATE_PATTERN = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";
    public static final String PASSWORD_PATTERN = "^[a-zA-Z\\d!@#$%&*]{8,30}$";
    public static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,10}$";
}
