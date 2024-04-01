package com.sycosoft.hireout.database.repositories

import com.sycosoft.hireout.database.entities.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRoleRepository: JpaRepository<UserRole, Int> {

    fun save(userRole: UserRole): UserRole

    @Query("SELECT role FROM table_user_roles WHERE role.role_name = :roleName")
    fun getRoleByName(roleName: String): Optional<UserRole>
}