/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ChangeAllLetterToLowerConverter.java
 * Last modified: 28/09/2022, 03:20
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

import ma.glasnost.orika.*;
import ma.glasnost.orika.metadata.Type;

import java.util.Locale;
import org.springframework.stereotype.Component;

import pl.miloszgilga.chessappbackend.mapper.*;

import static pl.miloszgilga.chessappbackend.mapper.Converter.CHANGE_ALL_LETTERS_TO_LOWER;

//----------------------------------------------------------------------------------------------------------------------

@Component
@InjectableMappingConverter
public class ChangeAllLetterToLowerConverter extends CustomConverter<String, String> implements IReflectConverter {

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String convert(String source, Type<? extends String> destinationType, MappingContext mappingContext) {
        return source.toLowerCase(Locale.ROOT);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getConverterType() {
        return CHANGE_ALL_LETTERS_TO_LOWER.getName();
    }
}
