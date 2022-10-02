/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: EmailExeception.java
 * Last modified: 01/09/2022, 20:55
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

package pl.miloszgilga.chessappbackend.exception.custom;

import org.springframework.http.HttpStatus;

//----------------------------------------------------------------------------------------------------------------------

public class EmailException {

    public static class EmailAlreadyExistException extends BasicServerException {
        public EmailAlreadyExistException(HttpStatus status, String message, Object... args) {
            super(status, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class EmailNotFoundException extends BasicServerException {
        public EmailNotFoundException(String message, Object... args) {
            super(HttpStatus.NOT_FOUND, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class EmailSenderException extends BasicServerException {
        public EmailSenderException(String message, Object... args) {
            super(HttpStatus.SERVICE_UNAVAILABLE, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class EmailAndSecondEmailIdenticalException extends BasicServerException {
        public EmailAndSecondEmailIdenticalException(String message, Object... args) {
            super(HttpStatus.BAD_REQUEST, message, args);
        }
    }
}
