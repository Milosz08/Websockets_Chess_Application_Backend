/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ExceptionHandler.java
 * Last modified: 01/09/2022, 18:39
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

package pl.miloszgilga.chessappbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.*;
import javax.servlet.http.*;

import pl.miloszgilga.lib.jmpsl.core.StringUtil;
import pl.miloszgilga.lib.jmpsl.core.exception.*;

//----------------------------------------------------------------------------------------------------------------------

@RestControllerAdvice
public class ExceptionListener {

    private final MessageSource messageSource;

    //------------------------------------------------------------------------------------------------------------------

    public ExceptionListener(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    //------------------------------------------------------------------------------------------------------------------

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public NotReadableExceptionResDto handleNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return new NotReadableExceptionResDto(ServerExceptionResDto.generate(HttpStatus.INTERNAL_SERVER_ERROR, req),
                ex.getMessage());
    }

    //------------------------------------------------------------------------------------------------------------------

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public InvalidDtoExceptionResDto handleInvalidArgument(MethodArgumentNotValidException ex, HttpServletRequest req) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        return new InvalidDtoExceptionResDto(ServerExceptionResDto.generate(HttpStatus.BAD_REQUEST, req), errors);
    }

    //------------------------------------------------------------------------------------------------------------------

    @ExceptionHandler(BasicServerException.class)
    public InvalidDtoExceptionResDto handleBasicServerException(BasicServerException ex, HttpServletRequest req,
                                                                HttpServletResponse res) {
        res.setStatus(ex.getStatus().value());
        return new InvalidDtoExceptionResDto(ServerExceptionResDto.generate(ex.getStatus(), req), ex.getMessage());
    }

    //------------------------------------------------------------------------------------------------------------------

    @ExceptionHandler(AuthenticationException.class)
    public InvalidDtoExceptionResDto handleAuthenticationException(AuthenticationException ex, HttpServletRequest req,
                                                               HttpServletResponse res) {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        return new InvalidDtoExceptionResDto(ServerExceptionResDto.generate(HttpStatus.UNAUTHORIZED, req),
                mappingAuthorizationExceptionMessages(ex.getMessage()));
    }

    //------------------------------------------------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public InvalidDtoExceptionResDto handleRuntimeException(Exception ex, HttpServletRequest req,
                                                               HttpServletResponse res) {
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        return new InvalidDtoExceptionResDto(ServerExceptionResDto.generate(HttpStatus.BAD_REQUEST, req),
                mappingAuthorizationExceptionMessages(StringUtil.addDot(ex.getMessage())));
    }

    //------------------------------------------------------------------------------------------------------------------

    private String mappingAuthorizationExceptionMessages(String rawMessage) {
        String messagePattern = "";
        switch (rawMessage) {
            case "User is disabled":            messagePattern = "auth.exception.accountNotVerified";   break;
            case "Bad credentials":             messagePattern = "auth.exception.accountNotExist";      break;
        }
        return messagePattern.isEmpty() ? rawMessage : messageSource.getMessage(messagePattern, null, Locale.getDefault());
    }
}
