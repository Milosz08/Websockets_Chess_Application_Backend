/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: NewsletterModel.java
 * Last modified: 02/09/2022, 16:42
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

package pl.miloszgilga.chessappbackend.network.newsletter_email.domain;

import lombok.*;

import java.util.*;
import javax.persistence.*;
import java.io.Serializable;

import pl.miloszgilga.chessappbackend.audit.AuditableEntity;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.LAZY;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "NEWSLETTER_EMAIL")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterEmailModel extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "USER_NAME")     private String userName;
    @Column(name = "USER_EMAIL")    private String userEmail;

    @OneToMany(fetch = LAZY, mappedBy = "newsletterUser", cascade = { PERSIST, MERGE, REMOVE })
    private Set<UnsubscribeOtaTokenModel> otaTokens = new HashSet<>();

    //------------------------------------------------------------------------------------------------------------------

    public NewsletterEmailModel(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "NewsletterEmailModel{" +
                "userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                "} " + super.toString();
    }
}
