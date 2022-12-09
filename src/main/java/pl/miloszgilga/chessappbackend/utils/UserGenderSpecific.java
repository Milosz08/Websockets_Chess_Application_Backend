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

import java.util.Comparator;
import java.util.Set;
import java.util.stream.*;

import pl.miloszgilga.chessappbackend.dto.SimpleTupleDto;
import pl.miloszgilga.lib.jmpsl.core.converter.IBasicEnumConverter;

//----------------------------------------------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum UserGenderSpecific implements IBasicEnumConverter {
    MALE(1, "male", "I am a men"),
    FEMALE(2, "female", "I am a women"),
    OTHER(3, "other", "I identify as another gender");

    //------------------------------------------------------------------------------------------------------------------

    private final int seqNumber;
    private final String gender;
    private final String alias;

    //------------------------------------------------------------------------------------------------------------------

    public static UserGenderSpecific findGenderByString(final String gender) {
        return Stream.of(UserGenderSpecific.values())
                .filter(g -> g.getGender().equals(gender))
                .findFirst()
                .orElse(UserGenderSpecific.OTHER);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static SimpleTupleDto<String> findGenderAndReturnTuple(final String gender) {
        final UserGenderSpecific foundGender = UserGenderSpecific.findGenderByString(gender);
        return new SimpleTupleDto<>(foundGender.gender, foundGender.alias);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static Set<SimpleTupleDto<String>> getAllGendersInTuples() {
        return Stream.of(UserGenderSpecific.values())
                .sorted(Comparator.comparingInt(o -> o.seqNumber))
                .map(g -> new SimpleTupleDto<>(g.gender, g.alias))
                .collect(Collectors.toSet());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getEnumName() {
        return gender;
    }
}
