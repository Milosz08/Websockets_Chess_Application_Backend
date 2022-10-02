/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MapperFacadesLoader.java
 * Last modified: 27/09/2022, 21:43
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

import org.reflections.util.*;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Set;
import java.lang.reflect.Method;

//----------------------------------------------------------------------------------------------------------------------

@Configuration
class MapperFacadesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperFacadesLoader.class);
    private final ApplicationContext applicationContext;

    //------------------------------------------------------------------------------------------------------------------

    MapperFacadesLoader(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        loadMapperFacadeImplByReflection();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void loadMapperFacadeImplByReflection() {
        final org.reflections.Configuration configuration = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(Scanners.MethodsAnnotated);
        final var reflections = new Reflections(configuration);
        final Set<Method> annotatedMethods = reflections.getMethodsAnnotatedWith(InjectableMappingFacade.class);
        for (Method method : annotatedMethods) {
            try {
                method.invoke(applicationContext.getBean(method.getDeclaringClass()));
                LOGGER.info("Custom mapping facade: {} was successfull loaded via reflection", method.getName());
            } catch (Exception ex) {
                LOGGER.error("Custom mapping facade: {} was failure loaded via reflection", method.getName());
                LOGGER.error("Error: {}", ex.getMessage());
            }
        }
    }
}
