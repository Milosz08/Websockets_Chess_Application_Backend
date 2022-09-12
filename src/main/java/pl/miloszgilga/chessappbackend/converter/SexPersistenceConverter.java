/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SexPersistenceConverter.java
 * Last modified: 11/09/2022, 02:19
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

package pl.miloszgilga.chessappbackend.converter;

import javax.persistence.Converter;
import javax.persistence.AttributeConverter;

import java.util.stream.Stream;

import pl.miloszgilga.chessappbackend.utils.UserSexSpecific;

//----------------------------------------------------------------------------------------------------------------------

@Converter(autoApply = true)
public class SexPersistenceConverter implements AttributeConverter<UserSexSpecific, String> {

    @Override
    public String convertToDatabaseColumn(UserSexSpecific attribute) {
        if (attribute == null) return null;
        return attribute.getSex();
    }

    @Override
    public UserSexSpecific convertToEntityAttribute(String sexValue) {
        if (sexValue == null) return null;
        return Stream.of(UserSexSpecific.values())
                .filter(s -> s.getSex().equals(sexValue))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}