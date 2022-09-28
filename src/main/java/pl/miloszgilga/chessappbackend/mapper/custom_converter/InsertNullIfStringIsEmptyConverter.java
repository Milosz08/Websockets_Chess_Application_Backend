/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: InsertNullIfStringIsEmptyConverter.java
 * Last modified: 28/09/2022, 20:24
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

package pl.miloszgilga.chessappbackend.mapper.custom_converter;

import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.CustomConverter;

import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.mapper.IReflectConverter;
import pl.miloszgilga.chessappbackend.mapper.InjectableMappingConverter;

import static pl.miloszgilga.chessappbackend.mapper.Converter.INSERT_NULL_IF_STRING_IS_EMPTY;

//----------------------------------------------------------------------------------------------------------------------

@Component
@InjectableMappingConverter
public class InsertNullIfStringIsEmptyConverter extends CustomConverter<String, String> implements IReflectConverter {

    private final StringManipulator manipulator;

    public InsertNullIfStringIsEmptyConverter(StringManipulator manipulator) {
        this.manipulator = manipulator;
    }

    @Override
    public String convert(String source, Type<? extends String> destinationType, MappingContext mappingContext) {
        return manipulator.emptyStringRetNull(source);
    }

    @Override
    public String getConverterType() {
        return INSERT_NULL_IF_STRING_IS_EMPTY.getName();
    }
}
