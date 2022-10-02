/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ValidateMultipleEmails.java
 * Last modified: 01/10/2022, 12:57
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

package pl.miloszgilga.chessappbackend.validator.annotation;

import javax.validation.*;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import pl.miloszgilga.chessappbackend.validator.constraint.MultipleEmailsValidator;

//----------------------------------------------------------------------------------------------------------------------

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = MultipleEmailsValidator.class)
@Documented
public @interface ValidateMultipleEmails {
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
