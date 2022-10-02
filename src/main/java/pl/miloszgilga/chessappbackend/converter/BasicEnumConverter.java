/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: BasicConverter.java
 * Last modified: 01/10/2022, 23:42
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

import java.util.stream.Stream;
import javax.persistence.AttributeConverter;

//----------------------------------------------------------------------------------------------------------------------

public abstract class BasicEnumConverter<T extends IBasicEnumConverter> implements AttributeConverter<T, String> {

    private final Class<T> enumClazz;

    //------------------------------------------------------------------------------------------------------------------

    protected BasicEnumConverter(Class<T> enumClazz) {
        this.enumClazz = enumClazz;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (attribute == null) return null;
        return attribute.getEnumName();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Stream.of(enumClazz.getEnumConstants())
                .filter(g -> g.getEnumName().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
