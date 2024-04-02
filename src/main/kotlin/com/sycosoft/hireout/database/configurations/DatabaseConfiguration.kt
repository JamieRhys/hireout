package com.sycosoft.hireout.database.configurations

import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.database.entities.UserRole
import com.sycosoft.hireout.services.user.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfiguration {
    companion object {
        @Bean
        fun initialiseUserRolesTable(userService: UserService): CommandLineRunner {
            return CommandLineRunner {
                if(userService.getAllUserRoles().isEmpty()) {
                    userService.saveUserRoles(listOf(
                        UserRole(roleName = "ROLE_ADMIN"),
                        UserRole(roleName = "ROLE_POWER_USER"),
                        UserRole(roleName = "ROLE_USER"),
                        UserRole(roleName = "ROLE_ACCOUNTING"),
                        UserRole(roleName = "ROLE_READ_ONLY"),
                    ))
                }
            }
        }

        @Bean
        fun initialiseUserTable(userService: UserService): CommandLineRunner {
            return CommandLineRunner {
                if(userService.getAllUsers().isEmpty()) {
                    userService.saveUser(
                        User(
                            firstName = "Admin",
                            lastName = "User",
                            username = "admin.user",
                            password = "password123"
                        )
                    )
                }
            }
        }
    }
}