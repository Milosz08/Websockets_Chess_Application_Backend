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

package pl.miloszgilga.chessappbackend.network.auth_local.domain;

import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.Date;
import java.io.Serializable;

import javax.persistence.*;

import pl.miloszgilga.chessappbackend.utils.UserGenderSpecific;
import pl.miloszgilga.chessappbackend.audit.AuditableEntity;
import pl.miloszgilga.chessappbackend.oauth.CredentialsSupplier;
import pl.miloszgilga.chessappbackend.network.renew_credentials.domain.RenewCredentialsOtaTokenModel;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "LOCAL_USER")
@NoArgsConstructor
public class LocalUserModel extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "NICKNAME")              private String nickname;
    @Column(name = "NAME")                  private String name;
    @Column(name = "SURNAME")               private String surname;
    @Column(name = "EMAIL_ADDRESS")         private String emailAddress;
    @Column(name = "SECOND_EMAIL_ADDRESS")  private String secondEmailAddress;
    @Column(name = "PASSWORD")              private String password;
    @Column(name = "BIRTH_DATE")            private Date birthDate;
    @Column(name = "PHONE_NUMBER")          private String phoneNumber;
    @Column(name = "GENDER")                private UserGenderSpecific gender;
    @Column(name = "HAS_PHOTO")             private boolean hasPhoto;
    @Column(name = "CREDENTIALS_SUPPLIER")  private CredentialsSupplier credentialsSupplier;
    @Column(name = "HAS_NEWSLETTER_ACCEPT") private boolean hasNewsletterAccept;

    @OneToOne(mappedBy = "localUser")       private RefreshTokenModel refreshToken;
    @OneToOne(mappedBy = "localUser")       private RenewCredentialsOtaTokenModel renewCredentials;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "LOCAL_USER_ROLE_BINDING",
            joinColumns = { @JoinColumn(name = "LOCAL_USER_ID") },
            inverseJoinColumns = { @JoinColumn(name = "LOCAL_USER_ROLE_ID") })
    private Set<LocalUserRoleModel> roles;

    LocalUserModel(
            String nickname, String name, String surname, String emailAddress, String secondEmailAddress,
            String password, Date birthDate, String phoneNumber, UserGenderSpecific gender, boolean hasPhoto,
            CredentialsSupplier credentialsSupplier, boolean hasNewsletterAccept
    ) {
        this.nickname = nickname;
        this.name = name;
        this.surname = surname;
        this.emailAddress = emailAddress;
        this.secondEmailAddress = secondEmailAddress;
        this.password = password;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.hasPhoto = hasPhoto;
        this.credentialsSupplier = credentialsSupplier;
        this.hasNewsletterAccept = hasNewsletterAccept;
    }

    String getNickname() {
        return nickname;
    }

    void setNickname(String nickname) {
        this.nickname = nickname;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getSurname() {
        return surname;
    }

    void setSurname(String surname) {
        this.surname = surname;
    }

    String getEmailAddress() {
        return emailAddress;
    }

    void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    String getSecondEmailAddress() {
        return secondEmailAddress;
    }

    void setSecondEmailAddress(String secondEmailAddress) {
        this.secondEmailAddress = secondEmailAddress;
    }

    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    Date getBirthDate() {
        return birthDate;
    }

    void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    UserGenderSpecific getGender() {
        return gender;
    }

    void setGender(UserGenderSpecific sex) {
        this.gender = sex;
    }

    boolean isHasPhoto() {
        return hasPhoto;
    }

    void setHasPhoto(boolean hasPhoto) {
        this.hasPhoto = hasPhoto;
    }

    CredentialsSupplier getCredentialsSupplier() {
        return credentialsSupplier;
    }

    void setCredentialsSupplier(CredentialsSupplier credentialsSupplier) {
        this.credentialsSupplier = credentialsSupplier;
    }

    RefreshTokenModel getRefreshToken() {
        return refreshToken;
    }

    void setRefreshToken(RefreshTokenModel refreshToken) {
        this.refreshToken = refreshToken;
    }

    Set<LocalUserRoleModel> getRoles() {
        return roles;
    }

    void setRoles(Set<LocalUserRoleModel> roles) {
        this.roles = roles;
    }

    RenewCredentialsOtaTokenModel getRenewCredentials() {
        return renewCredentials;
    }

    void setRenewCredentials(RenewCredentialsOtaTokenModel renewCredentials) {
        this.renewCredentials = renewCredentials;
    }

    boolean isHasNewsletterAccept() {
        return hasNewsletterAccept;
    }

    void setHasNewsletterAccept(boolean hasNewsletterAccept) {
        this.hasNewsletterAccept = hasNewsletterAccept;
    }

    @Override
    public String toString() {
        return "LocalUserModel{" +
                "nickname='" + nickname + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", secondEmailAddress='" + secondEmailAddress + '\'' +
                ", password='" + password + '\'' +
                ", birthDate=" + birthDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", gender=" + gender +
                ", hasPhoto=" + hasPhoto +
                ", credentialsSupplier=" + credentialsSupplier +
                ", hasNewsletterAccept=" + hasNewsletterAccept +
                ", refreshToken=" + refreshToken +
                ", renewCredentials=" + renewCredentials +
                ", roles=" + roles +
                "} " + super.toString();
    }
}
