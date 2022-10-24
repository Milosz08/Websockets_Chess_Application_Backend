/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: TokenException.java
 * Last modified: 07/09/2022, 16:04
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
import pl.miloszgilga.lib.jmpsl.util.exception.BasicAuthServerException;

//----------------------------------------------------------------------------------------------------------------------

public class TokenException {

    public static class JwtMalformedTokenException extends BasicAuthServerException {
        public JwtMalformedTokenException(String message, Object... args) {
            super(HttpStatus.BAD_REQUEST, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class JwtTokenExpiredException extends BasicAuthServerException {
        public JwtTokenExpiredException(String message, Object... args) {
                super(HttpStatus.BAD_REQUEST, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class OtaTokenExpiredException extends BasicAuthServerException {
        public OtaTokenExpiredException(String message, Object... args) {
            super(HttpStatus.UNAUTHORIZED, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class OtaTokenNotExistException extends BasicAuthServerException {
        public OtaTokenNotExistException(String message, Object... args) {
            super(HttpStatus.NOT_FOUND, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class RefreshTokenNotExistException extends BasicAuthServerException {
        public RefreshTokenNotExistException(String message, Object... args) {
            super(HttpStatus.NOT_FOUND, message, args);
        }
    }
}
