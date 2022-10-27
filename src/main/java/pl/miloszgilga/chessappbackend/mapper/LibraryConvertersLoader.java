/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LibraryConvertersLoader.java
 * Last modified: 27/10/2022, 04:41
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

package pl.miloszgilga.chessappbackend.mapper;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

import pl.miloszgilga.lib.jmpsl.util.mapper.converter.*;
import pl.miloszgilga.lib.jmpsl.util.mapper.converter.Converter;

import static pl.miloszgilga.lib.jmpsl.util.mapper.converter.Converter.*;

//----------------------------------------------------------------------------------------------------------------------

@Primary
@Component
public class LibraryConvertersLoader implements IMapperConverterLoader {

    @Override
    public Set<Converter> loadConverters() {
        return Set.of(
                DATE_FROM_STRING_TO_OBJECT,
                CAPITALIZED_FIRST_LETTER,
                CHANGE_ALL_LETTERS_TO_LOWER,
                INSERT_NULL_IF_STRING_IS_EMPTY
        );
    }
}
