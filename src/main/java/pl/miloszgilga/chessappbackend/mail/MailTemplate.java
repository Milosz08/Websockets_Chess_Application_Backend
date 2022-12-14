/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: MailTemplate.java
 * Last modified: 05/09/2022, 15:30
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

package pl.miloszgilga.chessappbackend.mail;

import lombok.*;
import pl.miloszgilga.lib.jmpsl.mail.IMailEnumeratedTemplate;

//----------------------------------------------------------------------------------------------------------------------

@AllArgsConstructor
enum MailTemplate implements IMailEnumeratedTemplate {
    UNSUBSCRIBE_NEWSLETTER("unsubscribe-newsletter.template.ftl"),
    ACTIVATE_ACCOUNT("activate-account.template.ftl"),
    CHANGE_PASSWORD("change-password.template.ftl");

    //------------------------------------------------------------------------------------------------------------------

    private final String templateName;

    @Override
    public String getTemplateName() {
        return templateName;
    }
}
