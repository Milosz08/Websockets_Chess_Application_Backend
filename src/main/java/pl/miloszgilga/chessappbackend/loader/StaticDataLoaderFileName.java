/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: StaticDataLoaderFileName.java
 * Last modified: 15/09/2022, 19:31
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

import lombok.AllArgsConstructor;
import pl.miloszgilga.lib.jmpsl.util.loader.IStaticJsonLoaderFiles;

//----------------------------------------------------------------------------------------------------------------------

@AllArgsConstructor
public enum StaticDataLoaderFileName implements IStaticJsonLoaderFiles {
    CALENDAR_STATIC_DATA("calendar-static-data.json"),
    GENDER_STATIC_DATA("gender-static-data.json"),
    COUNTRY_STATIC_DATA("country-static-data.json");

    //------------------------------------------------------------------------------------------------------------------

    private final String fileName;

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getFileName() {
        return fileName;
    }
}
