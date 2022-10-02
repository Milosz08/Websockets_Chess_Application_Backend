/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OAuth2CredentialsSupplierValidator.java
 * Last modified: 23/09/2022, 19:23
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

package pl.miloszgilga.chessappbackend.validator.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import javax.validation.*;

import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;
import pl.miloszgilga.chessappbackend.validator.annotation.ValidateOAuth2Supplier;

//----------------------------------------------------------------------------------------------------------------------

public class OAuth2SupplierValidator implements ConstraintValidator<ValidateOAuth2Supplier, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2SupplierValidator.class);

    private Set<String> availableSuppliers = new HashSet<>();

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void initialize(ValidateOAuth2Supplier constraintAnnotation) {
        availableSuppliers = CredentialsSupplier.getOAuth2Suppliers();
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean isValid(String supplierName, ConstraintValidatorContext context) {
        if (!availableSuppliers.contains(supplierName)) {
            LOGGER.error("Attempt refer to unexisting OAuth2 credentials supplier name. Supplier name: {}", supplierName);
            return false;
        }
        return true;
    }
}
