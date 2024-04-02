package com.sycosoft.hireout.database.repositories

import com.sycosoft.hireout.database.entities.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRoleRepository: JpaRepository<UserRole, Int> {

    fun save(userRole: UserRole): UserRole

    @Query("SELECT userRole FROM table_user_roles userRole WHERE userRole.roleName = :roleName")
    fun getRoleByName(roleName: String): Optional<UserRole>
}