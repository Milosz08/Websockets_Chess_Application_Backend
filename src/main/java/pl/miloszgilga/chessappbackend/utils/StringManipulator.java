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

import java.util.*;
import java.util.stream.*;

import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class StringManipulator {

    private static final Random RANDOM = new Random();

    //------------------------------------------------------------------------------------------------------------------

    public String capitalised(String textToCapitalised) {
        final String normalized = textToCapitalised.trim().toLowerCase(Locale.ROOT);
        return Character.toString(normalized.charAt(0)).toUpperCase(Locale.ROOT) + normalized.substring(1);
    }

    //------------------------------------------------------------------------------------------------------------------

    public String emptyStringRetNull(String text) {
        return text.equals("") ? null : text;
    }

    //------------------------------------------------------------------------------------------------------------------

    public String generateInitials(LocalUserModel userModel) {
        return (userModel.getFirstName().charAt(0) + Character.toString(userModel.getLastName().charAt(0)))
                .toUpperCase(Locale.ROOT);
    }

    //------------------------------------------------------------------------------------------------------------------

    public String generateUserDefNickname(String nickname) {
        String numbersSequence = IntStream.generate(() -> RANDOM.nextInt(10)).limit(3)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining());
        return nickname.toLowerCase(Locale.ROOT) + numbersSequence;
    }

    //------------------------------------------------------------------------------------------------------------------

    public String addExtraDotOnFinishIfNotExist(String sequence) {
        if (sequence == null) return "";
        if (sequence.charAt(sequence.length() - 1) == '.') return sequence;
        return sequence + ".";
    }

    //------------------------------------------------------------------------------------------------------------------

    public String generateFullName(LocalUserModel userModel) {
        return capitalised(userModel.getFirstName()) + " " + capitalised(userModel.getLastName());
    }

    //------------------------------------------------------------------------------------------------------------------

    public Pair<String, String> extractUserDataFromUsername(String username) {
        final String[] firstWithLast = username.contains(" ") ? username.split(" ") : new String[] { username, null };
        return new Pair<>(capitalised(firstWithLast[0]), capitalised(firstWithLast[1]));
    }
}
