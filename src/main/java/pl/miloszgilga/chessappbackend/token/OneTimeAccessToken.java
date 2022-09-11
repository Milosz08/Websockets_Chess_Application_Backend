/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ShortLivedAccessTokenHelper.java
 * Last modified: 02/09/2022, 16:25
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

package pl.miloszgilga.chessappbackend.token;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class OneTimeAccessToken {

    private final EnvironmentVars environment;

    private static final Random RANDOM = new Random();
    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z0-9]+");

    private static final String SIGNS = "abcdefghijklmnoprstquvwxyzABCDEFGHIJKLMNOPRSTQUWXYZ0123456789";

    public OneTimeAccessToken(EnvironmentVars environment) {
        this.environment = environment;
    }

    public String generateToken(int tokenLength) {
        final var builder = new StringBuilder();
        for (int i = 0; i < tokenLength; i++) {
            builder.append(SIGNS.charAt(RANDOM.nextInt(SIGNS.length())));
        }
        return builder.toString();
    }

    public String generateToken() {
        return generateToken(environment.getOtaTokenLenght());
    }

    public boolean checkIsTokenIsValid(String token) {
        final Matcher matcher = PATTERN.matcher(token);
        return matcher.matches();
    }
}
