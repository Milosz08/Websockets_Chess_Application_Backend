/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ILocalUserRepository.java
 * Last modified: 11/09/2022, 01:44
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

package pl.miloszgilga.chessappbackend.network.auth.domain;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//----------------------------------------------------------------------------------------------------------------------

@Repository
public interface ILocalUserRepository extends JpaRepository<LocalUserModel, Long> {

    @Query(value = "SELECT m FROM LocalUserModel m WHERE m.nickname=:nickname")
    Optional<LocalUserModel> findUserByNickname(@Param("nickname") String nickname);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT COUNT(m) > 0 FROM LocalUserModel m WHERE m.nickname=:nickname")
    Boolean checkIfUserByNicknameExist(@Param("nickname") String nickname);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT m FROM LocalUserModel m WHERE m.emailAddress=:emailAddress")
    Optional<LocalUserModel> findUserByEmailAddress(@Param("emailAddress") String emailAddress);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT COUNT(m) > 0 FROM LocalUserModel m WHERE m.emailAddress=:emailAddress")
    Boolean checkIfUserByEmailAddressExist(@Param("emailAddress") String emailAddress);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT m FROM LocalUserModel m " +
            "WHERE m.emailAddress=:emailAddress " +
            "AND m.nickname=:nickname " +
            "AND m.id=:id")
    Optional<LocalUserModel> findUserByEmailAddressNicknameAndId(@Param("emailAddress") String emailAddress,
                                                                 @Param("nickname") String nickname,
                                                                 @Param("id") Long id);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT COUNT(m) > 0 FROM LocalUserModel m INNER JOIN m.localUserDetails d " +
            "WHERE m.emailAddress=:emailAddress " +
            "OR d.secondEmailAddress=:emailAddress")
    Boolean checkIfUserHasEmailOrSecondEmail(@Param("emailAddress") String emailAddress);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT m FROM LocalUserModel m WHERE m.id=:userId")
    Optional<LocalUserModel> findUserById(@Param("userId") Long userId);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT m FROM LocalUserModel m " +
            "WHERE m.nickname=:nicknameOrEmail " +
            "OR m.emailAddress=:nicknameOrEmail")
    Optional<LocalUserModel> findUserByNickOrEmail(@Param("nicknameOrEmail") String nicknameOrEmail);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT COUNT(m) > 0 FROM LocalUserModel m " +
            "WHERE m.id=:userId " +
            "AND m.nickname=:nickname")
    Boolean checkIfUserByIdAndNicknameExist(@Param("userId") Long userId, @Param("nickname") String nickname);
}
