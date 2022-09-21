/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: CookieHelper.java
 * Last modified: 21/09/2022, 02:21
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

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Base64;
import java.util.Optional;

//----------------------------------------------------------------------------------------------------------------------

@Component
public class CookieHelper {

    public Optional<Cookie> getCookie(HttpServletRequest req, String cookieName) {
        final Cookie[] cookies = req.getCookies();
        if (cookies == null || cookies.length == 0) return Optional.empty();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) return Optional.of(cookie);
        }
        return Optional.empty();
    }

    public void addCookie(HttpServletResponse res, String cookieName, String cookieValue, int cookieMaxAge) {
        final Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(cookieMaxAge);
        res.addCookie(cookie);
    }

    public void deleteCookie(HttpServletRequest req, HttpServletResponse res, String cookieName) {
        final Cookie[] cookies = req.getCookies();
        if (cookies == null || cookies.length == 0) return;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                res.addCookie(cookie);
            }
        }
    }

    public String serializeCookieObjectData(Object cookieObjectData) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(cookieObjectData));
    }

    public <T> T deserializeCookieObjectData(Cookie cookie, Class<T> cookieClazz) {
        return cookieClazz.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }

    public Optional<String> getCookieValue(HttpServletRequest req, String cookieName) {
        return getCookie(req, cookieName).map(Cookie::getValue);
    }
}
