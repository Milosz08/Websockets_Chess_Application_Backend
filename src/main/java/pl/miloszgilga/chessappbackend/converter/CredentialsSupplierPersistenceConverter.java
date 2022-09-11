/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: CredentialsSupplierPersistenceConverter.java
 * Last modified: 11/09/2022, 02:52
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

package pl.miloszgilga.chessappbackend.converter;

import javax.persistence.Converter;
import javax.persistence.AttributeConverter;

import java.util.stream.Stream;

import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;

//----------------------------------------------------------------------------------------------------------------------

@Converter(autoApply = true)
public class CredentialsSupplierPersistenceConverter implements AttributeConverter<CredentialsSupplier, String> {

    @Override
    public String convertToDatabaseColumn(CredentialsSupplier attribute) {
        if (attribute == null) return null;
        return attribute.getSupplier();
    }

    @Override
    public CredentialsSupplier convertToEntityAttribute(String supplierName) {
        if (supplierName == null) return null;
        return Stream.of(CredentialsSupplier.values())
                .filter(s -> s.getSupplier().equals(supplierName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
