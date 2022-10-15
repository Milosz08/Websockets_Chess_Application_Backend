/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserSex.java
 * Last modified: 11/09/2022, 02:07
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

import lombok.*;
import java.util.stream.Stream;

import pl.miloszgilga.lib.jmpsl.util.converter.IBasicEnumConverter;

//----------------------------------------------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum UserGenderSpecific implements IBasicEnumConverter {
    MALE("male"),
    FEMALE("female"),
    OTHER("other");

    //------------------------------------------------------------------------------------------------------------------

    private final String gender;

    //------------------------------------------------------------------------------------------------------------------

    public static UserGenderSpecific findGenderByString(final String gender) {
        return Stream.of(UserGenderSpecific.values())
                .filter(r -> r.getGender().equals(gender))
                .findFirst()
                .orElse(UserGenderSpecific.OTHER);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getEnumName() {
        return gender;
    }
}
