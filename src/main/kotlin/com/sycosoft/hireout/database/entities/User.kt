package com.sycosoft.hireout.database.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

@Entity(name = "table_users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid")
    val uuid: UUID? = null,

    @Column(name = "username", unique = true)
    @NotNull(message = "Please provide a username") @NotBlank
    val username: String? = null,

    @Column(name = "password")
    @NotNull @NotBlank
    var password: String? = null,

    @Column(name = "first_name")
    @NotNull(message = "Please provide a first name.") @NotBlank
    var firstName: String? = null,

    @Column(name = "last_name")
    @NotNull @NotBlank
    var lastName: String? = null,

    @Column(name = "is_deleted")
    @NotNull
    var isDeleted: Boolean = false
) {
    constructor(builder: Builder) : this(
        uuid = builder.uuid,
        username = builder.username,
        password = builder.password,
        firstName = builder.firstName,
        lastName = builder.lastName,
        isDeleted = builder.isDeleted
    )

    data class Builder(
        var uuid: UUID? = null,
        var username: String? = null,
        var password: String? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var isDeleted: Boolean = false,
    ) {
        fun uuid(uuid: UUID) = apply { this.uuid = uuid }
        fun username(username: String) = apply { this.username = username }
        fun password(password: String) = apply { this.password = password }
        fun firstName(firstName: String) = apply { this.firstName = firstName }
        fun lastName(lastName: String) = apply { this.lastName = lastName }
        fun isDeleted(isDeleted: Boolean) = apply { this.isDeleted = isDeleted }
        fun build() = User(this)
    }
}
