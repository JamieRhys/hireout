package com.sycosoft.hireout.database.repository

import com.sycosoft.hireout.database.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun save(user: User): User

    @Query("SELECT user FROM table_users user WHERE user.name = :name")
    fun getUserByUsername(username: String): Optional<User>
}