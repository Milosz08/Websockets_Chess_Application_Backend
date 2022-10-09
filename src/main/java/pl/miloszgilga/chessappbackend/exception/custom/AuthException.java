/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AuthException.java
 * Last modified: 19/09/2022, 22:55
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

public class AuthException {

    public static class UserNotFoundException extends BasicServerException {
        public UserNotFoundException(String message, Object... args) {
            super(HttpStatus.NOT_FOUND, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class RoleNotFoundException extends BasicServerException {
        public RoleNotFoundException(String message, Object... args) {
            super(HttpStatus.NOT_FOUND, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class OAuth2AuthenticationProcessingException extends BasicAuthenticationException {
        public OAuth2AuthenticationProcessingException(String message, Object... args) {
            super(HttpStatus.NOT_IMPLEMENTED, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class OAuth2CredentialsSupplierMalformedException extends BasicAuthenticationException {
        public OAuth2CredentialsSupplierMalformedException(String message, Object... args) {
            super(HttpStatus.UNAUTHORIZED, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class OAuth2NotSupportedUriException extends BasicAuthenticationException {
        public OAuth2NotSupportedUriException(String message, Object... args) {
            super(HttpStatus.BAD_REQUEST, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class AccountAlreadyExistException extends BasicAuthenticationException {
        public AccountAlreadyExistException(String message, Object... args) {
            super(HttpStatus.BAD_REQUEST, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class DifferentAuthenticationProviderException extends BasicServerException {
        public DifferentAuthenticationProviderException(String message, Object... args) {
            super(HttpStatus.UNAUTHORIZED, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class AccountIsAlreadyActivatedException extends BasicServerException {
        public AccountIsAlreadyActivatedException(String message, Object... args) {
            super(HttpStatus.BAD_REQUEST, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class SessionIsNotStartedException extends BasicServerException {
        public SessionIsNotStartedException(String message, Object... args) {
            super(HttpStatus.UNAUTHORIZED, message, args);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class ChangePasswordProhibitedActionException extends BasicServerException {
        public ChangePasswordProhibitedActionException(String message, Object... args) {
            super(HttpStatus.BAD_REQUEST, message, args);
        }
    }
}
