/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: RefreshTokenModel.java
 * Last modified: 11/09/2022, 01:54
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

package pl.miloszgilga.chessappbackend.network.auth.domain;

import lombok.*;

import javax.persistence.*;

import java.util.Date;
import java.io.Serializable;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import pl.miloszgilga.chessappbackend.audit.AuditableEntity;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "USER_REFRESH_TOKEN")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenModel extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "REFRESH_TOKEN")     private String refreshToken;
    @Column(name = "EXPIRED_AT")        private Date expiredAt;

    @OneToOne(cascade = { PERSIST, MERGE }, fetch = LAZY)
    @JoinColumn(name = "LOCAL_USER_ID", referencedColumnName = "ID")
    private LocalUserModel localUser;

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "RefreshTokenModel{" +
                "refreshToken='" + refreshToken + '\'' +
                ", expiredDate=" + expiredAt +
                ", localUser=" + localUser +
                "} " + super.toString();
    }
}
