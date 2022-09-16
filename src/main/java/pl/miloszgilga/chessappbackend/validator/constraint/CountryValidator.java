/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: CountryValidator.java
 * Last modified: 16/09/2022, 16:20
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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import pl.miloszgilga.chessappbackend.validator.annotation.ValidateCountry;
import pl.miloszgilga.chessappbackend.loader.country_data.CountryPropertiesLoader;

//----------------------------------------------------------------------------------------------------------------------

public class CountryValidator implements ConstraintValidator<ValidateCountry, String> {

    private final CountryPropertiesLoader loader;
    private List<String> availableCountries;

    public CountryValidator(CountryPropertiesLoader loader) {
        this.loader = loader;
    }

    @Override
    public void initialize(ValidateCountry constraintAnnotation) {
        this.availableCountries = loader.getLoadedData().getCountries().stream()
                .map(x -> x.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String countryName, ConstraintValidatorContext context) {
        return availableCountries.contains(countryName);
    }
}
