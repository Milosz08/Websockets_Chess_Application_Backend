/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: StringManipulator.java
 * Last modified: 12/09/2022, 02:54
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

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import pl.miloszgilga.lib.jmpsl.core.StringUtil;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class StringManipulator {

    public String generateFullName(LocalUserModel userModel) {
        return StringUtil.capitalize(userModel.getFirstName()) + " " + StringUtil.capitalize(userModel.getLastName());
    }

    //------------------------------------------------------------------------------------------------------------------

    public Pair<String, String> extractUserDataFromUsername(String username) {
        final String[] firstWithLast = username.contains(" ") ? username.split(" ") : new String[] { username, null };
        return new Pair<>(StringUtil.capitalize(firstWithLast[0]), StringUtil.capitalize(firstWithLast[1]));
    }
}
