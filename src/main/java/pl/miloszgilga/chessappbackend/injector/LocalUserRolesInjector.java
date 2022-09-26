/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: LocalUserRolesLoader.java
 * Last modified: 11/09/2022, 14:26
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

package pl.miloszgilga.chessappbackend.injector;

import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;

import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import pl.miloszgilga.chessappbackend.security.LocalUserRole;
import pl.miloszgilga.chessappbackend.network.auth.domain.LocalUserRoleModel;
import pl.miloszgilga.chessappbackend.network.auth.domain.ILocalUserRoleRepository;

//----------------------------------------------------------------------------------------------------------------------

@Component
class LocalUserRolesInjector implements ApplicationRunner {

    private final ILocalUserRoleRepository repository;
    private final Set<String> userRoles;

    LocalUserRolesInjector(ILocalUserRoleRepository repository) {
        this.repository = repository;
        this.userRoles = Stream.of(LocalUserRole.values())
                .map(LocalUserRole::getRoleName)
                .collect(Collectors.toSet());
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRoles.isEmpty()) return;
        for (String roleName : userRoles) {
            final LocalUserRole enumRole = LocalUserRole.getEnumRoleBaseStringName(roleName);
            if (!repository.findIfRoleExist(enumRole)) {
                repository.save(new LocalUserRoleModel(enumRole));
            }
        }
    }
}
