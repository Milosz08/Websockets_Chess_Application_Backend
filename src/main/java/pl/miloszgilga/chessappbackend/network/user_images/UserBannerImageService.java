/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 *  File name: UserBannerService.java
 *  Last modified: 26/10/2022, 11:53
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import javax.transaction.Transactional;

import pl.miloszgilga.lib.jmpsl.gfx.sender.*;
import pl.miloszgilga.lib.jmpsl.oauth2.OAuth2Supplier;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.dto.SimpleServerMessageDto;
import pl.miloszgilga.chessappbackend.network.user_images.domain.*;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException;
import pl.miloszgilga.chessappbackend.exception.custom.ImageException.*;
import pl.miloszgilga.chessappbackend.network.user_images.dto.UpdatedImageResDto;

import static java.util.Objects.isNull;
import static pl.miloszgilga.lib.jmpsl.file.FileUtil.*;
import static pl.miloszgilga.lib.jmpsl.file.ContentType.*;

import static pl.miloszgilga.chessappbackend.utils.ImageUniquePrefix.BANNER;

//----------------------------------------------------------------------------------------------------------------------

@Service
class UserBannerImageService implements IUserBannerImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileImageService.class);

    private final EnvironmentVars environment;
    private final UserImageSftpService userImageSftpService;
    private final ILocalUserImagesRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    UserBannerImageService(EnvironmentVars environment, UserImageSftpService userImageSftpService,
                           ILocalUserImagesRepository repository) {
        this.environment = environment;
        this.userImageSftpService = userImageSftpService;
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public UpdatedImageResDto setUserBanner(final MultipartFile file, final Long userId) {
        isFileExist(file);
        final LocalUserImagesModel imagesModel = repository.findUserImagesByUserId(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to set user banner image from not existing user. User id: {}", userId);
            throw new AuthException.UserNotFoundException("Unable to load user data. Try again later.");
        });
        checkIfFileExtensionIsSupported(file, JPG, JPEG, PNG);
        BufferedImageSenderPayload imageSenderPayload;
        try {
            imageSenderPayload = BufferedImageSenderPayload.builder()
                    .imageUniquePrefix(BANNER.getImagePrefixName())
                    .bytesRepresentation(file.getBytes())
                    .preferredWidth(environment.getUserBannerImageWidth())
                    .preferredHeight(environment.getUserBannerImageHeight())
                    .userHashCode(imagesModel.getUserHashCode())
                    .id(userId)
                    .build();
        } catch (IOException ex) {
            LOGGER.error("Unable to set user banner image. Probaby image bytes data is corrupted. Data: {}", file);
            throw new UnableToSetImageException("Uploaded image is malformed or corrupted. Try again.");
        }
        final BufferedImageRes responsePayload = userImageSftpService.saveUserImage(imageSenderPayload);
        if (!imagesModel.getLocalUser().getOAuth2Supplier().equals(OAuth2Supplier.LOCAL)) {
            imagesModel.setUserHashCode(responsePayload.getUserHashCode());
        }
        imagesModel.setBannerImage(responsePayload.getLocation());
        return new UpdatedImageResDto(responsePayload.getLocation(), "Your banner image was successfully updated.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public SimpleServerMessageDto deleteUserBanner(final Long userId) {
        final LocalUserImagesModel imagesModel = repository.findUserImagesByUserId(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to remove user banner image from not existing user. User id: {}", userId);
            throw new AuthException.UserNotFoundException("Unable to load user data. Try again later.");
        });
        if (!isNull(imagesModel.getBannerImage())) {
            LOGGER.error("Attempt to delete non existing banner image. User images data: {}", imagesModel);
            throw new CustomUserBannerImageNotFoundException("Unable to delete banner image. Before deleting your " +
                    "banner image, check if banner image is acually set.");
        }
        final BufferedImageDeletePayload payload = BufferedImageDeletePayload.builder()
                .uniqueImagePrefix(BANNER.getImagePrefixName())
                .userHashCode(imagesModel.getUserHashCode())
                .id(userId)
                .build();
        userImageSftpService.deleteUserImage(payload);
        imagesModel.setBannerImage(null);
        return new SimpleServerMessageDto("Your banner image was successfully deleted.");
    }
}
