/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LocalUserImageModel.java
 * Last modified: 27/10/2022, 02:45
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

package pl.miloszgilga.chessappbackend.network.user_images.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;

import pl.miloszgilga.chessappbackend.audit.AuditableEntity;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "LOCAL_USER_IMAGES")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalUserImagesModel extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "HAS_AVATAR_IMAGE")                private Boolean hasAvatarImage;
    @Column(name = "AVATAR_IMAGE_REFER_LINK")         private String avatarImage;
    @Column(name = "HAS_BANNER_IMAGE")                private Boolean hasBannerImage;
    @Column(name = "BANNER_IMAGE_REFER_LINK")         private String bannerImage;
    @Column(name = "USER_HASH_CODE")                  private String userHashCode;
    @Column(name = "DEF_AVATAR_COLOR")                private String defAvatarColor;

    @OneToOne(cascade = { PERSIST, MERGE }, fetch = LAZY)
    @JoinColumn(name = "LOCAL_USER_ID", referencedColumnName = "ID")
    private LocalUserModel localUser;

    //------------------------------------------------------------------------------------------------------------------

    LocalUserImagesModel(Boolean hasAvatarImage, String avatarImage, Boolean hasBannerImage, String bannerImage,
                         String userHashCode, String defAvatarColor) {
        this.hasAvatarImage = hasAvatarImage;
        this.avatarImage = avatarImage;
        this.hasBannerImage = hasBannerImage;
        this.bannerImage = bannerImage;
        this.userHashCode = userHashCode;
        this.defAvatarColor = defAvatarColor;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "LocalUserImagesModel{" +
                "hasAvatarImage=" + hasAvatarImage +
                ", avatarImage='" + avatarImage + '\'' +
                ", hasBannerImage=" + hasBannerImage +
                ", bannerImage='" + bannerImage + '\'' +
                ", userHashCode='" + userHashCode + '\'' +
                ", defAvatarColor='" + defAvatarColor + '\'' +
                ", userId='" + localUser.getId() + '\'' +
                "} " + super.toString();
    }
}
