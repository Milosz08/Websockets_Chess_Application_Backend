/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: DateFromStringToObjectConverter.java
 * Last modified: 27/09/2022, 17:34
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

import java.util.Date;

import org.springframework.stereotype.Component;

import pl.miloszgilga.lib.jmpsl.util.mapper.*;
import pl.miloszgilga.lib.jmpsl.util.TimeUtil;

import static pl.miloszgilga.chessappbackend.mapper.Converter.DATE_FROM_STRING_TO_OBJECT;

//----------------------------------------------------------------------------------------------------------------------

@Component
@MappingConverter
public class DateFromStringToObjectConverter extends CustomConverter<String, Date> implements IReflectConverter {

    @Override
    public Date convert(String source, Type<? extends Date> destType, MappingContext context) {
        return TimeUtil.deserialize(source).orElse(new Date());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getConverterType() {
        return DATE_FROM_STRING_TO_OBJECT.getName();
    }
}
