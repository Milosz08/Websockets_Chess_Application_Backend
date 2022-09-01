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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pl.miloszgilga.chessappbackend.utils.TimeHelper;
import pl.miloszgilga.chessappbackend.exception.custom.BasicServerException;

//----------------------------------------------------------------------------------------------------------------------

@RestControllerAdvice
public class ExceptionListener {

    private final TimeHelper timeHelper;

    public ExceptionListener(TimeHelper timeHelper) {
        this.timeHelper = timeHelper;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public NotReadableExceptionRes handleNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest req) {
        final ServerExceptionRes res = basicExceptionRes(HttpStatus.INTERNAL_SERVER_ERROR, req);
        return new NotReadableExceptionRes(res, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public InvalidDaoExceptionRes handleInvalidArgument(MethodArgumentNotValidException ex, HttpServletRequest req) {
        final Map<String, String> errors = new HashMap<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        final ServerExceptionRes res = basicExceptionRes(HttpStatus.BAD_REQUEST, req);
        return new InvalidDaoExceptionRes(res, errors);
    }

    @ExceptionHandler(BasicServerException.class)
    public BasicDataExceptionRes handleBasicServerException(
            BasicServerException ex, HttpServletRequest req, HttpServletResponse res) {
        final HttpStatus status = ex.getStatus();
        final var resData = basicExceptionRes(status, req);
        res.setStatus(status.value());
        return new BasicDataExceptionRes(resData, ex.getMessage());
    }

    private ServerExceptionRes basicExceptionRes(HttpStatus status, HttpServletRequest req) {
        return new ServerExceptionRes(timeHelper.getCurrentUTC(), status.value(), status.name(),
                req.getServletPath(), req.getMethod());
    }
}