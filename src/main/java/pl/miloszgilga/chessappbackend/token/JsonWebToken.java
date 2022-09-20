/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: JsonWebToken.java
 * Last modified: 02/09/2022, 22:22
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

package pl.miloszgilga.chessappbackend.token;

import lombok.Getter;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import java.security.Key;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

//----------------------------------------------------------------------------------------------------------------------

@Getter
@Component
class JsonWebToken {

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final byte[] apiKeySecretBytes;

    JsonWebToken(EnvironmentVars environment) {
        this.apiKeySecretBytes = DatatypeConverter.parseBase64Binary(environment.getJwtSecretKey());
    }

    Key getSignatureKey() {
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }
}
