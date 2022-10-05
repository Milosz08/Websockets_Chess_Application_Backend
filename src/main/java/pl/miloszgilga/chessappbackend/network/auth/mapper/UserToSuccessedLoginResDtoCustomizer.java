/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UserToSuccessedLoginResDtoCustomizer.java
 * Last modified: 03/10/2022, 13:22
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

package pl.miloszgilga.chessappbackend.network.auth.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.*;
import org.javatuples.Pair;

import org.springframework.stereotype.Component;

import java.util.*;
import javax.transaction.Transactional;

import pl.miloszgilga.chessappbackend.network.auth.domain.*;
import pl.miloszgilga.chessappbackend.utils.StringManipulator;
import pl.miloszgilga.chessappbackend.token.JsonWebTokenCreator;
import pl.miloszgilga.chessappbackend.network.auth.dto.SuccessedLoginResDto;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class UserToSuccessedLoginResDtoCustomizer extends CustomMapper<LocalUserModel, SuccessedLoginResDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserToSuccessedLoginResDtoCustomizer.class);

    private final StringManipulator manipulator;
    private final JsonWebTokenCreator tokenCreator;
    private final IRefreshTokenRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    public UserToSuccessedLoginResDtoCustomizer(StringManipulator manipulator, JsonWebTokenCreator tokenCreator,
                                                IRefreshTokenRepository repository) {
        this.manipulator = manipulator;
        this.tokenCreator = tokenCreator;
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public void mapAtoB(LocalUserModel userModel, SuccessedLoginResDto resDto, MappingContext context) {
        resDto.setJwtToken(tokenCreator.createUserCredentialsToken(userModel));
        resDto.setRefreshToken(generateAndSaveRefreshToken(userModel));
        resDto.setActivated(userModel.getIsActivated());
        resDto.setFullName(userModel.getFirstName() + " " + userModel.getLastName());
        resDto.setInitials(manipulator.generateInitials(userModel));
        resDto.setCredentialsSupplier(userModel.getCredentialsSupplier().getName());
    }

    //------------------------------------------------------------------------------------------------------------------

    @Transactional
    String generateAndSaveRefreshToken(LocalUserModel userModel) {
        final Optional<RefreshTokenModel> existingRefreshToken = repository.findRefreshTokenByUserId(userModel.getId());
        final Pair<String, Date> refreshToken = tokenCreator.createUserRefreshToken(userModel);
        if (existingRefreshToken.isEmpty()) {
            final var refreshTokenModel = RefreshTokenModel.builder()
                    .refreshToken(refreshToken.getValue0())
                    .localUser(userModel)
                    .expiredAt(refreshToken.getValue1())
                    .build();
            repository.save(refreshTokenModel);
            LOGGER.info("Successfuly created refresh token for user. User data: {}", userModel);
            return refreshToken.getValue0();
        }
        final RefreshTokenModel foundExistingRefreshTokenModel = existingRefreshToken.get();
        foundExistingRefreshTokenModel.setRefreshToken(refreshToken.getValue0());
        foundExistingRefreshTokenModel.setExpiredAt(refreshToken.getValue1());
        repository.save(foundExistingRefreshTokenModel);
        LOGGER.info("Successfuly updated refresh token for user. User data: {}", userModel);
        return refreshToken.getValue0();
    }
}
