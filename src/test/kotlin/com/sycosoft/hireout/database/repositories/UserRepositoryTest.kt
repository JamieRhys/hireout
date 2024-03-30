package com.sycosoft.hireout.database.repositories

import com.sycosoft.hireout.TestStrings
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
import org.springframework.dao.DataIntegrityViolationException

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
            .uuid(testUUID)
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        entityManager.merge(user)
    }

//region Save Method tests

    @Test
    fun givenUserObject_whenSaved_thenReturnSavedUser() {
        val savedUser = userRepository.save(User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build())

        assertNotNull(savedUser)
        assertNotNull(savedUser.uuid)
        assertEquals(savedUser.firstName, TestStrings.USER_FIRST_NAME_VALID)
        assertEquals(savedUser.lastName, TestStrings.USER_LAST_NAME_VALID)
        assertEquals(savedUser.username, TestStrings.USER_USERNAME_VALID)
        assertFalse(savedUser.isDeleted)
    }

//endregion
//region Get Method Tests

    @Test
    fun givenValidUUID_whenGetting_thenReturnUserObject() {

    }

//endregion
}