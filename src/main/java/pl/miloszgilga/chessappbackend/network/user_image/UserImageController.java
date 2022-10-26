/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 *  File name: UserImageController.java
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

package pl.miloszgilga.chessappbackend.network.user_image;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import pl.miloszgilga.lib.jmpsl.security.user.CurrentUser;
import pl.miloszgilga.lib.jmpsl.oauth2.user.OAuth2UserExtender;
import pl.miloszgilga.lib.jmpsl.security.excluder.MethodSecurityPathExclude;

import pl.miloszgilga.chessappbackend.network.user_image.dto.*;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;
import pl.miloszgilga.chessappbackend.aspect.AspectCheckAuthSupplier;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(USER_IMAGE_ENDPOINT)
class UserImageController {

    private final IUserImageService service;
    private final IUserAvatarService avatarService;
    private final IUserBannerService bannerService;

    //------------------------------------------------------------------------------------------------------------------

    UserImageController(IUserImageService service, IUserAvatarService avatarService, IUserBannerService bannerService) {
        this.service = service;
        this.avatarService = avatarService;
        this.bannerService = bannerService;
    }

    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(USER_IMAGES_ALL)
    @MethodSecurityPathExclude()
    ResponseEntity<GetUserImagesResDto> getUserImages(@Valid @RequestBody GetUserImagesReqDto req) {
        return new ResponseEntity<>(service.getUserImages(req), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(AVATAR)
    @AspectCheckAuthSupplier(suppliedFor = { "local" })
    ResponseEntity<SimpleServerMessageDto> addUserAvatar(@RequestParam("file") MultipartFile image,
                                                         @CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(avatarService.addUserAvatar(image, userId), HttpStatus.CREATED);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PutMapping(AVATAR)
    @AspectCheckAuthSupplier(suppliedFor = { "local" })
    ResponseEntity<SimpleServerMessageDto> updateUserAvatar(@RequestParam("file") MultipartFile image,
                                                            @CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(avatarService.updateUserAvatar(image, userId), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @DeleteMapping(AVATAR)
    @AspectCheckAuthSupplier(suppliedFor = { "local" })
    ResponseEntity<SimpleServerMessageDto> deleteUserAvatar(@CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(avatarService.deleteUserAvatar(userId), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PostMapping(BANNER)
    @AspectCheckAuthSupplier(suppliedFor = { "local" })
    ResponseEntity<SimpleServerMessageDto> addUserBanner(@RequestParam("file") MultipartFile image,
                                                         @CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(bannerService.addUserBanner(image, userId), HttpStatus.CREATED);
    }

    //------------------------------------------------------------------------------------------------------------------

    @PutMapping(BANNER)
    @AspectCheckAuthSupplier(suppliedFor = { "local" })
    ResponseEntity<SimpleServerMessageDto> updateUserBanner(@RequestParam("file") MultipartFile image,
                                                            @CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(bannerService.updateUserBanner(image, userId), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @DeleteMapping(BANNER)
    @AspectCheckAuthSupplier(suppliedFor = { "local" })
    ResponseEntity<SimpleServerMessageDto> deleteUserBanner(@CurrentUser OAuth2UserExtender user) {
        final Long userId = ((LocalUserModel) user.getUserModel()).getId();
        return new ResponseEntity<>(bannerService.deleteUserBanner(userId), HttpStatus.OK);
    }
}
