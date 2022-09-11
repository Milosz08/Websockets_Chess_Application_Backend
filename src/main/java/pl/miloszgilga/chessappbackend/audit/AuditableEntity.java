/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AuditableEntity.java
 * Last modified: 01/09/2022, 19:39
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

package pl.miloszgilga.chessappbackend.audit;

import lombok.NoArgsConstructor;

import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.io.Serializable;

import javax.persistence.*;

//----------------------------------------------------------------------------------------------------------------------

@MappedSuperclass
@NoArgsConstructor
public abstract class AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID") private long id;

    @CreationTimestamp
    @Column(name = "CREATED_AT") private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT") private Date updatedAt;

    public long getId() {
        return id;
    }

    Date getCreatedAt() {
        return createdAt;
    }

    Date getUpdatedAt() {
        return updatedAt;
    }

    public void setId(long id) {
        this.id = id;
    }

    void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AuditableEntity{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
