/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: INewsletterService.java
 * Last modified: 31/08/2022, 15:21
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

package pl.miloszgilga.chessappbackend.network.newsletter_email;

import java.net.URI;

import pl.miloszgilga.chessappbackend.dto.*;
import pl.miloszgilga.chessappbackend.network.newsletter_email.dto.*;

//----------------------------------------------------------------------------------------------------------------------

interface INewsletterEmailService {
    SimpleServerMessageDto subscribeNewsletter(final EmailNewsletterReqDto emailAddress);
    SimpleServerMessageDto attemptToUnsubscribeNewsletter(final AttemptToUnsubscribeReqDto email);
    SimpleServerMessageDto unsubscribeNewsletterViaOta(final SimpleOtaTokenReqDto token);
    URI unsubscribeNewsletterViaLink(final String bearer);
    SimpleServerMessageDto resendVerificationEmailLink(final ResendEmailMessageReqDto req);
}
