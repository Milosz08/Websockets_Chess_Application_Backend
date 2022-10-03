/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: NetworkHelper.java
 * Last modified: 02/10/2022, 16:11
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

package pl.miloszgilga.chessappbackend.utils;

import org.javatuples.Pair;

import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import javax.servlet.http.HttpServletRequest;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class NetworkHelper {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final EnvironmentVars environment;

    //------------------------------------------------------------------------------------------------------------------

    public NetworkHelper(EnvironmentVars environment) {
        this.environment = environment;
    }

    //------------------------------------------------------------------------------------------------------------------

    public URI generateRedirectUri(Pair<String, String> queryParam, String subpage, boolean ifError) {
        return UriComponentsBuilder.fromUriString(environment.getFrontEndUrl() + subpage)
                .queryParam(queryParam.getValue0(), queryParam.getValue1())
                .queryParam("error", Boolean.toString(ifError))
                .build()
                .toUri();
    }

    //------------------------------------------------------------------------------------------------------------------

    public String extractJwtTokenFromRequest(HttpServletRequest req) {
        final String bearerToken = req.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return "";
    }
}
