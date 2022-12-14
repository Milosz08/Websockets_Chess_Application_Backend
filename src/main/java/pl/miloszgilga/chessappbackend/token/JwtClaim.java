/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: JwtClaim.java
 * Last modified: 07/09/2022, 16:41
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

package pl.miloszgilga.chessappbackend.token;

import lombok.*;

//----------------------------------------------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum JwtClaim {
    IS_EXPIRED("isExpired"),
    OTA_TOKEN("otaToken"),
    EMAIL("email"),
    NICKNAME("nickname"),
    ROLES("roles"),
    USER_ID("userId"),
    FULL_NAME("fullName");

    //------------------------------------------------------------------------------------------------------------------

    private final String claimName;
}
