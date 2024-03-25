package com.sycosoft.hireout.database.repository

import com.sycosoft.hireout.database.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {

}