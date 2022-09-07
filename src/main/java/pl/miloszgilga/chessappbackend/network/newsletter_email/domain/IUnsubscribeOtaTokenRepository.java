/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: IUnsubscribeTokenRepository.java
 * Last modified: 02/09/2022, 16:59
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

package pl.miloszgilga.chessappbackend.network.newsletter_email.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//----------------------------------------------------------------------------------------------------------------------

public interface IUnsubscribeOtaTokenRepository extends JpaRepository<UnsubscribeOtaTokenModel, Long> {

    @Query(value = "SELECT COUNT(m) > 0 FROM UnsubscribeOtaTokenModel m WHERE m.token=:token")
    boolean findExistingToken(@Param("token") String token);

    @Query(value = "SELECT m FROM UnsubscribeOtaTokenModel m WHERE m.token=:token AND m.userEmail=:email AND m.alreadyUsed=false")
    Optional<UnsubscribeOtaTokenModel> checkIfTokenExist(@Param("token") String token, @Param("email") String email);
}