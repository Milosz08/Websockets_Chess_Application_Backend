/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 *  File name: UserAspectJ.java
 *  Last modified: 26/10/2022, 10:15
 *  Project name: chess-app-backend
 *
 *  Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 *  COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.chessappbackend.aspect;

import org.slf4j.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.lang.reflect.Method;

import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.lib.jmpsl.oauth2.user.OAuth2UserExtender;

import static pl.miloszgilga.chessappbackend.exception.custom.AuthException.*;

//----------------------------------------------------------------------------------------------------------------------

@Aspect
@Component
public class LoggedUserSupplierCheckerAspectJ {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggedUserSupplierCheckerAspectJ.class);

    //------------------------------------------------------------------------------------------------------------------

    @Before("@annotation(pl.miloszgilga.chessappbackend.aspect.AspectCheckAuthSupplier) && args(userExtender)")
    public void around(JoinPoint joinPoint, OAuth2UserExtender userExtender) {
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final AspectCheckAuthSupplier annotation = method.getAnnotation(AspectCheckAuthSupplier.class);

        final LocalUserModel userModel = ((LocalUserModel) userExtender.getUserModel());
        if (Arrays.stream(annotation.suppliedFor()).noneMatch(s -> s.equals(userModel.getOAuth2Supplier().getEnumName()))) {
            LOGGER.error("Attempt to get protected data from user not managed locally. User: {}", userModel);
            throw new NotSupportedSupplierException("Action not supported for accounts managed by external " +
                    "authorization server.");
        }
    }
}
