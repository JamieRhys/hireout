package com.sycosoft.hireout.database.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity(name = "table_user_roles")
data class UserRole(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "role_name")
    @NotNull(message = "Please provide a name for this role")
    val roleName: String? = null
) {

    constructor(builder: Builder) : this(
        id = builder.id,
        roleName = builder.roleName
    )

    data class Builder(
        var id: Int? = null,
        var roleName: String? = null
    ) {
        fun id(id: Int?) = apply { this.id = id }
        fun roleName(roleName: String?) = apply { this.roleName = roleName }
        fun build() = UserRole(this)
    }
}
