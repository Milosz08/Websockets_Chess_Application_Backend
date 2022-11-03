/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 *  File name: UserAvatarService.java
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

import java.awt.*;
import java.io.IOException;
import javax.transaction.Transactional;

import pl.miloszgilga.lib.jmpsl.gfx.sender.*;
import pl.miloszgilga.lib.jmpsl.gfx.generator.*;
import pl.miloszgilga.lib.jmpsl.core.StringUtil;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;
import pl.miloszgilga.chessappbackend.network.user_images.domain.*;
import pl.miloszgilga.chessappbackend.exception.custom.ImageException.*;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;
import pl.miloszgilga.chessappbackend.network.user_images.dto.UpdatedImageResDto;
import pl.miloszgilga.chessappbackend.exception.custom.AuthException.UserNotFoundException;

import static pl.miloszgilga.lib.jmpsl.file.FileUtil.*;
import static pl.miloszgilga.lib.jmpsl.file.ContentType.*;

import static pl.miloszgilga.chessappbackend.utils.ImageUniquePrefix.PROFILE;

//----------------------------------------------------------------------------------------------------------------------

@Service
class UserProfileImageService implements IUserProfileImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileImageService.class);

    private final EnvironmentVars environment;
    private final UserImageSftpService userImageSftpService;
    private final ILocalUserImagesRepository repository;

    //------------------------------------------------------------------------------------------------------------------

    UserProfileImageService(EnvironmentVars environment, UserImageSftpService userImageSftpService,
                                   ILocalUserImagesRepository repository) {
        this.environment = environment;
        this.userImageSftpService = userImageSftpService;
        this.repository = repository;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public UpdatedImageResDto setUserProfileImage(final MultipartFile file, final Long userId) {
        isFileExist(file);
        final LocalUserImagesModel imagesModel = repository.findUserImagesByUserId(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to set custom profile image from not existing user. User id: {}", userId);
            throw new UserNotFoundException("Unable to load user data. Try again later.");
        });
        checkIfFileExtensionIsSupported(file, JPG, JPEG, PNG);
        BufferedImageSenderPayload imageSenderPayload;
        try {
            imageSenderPayload = BufferedImageSenderPayload.builder()
                    .imageUniquePrefix(PROFILE.getImagePrefixName())
                    .bytesRepresentation(file.getBytes())
                    .preferredWidth(environment.getUserProfileImageSize())
                    .preferredHeight(environment.getUserProfileImageSize())
                    .userHashCode(imagesModel.getUserHashCode())
                    .id(userId)
                    .build();
        } catch (IOException ex) {
            LOGGER.error("Unable to set user profile image. Probaby image bytes data is corrupted. Data: {}", file);
            throw new UnableToSetImageException("Uploaded image is malformed or corrupted. Try again.");
        }
        final BufferedImageRes responsePayload = userImageSftpService.saveUserImage(imageSenderPayload);
        imagesModel.setHasProfileImage(true);
        imagesModel.setProfileImage(responsePayload.getLocation());
        return new UpdatedImageResDto(responsePayload.getLocation(), "Your profile image was successfully updated.");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public UpdatedImageResDto deleteUserProfileImage(final Long userId) {
        final LocalUserImagesModel imagesModel = repository.findUserImagesByUserId(userId).orElseThrow(() -> {
            LOGGER.error("Attempt to delete custom profile image from not existing user. User id: {}", userId);
            throw new UserNotFoundException("Unable to load user data. Try again later.");
        });
        if (!imagesModel.getHasProfileImage()) {
            LOGGER.error("Attempt to delete default user profile image. User images data: {}", imagesModel);
            throw new CustomUserProfileImageNotFoundException("Unable to delete profile image. Before deleting " +
                    "your profile image, check if your image is not default.");
        }
        final LocalUserModel userModel = imagesModel.getLocalUser();
        final BufferedImageGeneratorPayload payload = BufferedImageGeneratorPayload.builder()
                .imageUniquePrefix(PROFILE.getImagePrefixName())
                .userHashCode(imagesModel.getUserHashCode())
                .size(environment.getUserProfileImageSize())
                .fontSize(environment.getUserProfileImageFontSize())
                .initials(StringUtil.initialsAsCharsArray(userModel.getFirstName(), userModel.getLastName()))
                .preferredColor(Color.decode(imagesModel.getDefProfileImageColor()))
                .id(userId)
                .build();
        BufferedImageGeneratorRes responsePayload = userImageSftpService.generateAndSaveDefaultUserImage(payload);
        imagesModel.setHasProfileImage(false);
        imagesModel.setProfileImage(responsePayload.getBufferedImageRes().getLocation());
        LOGGER.info("Successful remove user profile image. User image data: {}", imagesModel);
        return new UpdatedImageResDto(responsePayload.getBufferedImageRes().getLocation(),
                "Your profile image was successfully deleted.");
    }
}
