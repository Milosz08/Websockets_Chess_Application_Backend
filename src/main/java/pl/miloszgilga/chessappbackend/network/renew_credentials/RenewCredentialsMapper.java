/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: RenewCredentialsMapper.java
 * Last modified: 09/10/2022, 19:54
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

package pl.miloszgilga.chessappbackend.network.renew_credentials;

import lombok.AllArgsConstructor;
import ma.glasnost.orika.MapperFactory;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Primary;

import pl.miloszgilga.lib.jmpsl.util.mapper.MappingFacade;

import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.renew_credentials.dto.ChangePasswordUserDetailsResDto;
import pl.miloszgilga.chessappbackend.network.renew_credentials.mapper.UserToChangePasswordUserDetailsCustomizer;

//----------------------------------------------------------------------------------------------------------------------

@Component
@Primary
@AllArgsConstructor
public class RenewCredentialsMapper {

    private final MapperFactory mapperFactory;
    private final UserToChangePasswordUserDetailsCustomizer userToChangePasswordUserDetailsCustomizer;

    //------------------------------------------------------------------------------------------------------------------

    @MappingFacade
    public void renewCredentialsAutoMapperFacadeImplementation() {
        mapperFactory.classMap(LocalUserModel.class, ChangePasswordUserDetailsResDto.class)
                .field("id", "userId")
                .field("localUserImages.avatarImage", "photoUrl")
                .customize(userToChangePasswordUserDetailsCustomizer)
                .byDefault().register();
    }
}
