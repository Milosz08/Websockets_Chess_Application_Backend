/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: GoogleOAuth2UserInfo.java
 * Last modified: 21/09/2022, 01:29
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

package pl.miloszgilga.chessappbackend.oauth.user_info;

import java.util.Map;

//----------------------------------------------------------------------------------------------------------------------

class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getUsername() {
        return (String) attributes.get("name");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getEmailAddress() {
        return (String) attributes.get("email");
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String getUserImageUrl() {
        return (String) attributes.get("picture");
    }
}
