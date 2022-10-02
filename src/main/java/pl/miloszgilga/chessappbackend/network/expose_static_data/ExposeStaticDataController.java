/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ExposeStaticDataController.java
 * Last modified: 15/09/2022, 17:32
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

package pl.miloszgilga.chessappbackend.network.expose_static_data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pl.miloszgilga.chessappbackend.network.expose_static_data.dto.*;

import static pl.miloszgilga.chessappbackend.config.ApplicationEndpoints.*;

//----------------------------------------------------------------------------------------------------------------------

@RestController
@RequestMapping(EXPOSE_STATIC_DATA_ENDPOINT)
class ExposeStaticDataController {

    private final ExposeStaticDataService service;

    //------------------------------------------------------------------------------------------------------------------

    ExposeStaticDataController(ExposeStaticDataService service) {
        this.service = service;
    }

    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(SIGNUP_CALENDAR_DATA)
    ResponseEntity<SignupCalendarDataResDto> getSignupCalendarData() {
        return new ResponseEntity<>(service.getSignupCalendarData(), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(SIGNUP_GENDER_DATA)
    ResponseEntity<SignupGenderDataResDto> getSignupGenderData() {
        return new ResponseEntity<>(service.getSignupGenderData(), HttpStatus.OK);
    }

    //------------------------------------------------------------------------------------------------------------------

    @GetMapping(SIGNUP_COUNTRY_DATA)
    ResponseEntity<SignupCountryDataResDto> getSignupCountryData() {
        return new ResponseEntity<>(service.getSignupCountryData(), HttpStatus.OK);
    }
}
