/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: RenewCredentialsModel.java
 * Last modified: 11/09/2022, 01:57
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

package pl.miloszgilga.chessappbackend.network.renew_credentials.domain;

import java.io.Serializable;

import javax.persistence.*;

import lombok.NoArgsConstructor;
import pl.miloszgilga.chessappbackend.audit.AuditableEntity;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "RENEW_CREDENTIALS_OTA_TOKEN")
@NoArgsConstructor
public class RenewCredentialsOtaTokenModel extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "OTA_TOKEN")         private String otaToken;
    @Column(name = "EXPIRATION_DATE")   private String expirationDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "LOCAL_USER_ID", referencedColumnName = "ID")
    private LocalUserModel localUser;

    RenewCredentialsOtaTokenModel(String otaToken, String expirationDate) {
        this.otaToken = otaToken;
        this.expirationDate = expirationDate;
    }

    String getOtaToken() {
        return otaToken;
    }

    void setOtaToken(String otaToken) {
        this.otaToken = otaToken;
    }

    String getExpirationDate() {
        return expirationDate;
    }

    void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    LocalUserModel getLocalUser() {
        return localUser;
    }

    void setLocalUser(LocalUserModel localUser) {
        this.localUser = localUser;
    }

    @Override
    public String toString() {
        return "RenewCredentialsModel{" +
                "otaToken='" + otaToken + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", localUser=" + localUser +
                "} " + super.toString();
    }
}
