/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LocalUserRoleModel.java
 * Last modified: 11/09/2022, 01:45
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

import lombok.NoArgsConstructor;

import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.ManyToMany;

import java.util.Set;
import java.io.Serializable;

import pl.miloszgilga.chessappbackend.audit.AuditableEntity;
import pl.miloszgilga.chessappbackend.security.LocalUserRole;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@Table(name = "LOCAL_USER_ROLE")
@NoArgsConstructor
public class LocalUserRoleModel extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "ROLE_NAME")        private LocalUserRole roleName;
    @ManyToMany(mappedBy = "roles")    private Set<LocalUserModel> users;

    public LocalUserRoleModel(LocalUserRole roleName) {
        this.roleName = roleName;
    }

    public LocalUserRole getRoleName() {
        return roleName;
    }

    void setRoleName(LocalUserRole roleName) {
        this.roleName = roleName;
    }

    Set<LocalUserModel> getUsers() {
        return users;
    }

    void setUsers(Set<LocalUserModel> userRoles) {
        this.users = userRoles;
    }

    @Override
    public String toString() {
        return "LocalUserRoleModel{" +
                "roleName=" + roleName +
                ", role=" + users +
                "} " + super.toString();
    }
}
