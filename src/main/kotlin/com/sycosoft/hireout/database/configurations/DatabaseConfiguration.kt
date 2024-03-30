package com.sycosoft.hireout.database.configurations

import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.services.user.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfiguration {
    companion object {
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