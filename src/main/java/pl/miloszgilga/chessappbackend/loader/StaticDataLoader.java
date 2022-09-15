/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: StaticDataLoader.java
 * Last modified: 15/09/2022, 19:33
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

package pl.miloszgilga.chessappbackend.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

//----------------------------------------------------------------------------------------------------------------------

public class StaticDataLoader<T extends LoaderModel> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticDataLoader.class);

    private static final String REL_PATH = "/static/data/";
    private final String fileName;
    private final Class<T> mappedTypeClazz;
    private T loadedData;

    public StaticDataLoader(StaticDataLoaderFileName fileName, Class<T> mappedTypeClazz) {
        this.fileName = fileName.getFileName();
        this.mappedTypeClazz = mappedTypeClazz;
        loadDataFromFile();
    }

    private void loadDataFromFile() {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            try (final InputStream inputStream = StaticDataLoader.class.getResourceAsStream(REL_PATH + fileName)) {
                if (inputStream == null) throw new IOException();
                loadedData = objectMapper.readValue(new String(inputStream.readAllBytes()), mappedTypeClazz);
                LOGGER.info("Static data file: {} was loaded successfuly", fileName);
            }
        } catch (IOException ex) {
            LOGGER.error("File: {} not exist or is corrupted", fileName);
        }
    }

    public T getLoadedData() {
        return loadedData;
    }
}
