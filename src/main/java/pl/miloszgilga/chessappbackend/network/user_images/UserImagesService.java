/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 *  File name: UserImageService.java
 *  Last modified: 26/10/2022, 09:44
 *  Project name: chess-app-backend
 *
 *  Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 *  THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 *  COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.chessappbackend.network.user_images;

import org.slf4j.*;
import ma.glasnost.orika.MapperFacade;

import org.springframework.stereotype.Service;

import pl.miloszgilga.chessappbackend.network.user_images.dto.*;
import pl.miloszgilga.chessappbackend.network.user_images.domain.*;

import static pl.miloszgilga.chessappbackend.exception.custom.AuthException.UserNotFoundException;

//----------------------------------------------------------------------------------------------------------------------

@Service
class UserImagesService implements IUserImagesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserImagesService.class);

    private final MapperFacade mapperFacade;
    private final ILocalUserImagesRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    UserImagesService(MapperFacade mapperFacade, ILocalUserImagesRepository repository) {
        this.mapperFacade = mapperFacade;
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public GetUserImagesResDto getUserImages(final GetUserImagesReqDto req) {
        final LocalUserImagesModel userImages = repository.findUserImagesByNickname(req.getNickname()).orElseThrow(() -> {
            LOGGER.error("Attempt to get images from non-existing user. Req: {}", req);
            throw new UserNotFoundException("Unable to find user by passed data.");
        });
        return mapperFacade.map(userImages, GetUserImagesResDto.class);
    }
}
