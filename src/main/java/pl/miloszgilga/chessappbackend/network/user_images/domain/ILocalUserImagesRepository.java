/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ILocalUserImagesRepository.java
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

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//----------------------------------------------------------------------------------------------------------------------

@Repository
public interface ILocalUserImagesRepository extends JpaRepository<LocalUserImagesModel, Long> {

    @Query(value = "SELECT m FROM LocalUserImagesModel m INNER JOIN m.localUser u " +
            "WHERE u.nickname=:nickname")
    Optional<LocalUserImagesModel> findUserImagesByNickname(@Param("nickname") String nickname);
}
