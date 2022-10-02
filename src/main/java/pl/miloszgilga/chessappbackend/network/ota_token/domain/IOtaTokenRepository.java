/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: IOtaTokenRepository.java
 * Last modified: 26/09/2022, 23:29
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

package pl.miloszgilga.chessappbackend.network.ota_token.domain;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import pl.miloszgilga.chessappbackend.token.OtaTokenType;

//----------------------------------------------------------------------------------------------------------------------

@Repository
public interface IOtaTokenRepository extends JpaRepository<OtaTokenModel, Long> {

    @Query(value = "SELECT m FROM OtaTokenModel m INNER JOIN m.localUser u INNER JOIN m.localUser.localUserDetails d " +
            "WHERE m.alreadyUsed=false " +
            "AND m.otaToken=:otaToken " +
            "AND m.userFor=:usedFor " +
            "AND (u.emailAddress=:emailAddress OR d.secondEmailAddress=:emailAddress)")
    Optional<OtaTokenModel> findTokenByUserEmailOrSecondEmailAddress(@Param("emailAddress") String emailAddress,
                                                                     @Param("usedFor") OtaTokenType usedFor,
                                                                     @Param("otaToken") String otaToken);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT COUNT(m) > 0 FROM OtaTokenModel m " +
            "WHERE m.otaToken=:token " +
            "AND m.userFor=:usedFor")
    Boolean checkIfOtaTokenExist(@Param("token") String token, @Param("usedFor") OtaTokenType usedFor);

    //------------------------------------------------------------------------------------------------------------------

    @Query(value = "SELECT m FROM OtaTokenModel m " +
            "WHERE m.otaToken=:token " +
            "AND m.userFor=:usedFor")
    Optional<OtaTokenModel> findTokenBasedValueAndUsed(@Param("token") String token, @Param("usedFor") OtaTokenType usedFor);
}
