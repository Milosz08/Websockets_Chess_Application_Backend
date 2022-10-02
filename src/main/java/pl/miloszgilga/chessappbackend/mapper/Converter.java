/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: CustomConverter.java
 * Last modified: 27/09/2022, 17:28
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

import lombok.*;

//----------------------------------------------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum Converter {
    DATE_FROM_STRING_TO_OBJECT("date-from-string-to-object"),
    HASH_PASSWORD("hash-password"),
    CAPITALIZED_FIRST_LETTER("capitalized-first-letter"),
    CHANGE_ALL_LETTERS_TO_LOWER("change-all-letters-to-lower"),
    INSERT_NULL_IF_STRING_IS_EMPTY("unsert-null-if-string-is-empty");

    //------------------------------------------------------------------------------------------------------------------

    private final String name;
}
