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

import pl.miloszgilga.chessappbackend.utils.*;
import pl.miloszgilga.chessappbackend.exception.custom.BasicServerException;

//----------------------------------------------------------------------------------------------------------------------

@RestControllerAdvice
public class ExceptionListener {

    private final TimeHelper timeHelper;
    private final MessageSource messageSource;
    private final StringManipulator manipulator;

    //------------------------------------------------------------------------------------------------------------------

    public ExceptionListener(TimeHelper timeHelper, MessageSource messageSource, StringManipulator manipulator) {
        this.timeHelper = timeHelper;
        this.messageSource = messageSource;
        this.manipulator = manipulator;
    }

    //------------------------------------------------------------------------------------------------------------------

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public NotReadableExceptionRes handleNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest req) {
        final ServerExceptionRes res = basicExceptionRes(HttpStatus.INTERNAL_SERVER_ERROR, req);
        return new NotReadableExceptionRes(res, ex.getMessage());
    }

    //------------------------------------------------------------------------------------------------------------------

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public InvalidDtoExceptionRes handleInvalidArgument(MethodArgumentNotValidException ex, HttpServletRequest req) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        final ServerExceptionRes res = basicExceptionRes(HttpStatus.BAD_REQUEST, req);
        return new InvalidDtoExceptionRes(res, errors);
    }

    //------------------------------------------------------------------------------------------------------------------

    @ExceptionHandler(BasicServerException.class)
    public BasicDataExceptionRes handleBasicServerException(BasicServerException ex, HttpServletRequest req,
                                                            HttpServletResponse res) {
        final HttpStatus status = ex.getStatus();
        final var resData = basicExceptionRes(status, req);
        res.setStatus(status.value());
        return new BasicDataExceptionRes(resData, ex.getMessage());
    }

    //------------------------------------------------------------------------------------------------------------------

    @ExceptionHandler(AuthenticationException.class)
    public BasicDataExceptionRes handleAuthenticationException(AuthenticationException ex, HttpServletRequest req,
                                                               HttpServletResponse res) {
        final var resData = basicExceptionRes(HttpStatus.UNAUTHORIZED, req);
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        return new BasicDataExceptionRes(resData, mappingAuthorizationExceptionMessages(ex.getMessage()));
    }

    //------------------------------------------------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public BasicDataExceptionRes handleRuntimeException(Exception ex, HttpServletRequest req,
                                                               HttpServletResponse res) {
        final var resData = basicExceptionRes(HttpStatus.BAD_REQUEST, req);
        res.setStatus(HttpStatus.BAD_REQUEST.value());
        final String messageWithDot = manipulator.addExtraDotOnFinishIfNotExist(ex.getMessage());
        return new BasicDataExceptionRes(resData, mappingAuthorizationExceptionMessages(messageWithDot));
    }

    //------------------------------------------------------------------------------------------------------------------

    private ServerExceptionRes basicExceptionRes(HttpStatus status, HttpServletRequest req) {
        return ServerExceptionRes.builder()
                .path(req.getServletPath())
                .method(req.getMethod())
                .statusCode(status.value())
                .statusText(status.name())
                .servletTimestampUTC(timeHelper.getCurrentUTC())
                .build();
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
