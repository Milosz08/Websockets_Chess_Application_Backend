/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: HashPasswordConverter.java
 * Last modified: 28/09/2022, 01:55
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

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import pl.miloszgilga.lib.jmpsl.core.mapper.*;
import pl.miloszgilga.lib.jmpsl.core.mapper.converter.ImmediatelyLoadConverter;

import static pl.miloszgilga.chessappbackend.mapper.Converter.HASH_PASSWORD;

//----------------------------------------------------------------------------------------------------------------------

@Component
@MappingConverter
@ImmediatelyLoadConverter
public class HashPasswordConverter extends CustomConverter<String, String> implements IReflectConverter {

    private final PasswordEncoder passwordEncoder;

    //------------------------------------------------------------------------------------------------------------------

    public HashPasswordConverter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String convert(String source, Type<? extends String> destType, MappingContext mappingContext) {
        return passwordEncoder.encode(source);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getConverterType() {
        return HASH_PASSWORD.getName();
    }
}
