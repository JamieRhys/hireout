package com.sycosoft.hireout.database.repositories

import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.database.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.core.env.Environment
import java.util.*
import org.junit.jupiter.api.Assertions.*

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var env: Environment

    private val testUUID = UUID.randomUUID()

    @BeforeEach
    fun setUp() {
        val user = User.Builder()
            .uuid(UUID.randomUUID())
            .firstName(Strings.USER_FIRST_NAME_VALID)
            .lastName(Strings.USER_LAST_NAME_VALID)
            .username(Strings.USER_USERNAME_VALID)
            .password(Strings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        entityManager.merge(user)
    }

    class Strings {
        companion object {
            const val USER_USERNAME_VALID: String = "test.user"
            const val USER_PASSWORD_VALID: String = "password123"
            const val USER_FIRST_NAME_VALID: String = "Test"
            const val USER_LAST_NAME_VALID: String = "User"
        }
    }

    @Test
    fun givenUserObject_whenSave_thenReturnSavedUser() {
        val savedUser = userRepository.save(User.Builder()
            .firstName(Strings.USER_FIRST_NAME_VALID)
            .lastName(Strings.USER_LAST_NAME_VALID)
            .username(Strings.USER_USERNAME_VALID)
            .password(Strings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build())

        assertNotNull(savedUser)
        assertNotNull(savedUser.uuid)
    }
}