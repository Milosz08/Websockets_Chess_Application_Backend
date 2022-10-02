/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: GlobalCorsHeadersFilter.java
 * Last modified: 28/09/2022, 13:51
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

package pl.miloszgilga.chessappbackend.filter;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import pl.miloszgilga.chessappbackend.config.EnvironmentVars;

//----------------------------------------------------------------------------------------------------------------------

@Component
@Order(HIGHEST_PRECEDENCE)
public class GlobalCorsHeadersFilter implements Filter {

    private final EnvironmentVars environment;
    private String flattedMethods;

    private static final HttpMethod[] REST_METHODS = { GET, POST, PUT, OPTIONS, PATCH, DELETE };

    private static final String[] ALLOW_HEADERS = {
            "x-requested-with", "authorization", "Content-Type", "Authorization", "credential", "X-XSRF-TOKEN",
    };

    //------------------------------------------------------------------------------------------------------------------

    public GlobalCorsHeadersFilter(EnvironmentVars environment) {
        this.environment = environment;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void init(FilterConfig filterConfig) {
        this.flattedMethods = Arrays.stream(REST_METHODS).map(Enum::name).collect(Collectors.joining(","));
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse response = (HttpServletResponse) res;
        final HttpServletRequest request = (HttpServletRequest) req;

        response.setHeader("Access-Control-Allow-Origin", environment.getFrontEndUrl());
        response.setHeader("Access-Control-Allow-Methods", flattedMethods);
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", String.join(",", ALLOW_HEADERS));

        if (OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void destroy() {
    }
}
