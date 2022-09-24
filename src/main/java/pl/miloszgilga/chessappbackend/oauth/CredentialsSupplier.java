/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: CredentialsProvider.java
 * Last modified: 11/09/2022, 02:00
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

package pl.miloszgilga.chessappbackend.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import pl.miloszgilga.chessappbackend.exception.custom.AuthException;

//----------------------------------------------------------------------------------------------------------------------

@Getter
@AllArgsConstructor
public enum CredentialsSupplier {
    GOOGLE("google"),
    FACEBOOK("facebook"),
    LOCAL("local");

    private final String supplier;
    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsSupplier.class);

    public static Set<String> getAllSuppliers() {
        return Stream.of(CredentialsSupplier.values())
                .filter(s -> s.supplier.equals(LOCAL.supplier))
                .map(CredentialsSupplier::getSupplier)
                .collect(Collectors.toSet());
    }

    public static CredentialsSupplier findSupplierBasedRegistrationId(String registrationId) {
        return Stream.of(CredentialsSupplier.values())
                .filter(s -> s.supplier.equalsIgnoreCase(registrationId))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("Passed registration id: {} is not valid credentials supplier name.", registrationId);
                    throw new AuthException.OAuth2CredentialsSupplierMalformedException(
                            "Passed registration id: %s is not valid credentials supplier name.", registrationId);
                });
    }

    public static Set<String> getOAuth2Suppliers() {
        return Stream.of(CredentialsSupplier.values())
                .filter(s -> !s.equals(LOCAL))
                .map(CredentialsSupplier::getSupplier)
                .collect(Collectors.toSet());
    }
}
