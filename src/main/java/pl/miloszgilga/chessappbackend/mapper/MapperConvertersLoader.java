/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MapperConvertersLoader.java
 * Last modified: 27/09/2022, 17:26
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.*;
import ma.glasnost.orika.converter.ConverterFactory;

import org.reflections.util.*;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.lang.reflect.Method;

//----------------------------------------------------------------------------------------------------------------------

@Configuration
class MapperConvertersLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperConvertersLoader.class);

    private final ConverterFactory converterFactory;
    private final ApplicationContext applicationContext;

    //------------------------------------------------------------------------------------------------------------------

    MapperConvertersLoader(MapperFactory mapperFactory, ApplicationContext applicationContext) {
        this.converterFactory = mapperFactory.getConverterFactory();
        this.applicationContext = applicationContext;
        loadAllConvertersByReflection();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void loadAllConvertersByReflection() {
        final org.reflections.Configuration configuration = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(Scanners.TypesAnnotated);
        final var reflections = new Reflections(configuration);
        final Set<Class<?>> convertersClazz = reflections.getTypesAnnotatedWith(InjectableMappingConverter.class);
        for (Class<?> converterClazz : convertersClazz) {
            final CustomConverter<?, ?> converter = (CustomConverter<?, ?>) applicationContext.getBean(converterClazz);
            try {
                final Method method = converterClazz.getMethod("getConverterType");
                converterFactory.registerConverter((String) method.invoke(converter), converter);
                LOGGER.info("Custom mapper: {} was successfully loaded via reflection", converterClazz.getSimpleName());
            } catch (Exception ex) {
                LOGGER.error("Custom mapper: {} was failure loaded via reflection", converterClazz.getSimpleName());
                LOGGER.error("Error: {}", ex.getMessage());
            }
        }
    }
}
