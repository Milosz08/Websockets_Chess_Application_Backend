/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: OtaTokenModel.java
 * Last modified: 26/09/2022, 23:20
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

package pl.miloszgilga.chessappbackend.network.ota_token.domain;

import lombok.*;

import javax.persistence.*;

import java.util.Date;
import java.io.Serializable;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import pl.miloszgilga.chessappbackend.token.OtaTokenType;
import pl.miloszgilga.chessappbackend.audit.AuditableEntity;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserModel;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "OTA_TOKEN_STORAGE")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtaTokenModel extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "OTA_TOKEN")         private String otaToken;
    @Column(name = "EXPIRATION_DATE")   private Date expirationDate;
    @Column(name = "USED_FOR")          private OtaTokenType usedFor;
    @Column(name = "ALREADY_USED")      private Boolean alreadyUsed;

    @ManyToOne(cascade = { MERGE, PERSIST }, fetch = LAZY)
    @JoinColumn(name = "LOCAL_USER_ID", referencedColumnName = "ID")
    private LocalUserModel localUser;

    //------------------------------------------------------------------------------------------------------------------

    OtaTokenModel(String otaToken, Date expirationDate, OtaTokenType usedFor, Boolean alreadyUsed) {
        this.otaToken = otaToken;
        this.expirationDate = expirationDate;
        this.usedFor = usedFor;
        this.alreadyUsed = alreadyUsed;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "OtaTokenModel{" +
                "otaToken='" + otaToken + '\'' +
                ", expirationDate=" + expirationDate +
                ", usedFor=" + usedFor.getOtaTokenTypeName() +
                ", alreadyUsed=" + alreadyUsed +
                ", userId=" + localUser.getId() +
                "} " + super.toString();
    }
}
