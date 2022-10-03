/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LocalUserModel.java
 * Last modified: 11/09/2022, 01:43
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

import java.util.*;
import java.io.Serializable;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import pl.miloszgilga.chessappbackend.audit.AuditableEntity;
import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;
import pl.miloszgilga.chessappbackend.network.ota_token.domain.OtaTokenModel;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "LOCAL_USER")
@Getter @Setter
@NoArgsConstructor
public class LocalUserModel extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "NICKNAME")              private String nickname;
    @Column(name = "FIRST_NAME")            private String firstName;
    @Column(name = "LAST_NAME")             private String lastName;
    @Column(name = "EMAIL_ADDRESS")         private String emailAddress;
    @Column(name = "PASSWORD")              private String password;
    @Column(name = "CREDENTIALS_SUPPLIER")  private CredentialsSupplier credentialsSupplier;
    @Column(name = "SUPPLIED_USER_ID")      private String supplierUserId;
    @Column(name = "IS_ACTIVATED")          private Boolean isActivated;
    @Column(name = "IS_BLOCKED")            private Boolean isBlocked;

    @OneToOne(fetch = LAZY, mappedBy = "localUser", cascade = { PERSIST, MERGE })
    private RefreshTokenModel refreshToken;

    @OneToMany(fetch = LAZY, mappedBy = "localUser", cascade = { PERSIST, MERGE })
    private Set<OtaTokenModel> otaTokens = new HashSet<>();

    @OneToOne(fetch = LAZY, mappedBy = "localUser", cascade = { PERSIST, MERGE })
    private LocalUserDetailsModel localUserDetails;

    @ManyToMany(cascade = { PERSIST, MERGE }, fetch = LAZY)
    @JoinTable(name = "LOCAL_USER_ROLE_BINDING",
            joinColumns = { @JoinColumn(name = "LOCAL_USER_ID") },
            inverseJoinColumns = { @JoinColumn(name = "LOCAL_USER_ROLE_ID") })
    private Set<LocalUserRoleModel> roles;

    //------------------------------------------------------------------------------------------------------------------

    public LocalUserModel(String nickname, String firstName, String lastName, String emailAddress, String password,
                          CredentialsSupplier credentialsSupplier, String supplierUserId, Boolean isActivated,
                          Boolean isBlocked) {
        this.nickname = nickname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.credentialsSupplier = credentialsSupplier;
        this.supplierUserId = supplierUserId;
        this.isActivated = isActivated;
        this.isBlocked = isBlocked;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "LocalUserModel{" +
                "nickname='" + nickname + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", credentialsSupplier=" + credentialsSupplier +
                ", supplierUserId='" + supplierUserId + '\'' +
                ", isActivated=" + isActivated +
                ", isBlocked=" + isBlocked +
                "} ";
    }
}
