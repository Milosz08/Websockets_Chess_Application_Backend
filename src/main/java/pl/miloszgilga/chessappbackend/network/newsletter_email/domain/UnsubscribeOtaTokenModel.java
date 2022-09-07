/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: UnsubscribeTokenModel.java
 * Last modified: 02/09/2022, 16:50
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

import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Entity;

import pl.miloszgilga.chessappbackend.audit.AuditableEntity;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "UNSUBSCRIBE_NEWSLETTER_OTA")
@NoArgsConstructor
public class UnsubscribeOtaTokenModel extends AuditableEntity {

    @Column(name = "USER_EMAIL")        private String userEmail;
    @Column(name = "TOKEN")             private String token;
    @Column(name = "TOKEN_EXPIRED")     private Date tokenExpired;
    @Column(name = "ALREADY_USED")      private boolean alreadyUsed;

    public UnsubscribeOtaTokenModel(String userEmail, String token, Date tokenExpired) {
        this.userEmail = userEmail;
        this.token = token;
        this.tokenExpired = tokenExpired;
    }

    String getUserEmail() {
        return userEmail;
    }

    void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = token;
    }

    public Date getTokenExpired() {
        return tokenExpired;
    }

    void setTokenExpired(Date tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    boolean isAlreadyUsed() {
        return alreadyUsed;
    }

    public void setAlreadyUsed(boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    @Override
    public String toString() {
        return "UnsubscribeOtaTokenModel{" +
                "userEmail='" + userEmail + '\'' +
                ", token='" + token + '\'' +
                ", tokenExpired=" + tokenExpired +
                ", alreadyUsed=" + alreadyUsed +
                "} " + super.toString();
    }
}