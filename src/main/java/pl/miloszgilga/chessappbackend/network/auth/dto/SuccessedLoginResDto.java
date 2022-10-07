/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: SuccessedLoginViaLocalResponseDto.java
 * Last modified: 10/09/2022, 19:17
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

package pl.miloszgilga.chessappbackend.network.auth.dto;

import lombok.*;

//----------------------------------------------------------------------------------------------------------------------

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessedLoginResDto {
    private Long userId;
    private String nickname;
    private String fullName;
    private String initials;
    private boolean ifHasPhoto;
    private String credentialsSupplier;
    private String photoUrl;
    private String jwtToken;
    private String refreshToken;
    private boolean isActivated;
}
